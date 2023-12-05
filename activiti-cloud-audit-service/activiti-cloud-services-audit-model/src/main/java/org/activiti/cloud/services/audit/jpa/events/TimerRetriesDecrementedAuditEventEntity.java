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

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.activiti.cloud.api.process.model.events.CloudBPMNTimerRetriesDecrementedEvent;
import org.hibernate.annotations.DynamicInsert;

@Entity(name = TimerRetriesDecrementedAuditEventEntity.TIMER_RETRIES_DECREMENTED_EVENT)
@DiscriminatorValue(value = TimerRetriesDecrementedAuditEventEntity.TIMER_RETRIES_DECREMENTED_EVENT)
public class TimerRetriesDecrementedAuditEventEntity extends TimerAuditEventEntity {

    protected static final String TIMER_RETRIES_DECREMENTED_EVENT = "TimerRetriesDecrementedEvent";

    public TimerRetriesDecrementedAuditEventEntity() {}

    public TimerRetriesDecrementedAuditEventEntity(CloudBPMNTimerRetriesDecrementedEvent cloudEvent) {
        super(cloudEvent);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
