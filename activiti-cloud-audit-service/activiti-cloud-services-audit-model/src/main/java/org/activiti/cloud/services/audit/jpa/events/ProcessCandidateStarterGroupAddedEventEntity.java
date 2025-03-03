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
package org.activiti.cloud.services.audit.jpa.events;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.activiti.api.process.model.ProcessCandidateStarterGroup;
import org.activiti.api.runtime.model.impl.ProcessCandidateStarterGroupImpl;
import org.activiti.cloud.api.process.model.events.CloudProcessCandidateStarterGroupAddedEvent;
import org.activiti.cloud.services.audit.jpa.converters.json.ProcessCandidateStarterGroupJpaJsonConverter;

@Entity(name = ProcessCandidateStarterGroupAddedEventEntity.PROCESS_CANDIDATE_STARTER_GROUP_ADDED_EVENT)
@DiscriminatorValue(value = ProcessCandidateStarterGroupAddedEventEntity.PROCESS_CANDIDATE_STARTER_GROUP_ADDED_EVENT)
public class ProcessCandidateStarterGroupAddedEventEntity extends AuditEventEntity {

    protected static final String PROCESS_CANDIDATE_STARTER_GROUP_ADDED_EVENT =
        "ProcessCandidateStarterGroupAddedEvent";

    @Convert(converter = ProcessCandidateStarterGroupJpaJsonConverter.class)
    @Column(columnDefinition = "text")
    private ProcessCandidateStarterGroupImpl candidateStarterGroup;

    public ProcessCandidateStarterGroupAddedEventEntity() {}

    public ProcessCandidateStarterGroupAddedEventEntity(CloudProcessCandidateStarterGroupAddedEvent cloudEvent) {
        super(cloudEvent);
        setCandidateStarterGroup(cloudEvent.getEntity());
    }

    public ProcessCandidateStarterGroup getCandidateStarterGroup() {
        return candidateStarterGroup;
    }

    public void setCandidateStarterGroup(ProcessCandidateStarterGroup candidateGroup) {
        this.candidateStarterGroup =
            new ProcessCandidateStarterGroupImpl(candidateGroup.getProcessDefinitionId(), candidateGroup.getGroupId());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
            .append("ProcessCandidateStarterGroupAddedEventEntity [candidateStarterGroup=")
            .append(candidateStarterGroup)
            .append(", toString()=")
            .append(super.toString())
            .append("]");
        return builder.toString();
    }
}
