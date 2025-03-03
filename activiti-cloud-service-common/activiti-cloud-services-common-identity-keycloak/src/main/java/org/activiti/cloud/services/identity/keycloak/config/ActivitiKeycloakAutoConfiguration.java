/*
 * Copyright 2017-2020 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cloud.services.identity.keycloak.config;

import feign.Feign;
import org.activiti.cloud.security.feign.AuthTokenRequestInterceptor;
import org.activiti.cloud.security.feign.configuration.ClientCredentialsAuthConfiguration;
import org.activiti.cloud.services.identity.keycloak.ActivitiKeycloakProperties;
import org.activiti.cloud.services.identity.keycloak.KeycloakClientPrincipalDetailsProvider;
import org.activiti.cloud.services.identity.keycloak.KeycloakHealthService;
import org.activiti.cloud.services.identity.keycloak.KeycloakManagementService;
import org.activiti.cloud.services.identity.keycloak.KeycloakProperties;
import org.activiti.cloud.services.identity.keycloak.KeycloakUserGroupManager;
import org.activiti.cloud.services.identity.keycloak.client.KeycloakClient;
import org.activiti.cloud.services.identity.keycloak.validator.RealmValidationCheck;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@AutoConfiguration
@PropertySource("classpath:keycloak-client.properties")
@ConditionalOnProperty(
    value = "activiti.cloud.services.oauth2.iam-name",
    havingValue = "keycloak",
    matchIfMissing = true
)
@EnableConfigurationProperties({ ActivitiKeycloakProperties.class, KeycloakProperties.class })
public class ActivitiKeycloakAutoConfiguration {

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean(name = "userGroupManager")
    @ConditionalOnMissingBean(KeycloakUserGroupManager.class)
    public KeycloakUserGroupManager keycloakUserGroupManager(KeycloakClient keycloakClient) {
        return new KeycloakUserGroupManager(keycloakClient);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnMissingBean
    public KeycloakClientPrincipalDetailsProvider keycloakClientPrincipalDetailsProvider(
        KeycloakClient keycloakClient
    ) {
        return new KeycloakClientPrincipalDetailsProvider(keycloakClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeycloakManagementService identityManagementService(KeycloakClient keycloakClient) {
        return new KeycloakManagementService(keycloakClient);
    }

    @Bean(name = "identityHealthService")
    @ConditionalOnMissingBean(KeycloakHealthService.class)
    public KeycloakHealthService keycloakHealthService(KeycloakUserGroupManager keycloakUserGroupManager) {
        return new KeycloakHealthService(keycloakUserGroupManager);
    }

    @Bean
    public RealmValidationCheck realmValidationCheck(
        @Value("${keycloak.auth-server-url}") String authServerUrl,
        @Value("${keycloak.realm}") String realm
    ) {
        return new RealmValidationCheck(authServerUrl, realm);
    }

    @Bean
    public KeycloakClient keycloakClient(
        @Value("${keycloak.auth-server-url}/admin/realms/${keycloak.realm}/") String url,
        ObjectFactory<HttpMessageConverters> messageConverters,
        ObjectProvider<HttpMessageConverterCustomizer> customizers
    ) {
        ClientCredentialsAuthConfiguration clientCredentialsAuthConfiguration = new ClientCredentialsAuthConfiguration();
        ClientRegistration clientRegistration = clientCredentialsAuthConfiguration.clientRegistration(
            clientRegistrationRepository,
            "keycloak"
        );
        AuthTokenRequestInterceptor clientCredentialsAuthRequestInterceptor = clientCredentialsAuthConfiguration.clientCredentialsAuthRequestInterceptor(
            oAuth2AuthorizedClientService,
            clientRegistrationRepository,
            clientRegistration
        );
        return Feign
            .builder()
            .contract(new SpringMvcContract())
            .encoder(new SpringEncoder(messageConverters))
            .decoder(new SpringDecoder(messageConverters, customizers))
            .requestInterceptor(clientCredentialsAuthRequestInterceptor)
            .target(KeycloakClient.class, url);
    }
}
