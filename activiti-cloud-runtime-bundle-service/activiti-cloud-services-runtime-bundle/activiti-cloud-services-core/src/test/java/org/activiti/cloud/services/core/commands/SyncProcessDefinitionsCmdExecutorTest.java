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
package org.activiti.cloud.services.core.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.activiti.cloud.api.process.model.impl.SyncCloudProcessDefinitionsPayload;
import org.activiti.cloud.services.core.ProcessDefinitionsSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SyncProcessDefinitionsCmdExecutorTest {

    @InjectMocks
    private SyncProcessDefinitionsCmdExecutor subject;

    @Mock
    private ProcessDefinitionsSyncService processDefinitionsSyncService;

    @Test
    public void syncProcessDefinitionsCmdExecutorTest() {
        SyncCloudProcessDefinitionsPayload payload = SyncCloudProcessDefinitionsPayload
            .builder()
            .processDefinitionKeys(List.of("key1", "key2"))
            .excludedProcessDefinitionIds(List.of("1", "2", "3"))
            .build();

        assertThat(subject.getHandledType()).isEqualTo(SyncCloudProcessDefinitionsPayload.class.getName());

        subject.execute(payload);

        verify(processDefinitionsSyncService).syncProcessDefinitions(payload);
    }
}
