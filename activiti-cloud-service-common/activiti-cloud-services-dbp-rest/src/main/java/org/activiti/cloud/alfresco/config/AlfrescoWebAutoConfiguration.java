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

package org.activiti.cloud.alfresco.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.activiti.cloud.alfresco.argument.resolver.AlfrescoPageArgumentMethodResolver;
import org.activiti.cloud.alfresco.argument.resolver.AlfrescoPageParameterParser;
import org.activiti.cloud.alfresco.converter.json.AlfrescoJackson2HttpMessageConverter;
import org.activiti.cloud.alfresco.converter.json.PageMetadataConverter;
import org.activiti.cloud.alfresco.converter.json.PagedModelConverter;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedModelAssembler;
import org.activiti.cloud.alfresco.data.domain.ExtendedPageMetadataConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UriComponents;

@AutoConfiguration
@PropertySource("classpath:config/alfresco-rest-config.properties")
public class AlfrescoWebAutoConfiguration implements WebMvcConfigurer {

    private final PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;
    private final int defaultPageSize;
    private final int maxItemsLimit;
    private final boolean maxItemsLimitEnabled;

    public AlfrescoWebAutoConfiguration(
        @Lazy PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver,
        @Value("${spring.data.rest.default-page-size:100}") int defaultPageSize,
        @Value("${activiti.cloud.rest.max-items}") int maxItemsLimit,
        @Value("${activiti.cloud.rest.max-items.enabled}") boolean maxItemsLimitEnabled
    ) {
        this.pageableHandlerMethodArgumentResolver = pageableHandlerMethodArgumentResolver;
        this.defaultPageSize = defaultPageSize;
        this.maxItemsLimit = maxItemsLimit;
        this.maxItemsLimitEnabled = maxItemsLimitEnabled;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(
            0,
            new AlfrescoPageArgumentMethodResolver(
                new AlfrescoPageParameterParser(defaultPageSize),
                pageableHandlerMethodArgumentResolver,
                maxItemsLimit,
                maxItemsLimitEnabled
            )
        );
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //the property spring.hateoas.use-hal-as-default-json-media-type is not working
        //we need to manually remove application/json from supported mediaTypes
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof TypeConstrainedMappingJackson2HttpMessageConverter) {
                ArrayList<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
                mediaTypes.remove(MediaType.APPLICATION_JSON);
                ((TypeConstrainedMappingJackson2HttpMessageConverter) converter).setSupportedMediaTypes(mediaTypes);
            }
        }
    }

    @Bean
    public <T> AlfrescoJackson2HttpMessageConverter<T> alfrescoJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new AlfrescoJackson2HttpMessageConverter<>(
            new PagedModelConverter(new PageMetadataConverter()),
            objectMapper
        );
    }

    @Bean
    public ExtendedPageMetadataConverter extendedPageMetadataConverter() {
        return new ExtendedPageMetadataConverter();
    }

    @Bean
    public <T> AlfrescoPagedModelAssembler<T> alfrescoPagedModelAssembler(
        @Autowired(required = false) HateoasPageableHandlerMethodArgumentResolver resolver,
        @Autowired(required = false) UriComponents baseUri,
        ExtendedPageMetadataConverter extendedPageMetadataConverter
    ) {
        return new AlfrescoPagedModelAssembler<>(resolver, baseUri, extendedPageMetadataConverter);
    }

    @Bean
    public InitializingBean configureObjectMapperForBigDecimal(ObjectMapper objectMapper) {
        /*
        This will ensure that BigDecimals are serialized as String and not as a number, meaning
        that double quotes will be added around the value. Serializing it as a number it's problematic
        because, by default, it will be deserialized back to Java as double and it will loose precision.
        For instance, `1.00` (scale 2) will become `1.0` (scale 1) that are considered as different
        values in BigDecimal. By adding the quotes, it will be deserialized as String, but it will not
        loose the information about the scale, so it can be easily converted back to BigDecimal.
         */

        return () ->
            objectMapper.configOverride(BigDecimal.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
    }
}
