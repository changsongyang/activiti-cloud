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
package org.activiti.cloud.acc.core.steps.query;

import static org.activiti.cloud.acc.core.assertions.RestErrorAssert.assertThatRestNotFoundErrorIsThrownBy;
import static org.activiti.cloud.services.common.util.ImageUtils.svgToPng;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.awaitility.Awaitility.await;

import java.util.Collection;
import java.util.List;
import net.thucydides.core.annotations.Step;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.cloud.acc.core.rest.feign.EnableRuntimeFeignContext;
import org.activiti.cloud.acc.core.services.query.ProcessModelQueryService;
import org.activiti.cloud.acc.core.services.query.ProcessQueryDiagramService;
import org.activiti.cloud.acc.core.services.query.ProcessQueryService;
import org.activiti.cloud.acc.shared.service.BaseService;
import org.activiti.cloud.api.model.shared.CloudVariableInstance;
import org.activiti.cloud.api.process.model.CloudProcessInstance;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedModel;

@EnableRuntimeFeignContext
public class ProcessQuerySteps {

    @Autowired
    private ProcessQueryService processQueryService;

    @Autowired
    private ProcessModelQueryService processModelQueryService;

    @Autowired
    private ProcessQueryDiagramService processQueryDiagramService;

    @Autowired
    @Qualifier("queryBaseService")
    private BaseService baseService;

    @Step
    public void checkServicesHealth() {
        assertThat(baseService.isServiceUp()).isTrue();
    }

    @Step
    public CloudProcessInstance getProcessInstance(String processInstanceId) {
        await()
            .untilAsserted(() ->
                assertThat(catchThrowable(() -> processQueryService.getProcessInstance(processInstanceId))).isNull()
            );
        return processQueryService.getProcessInstance(processInstanceId);
    }

    @Step
    public PagedModel<CloudProcessInstance> getAllProcessInstances() {
        return processQueryService.getProcessInstances();
    }

    @Step
    public void checkProcessInstanceStatus(
        String processInstanceId,
        ProcessInstance.ProcessInstanceStatus expectedStatus
    ) {
        assertThat(expectedStatus).isNotNull();

        await()
            .untilAsserted(() -> {
                CloudProcessInstance processInstance = getProcessInstance(processInstanceId);
                assertThat(processInstance).isNotNull();
                assertThat(processInstance.getStatus()).isEqualTo(expectedStatus);
                assertThat(processInstance.getServiceName()).isNotEmpty();
                assertThat(processInstance.getServiceFullName()).isNotEmpty();
            });
    }

    @Step
    public void checkProcessInstanceHasVariable(String processInstanceId, String variableName) {
        await()
            .untilAsserted(() -> {
                assertThat(variableName).isNotNull();
                final Collection<CloudVariableInstance> variableInstances = processQueryService
                    .getProcessInstanceVariables(processInstanceId)
                    .getContent();
                assertThat(variableInstances).isNotNull();
                assertThat(variableInstances).isNotEmpty();
                //one of the variables should have name matching variableName
                assertThat(variableInstances).extracting(VariableInstance::getName).contains(variableName);
            });
    }

    @Step
    public void checkProcessInstanceHasVariableValue(
        String processInstanceId,
        String variableName,
        Object variableValue
    ) {
        await()
            .untilAsserted(() -> {
                assertThat(variableName).isNotNull();
                final Collection<CloudVariableInstance> variableInstances = processQueryService
                    .getProcessInstanceVariables(processInstanceId)
                    .getContent();
                assertThat(variableInstances).isNotNull();
                assertThat(variableInstances).isNotEmpty();
                //one of the variables should have name matching variableName and value

                assertThat(variableInstances).extracting(VariableInstance::getName).contains(variableName);

                assertThat(
                    variableInstances
                        .stream()
                        .filter(it -> it.getName().equals(variableName))
                        .map(CloudVariableInstance::getValue)
                        .map(value -> value instanceof List<?> listOfValues ? listOfValues : List.of(value))
                        .findFirst()
                )
                    .isNotEmpty()
                    .get()
                    .asInstanceOf(InstanceOfAssertFactories.LIST)
                    .containsExactlyInAnyOrderElementsOf(
                        variableValue instanceof List<?> variableValues ? variableValues : List.of(variableValue)
                    );
            });
    }

    @Step
    public void checkProcessInstanceName(String processInstanceId, String processInstanceName) {
        await()
            .untilAsserted(() ->
                assertThat(processQueryService.getProcessInstance(processInstanceId).getName())
                    .isNotNull()
                    .isEqualTo(processInstanceName)
            );
    }

    @Step
    public PagedModel<ProcessDefinition> getProcessDefinitions() {
        return processQueryService.getProcessDefinitions();
    }

    @Step
    public String getProcessModel(String processDefinitionId) {
        return processModelQueryService.getProcessModel(processDefinitionId);
    }

    @Step
    public PagedModel<CloudProcessInstance> getProcessInstancesByName(String processName) {
        return processQueryService.getProcessInstancesByName(processName);
    }

    @Step
    public PagedModel<CloudProcessInstance> getProcessInstancesByProcessDefinitionKey(String processDefinitionKey) {
        return processQueryService.getProcessInstancesByProcessDefinitionKey(processDefinitionKey);
    }

    @Step
    public String getProcessInstanceDiagram(String id) {
        await()
            .untilAsserted(() ->
                assertThat(catchThrowable(() -> processQueryDiagramService.getProcessInstanceDiagram(id))).isNull()
            );
        return processQueryDiagramService.getProcessInstanceDiagram(id);
    }

    @Step
    public void checkProcessInstanceDiagram(String diagram) throws Exception {
        assertThat(diagram).isNotEmpty();
        assertThat(svgToPng(diagram.getBytes())).isNotEmpty();
    }

    @Step
    public void checkProcessInstanceNoDiagram(String diagram) {
        assertThat(diagram).isEmpty();
    }

    @Step
    public void checkProcessInstanceNotFound(String processInstanceId) {
        await()
            .pollInSameThread()
            .untilAsserted(() ->
                assertThatRestNotFoundErrorIsThrownBy(() -> processQueryService.getProcessInstance(processInstanceId))
            );
    }
}
