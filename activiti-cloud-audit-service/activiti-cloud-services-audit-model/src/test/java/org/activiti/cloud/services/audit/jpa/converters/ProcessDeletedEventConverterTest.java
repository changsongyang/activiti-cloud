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
package org.activiti.cloud.services.audit.jpa.converters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.events.ProcessRuntimeEvent;
import org.activiti.api.runtime.model.impl.ProcessInstanceImpl;
import org.activiti.cloud.api.model.shared.events.CloudRuntimeEvent;
import org.activiti.cloud.api.model.shared.impl.events.CloudRuntimeEventImpl;
import org.activiti.cloud.api.process.model.impl.events.CloudProcessDeletedEventImpl;
import org.activiti.cloud.services.audit.jpa.events.AuditEventEntity;
import org.activiti.cloud.services.audit.jpa.events.ProcessCompletedEventEntity;
import org.activiti.cloud.services.audit.jpa.events.ProcessDeletedAuditEventEntity;
import org.activiti.cloud.services.audit.jpa.repository.EventsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProcessDeletedEventConverterTest {

    private ProcessDeletedEventConverter converter;

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private EventContextInfoAppender eventContextInfoAppender;

    @BeforeEach
    public void setUp() {
        converter = new ProcessDeletedEventConverter(eventsRepository, eventContextInfoAppender);
    }

    @Test
    public void getSupportedEventShouldReturnProcessDeleted() {
        assert (converter.getSupportedEvent()).equals(ProcessRuntimeEvent.ProcessEvents.PROCESS_DELETED.name());
    }

    @Test
    public void createEventEntityShouldReturnEntity() {
        //given
        String processDefinitionId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        CloudRuntimeEvent<?, ?> runtimeEvent = buildEvent(processInstanceId, processDefinitionId);

        //when
        ProcessDeletedAuditEventEntity event = converter.createEventEntity(runtimeEvent);

        //then
        assertThat(event.getProcessInstanceId()).isEqualTo(runtimeEvent.getProcessInstanceId());
        assertThat(event.getProcessDefinitionId()).isEqualTo(processDefinitionId);
    }

    @Test
    public void createAPIEventShouldReturnEvent() {
        //given
        String processDefinitionId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        CloudRuntimeEvent<?, ?> runtimeEvent = buildEvent(processInstanceId, processDefinitionId);

        //when
        ProcessDeletedAuditEventEntity event = converter.createEventEntity(runtimeEvent);
        CloudRuntimeEventImpl<?, ?> apiEvent = converter.createAPIEvent(event);

        //then
        assertThat(apiEvent.getProcessDefinitionId()).isEqualTo(processDefinitionId);
        assertThat(apiEvent.getProcessInstanceId()).isEqualTo(processInstanceId);
    }

    private CloudRuntimeEvent<?, ?> buildEvent(String processInstanceId, String processDefinitionId) {
        ProcessInstanceImpl instance = new ProcessInstanceImpl();
        instance.setId(processInstanceId);
        instance.setProcessDefinitionId(processDefinitionId);

        CloudProcessDeletedEventImpl event = new CloudProcessDeletedEventImpl(instance);
        event.setProcessInstanceId(processInstanceId);
        event.setSequenceNumber(1);
        return event;
    }

    private ProcessInstance buildProcessInstance(String processInstanceId, String processDefinitionId) {
        ProcessInstanceImpl instance = new ProcessInstanceImpl();
        instance.setId(processInstanceId);
        instance.setProcessDefinitionId(processDefinitionId);
        return instance;
    }

    private List<? extends AuditEventEntity> buildCompletedEntities(
        String processDefinitionId,
        String processInstanceId
    ) {
        ProcessCompletedEventEntity event = new ProcessCompletedEventEntity();
        event.setProcessDefinitionId(processDefinitionId);
        event.setEventType("PROCESS_COMPLETED");
        event.setProcessInstanceId(processInstanceId);
        event.setProcessInstance(buildProcessInstance(processInstanceId, processDefinitionId));
        return Arrays.asList(event);
    }
}
