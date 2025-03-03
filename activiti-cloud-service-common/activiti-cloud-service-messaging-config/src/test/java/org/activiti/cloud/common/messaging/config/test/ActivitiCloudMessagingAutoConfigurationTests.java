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

package org.activiti.cloud.common.messaging.config.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.activiti.cloud.common.messaging.ActivitiCloudMessagingProperties;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.util.LinkedCaseInsensitiveMap;

@SpringBootTest
@SpringBootApplication
public class ActivitiCloudMessagingAutoConfigurationTests {

    @Autowired
    private ActivitiCloudMessagingProperties messagingProperties;

    @Autowired(required = false)
    private ListenerContainerCustomizer<MessageListenerContainer> activitiRabbitMqMessageListenerContainerCustomizer;

    @Test
    public void contextLoads() {
        assertThat(messagingProperties.getDestinations()).isInstanceOf(LinkedCaseInsensitiveMap.class);
    }

    @Test
    public void rabbitMqConfiguration() {
        assertThat(messagingProperties.getRabbitmq().getMissingAnonymousQueuesFatal()).isTrue();

        assertThat(activitiRabbitMqMessageListenerContainerCustomizer).isNotNull();
    }
}
