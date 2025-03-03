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

package org.activiti.cloud.services.gateway;

import org.activiti.api.model.shared.EmptyResult;
import org.activiti.api.process.model.payloads.DeleteProcessPayload;
import org.activiti.api.process.model.payloads.ReceiveMessagePayload;
import org.activiti.api.process.model.payloads.RemoveProcessVariablesPayload;
import org.activiti.api.process.model.payloads.ResumeProcessPayload;
import org.activiti.api.process.model.payloads.SetProcessVariablesPayload;
import org.activiti.api.process.model.payloads.SignalPayload;
import org.activiti.api.process.model.payloads.StartMessagePayload;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.api.process.model.payloads.SuspendProcessPayload;
import org.activiti.api.process.model.results.ProcessInstanceResult;
import org.activiti.api.task.model.payloads.ClaimTaskPayload;
import org.activiti.api.task.model.payloads.CompleteTaskPayload;
import org.activiti.api.task.model.payloads.CreateTaskVariablePayload;
import org.activiti.api.task.model.payloads.ReleaseTaskPayload;
import org.activiti.api.task.model.payloads.UpdateTaskVariablePayload;
import org.activiti.api.task.model.results.TaskResult;
import org.activiti.cloud.api.process.model.impl.SyncCloudProcessDefinitionsPayload;
import org.activiti.cloud.api.process.model.impl.SyncCloudProcessDefinitionsResult;

public interface ProcessRuntimeGateway {
    ProcessInstanceResult startProcess(StartProcessPayload startProcessPayload);

    ProcessInstanceResult suspendProcess(SuspendProcessPayload payload);

    ProcessInstanceResult resumeProcess(ResumeProcessPayload payload);

    EmptyResult setProcessVariables(SetProcessVariablesPayload setProcessVariablesPayload);

    EmptyResult deleteProcess(DeleteProcessPayload payload);

    EmptyResult removeProcessVariables(RemoveProcessVariablesPayload payload);

    TaskResult completeTask(CompleteTaskPayload payload);

    TaskResult claimTask(ClaimTaskPayload payload);

    TaskResult releaseTask(ReleaseTaskPayload releaseTaskPayload);

    EmptyResult createTaskVariable(CreateTaskVariablePayload payload);

    EmptyResult updateTaskVariable(UpdateTaskVariablePayload payload);

    ProcessInstanceResult startMessage(StartMessagePayload payload);

    EmptyResult receiveMessage(ReceiveMessagePayload payload);

    EmptyResult sendSignal(SignalPayload payload);

    SyncCloudProcessDefinitionsResult syncProcessDefinitions(SyncCloudProcessDefinitionsPayload payload);
}
