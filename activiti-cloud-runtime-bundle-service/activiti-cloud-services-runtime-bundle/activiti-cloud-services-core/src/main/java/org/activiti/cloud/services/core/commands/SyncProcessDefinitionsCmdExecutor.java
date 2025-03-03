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

import org.activiti.cloud.api.process.model.impl.SyncCloudProcessDefinitionsPayload;
import org.activiti.cloud.api.process.model.impl.SyncCloudProcessDefinitionsResult;
import org.activiti.cloud.services.core.ProcessDefinitionsSyncService;

public class SyncProcessDefinitionsCmdExecutor extends AbstractCommandExecutor<SyncCloudProcessDefinitionsPayload> {

    private final ProcessDefinitionsSyncService processDefinitionsSyncService;

    public SyncProcessDefinitionsCmdExecutor(ProcessDefinitionsSyncService processDefinitionsSyncService) {
        this.processDefinitionsSyncService = processDefinitionsSyncService;
    }

    @Override
    public SyncCloudProcessDefinitionsResult execute(SyncCloudProcessDefinitionsPayload payload) {
        var result = processDefinitionsSyncService.syncProcessDefinitions(payload);

        return new SyncCloudProcessDefinitionsResult(payload, result);
    }
}
