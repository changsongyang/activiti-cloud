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

package org.activiti.services.connectors.conf;

import org.activiti.engine.RepositoryService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.stream.config.BinderFactoryAutoConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = BinderFactoryAutoConfiguration.class)
@Import(ProcessEngineIntegrationChannelsConfiguration.class)
public class CloudConnectorsMessagingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConnectorImplementationsProvider connectorDestinationsProvider(RepositoryService repositoryService) {
        return new RepositoryConnectorImplementationsProvider(repositoryService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectorDestinationMappingStrategy destinationMappingStrategy() {
        return new ConnectorDestinationMappingStrategy() {};
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectorMessagingDestinationsConfigurer connectorMessagingDestinationsConfigurer(
        BindingServiceProperties bindingServiceProperties,
        ConnectorImplementationsProvider destinationsProvider,
        ConnectorDestinationMappingStrategy destinationMappingStrategy
    ) {
        return new ConnectorMessagingDestinationsConfigurer(
            destinationsProvider,
            destinationMappingStrategy,
            bindingServiceProperties
        );
    }
}
