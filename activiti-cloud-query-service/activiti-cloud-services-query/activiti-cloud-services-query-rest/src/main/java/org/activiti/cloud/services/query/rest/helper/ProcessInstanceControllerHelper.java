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
package org.activiti.cloud.services.query.rest.helper;

import com.querydsl.core.types.Predicate;
import java.util.List;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.model.ProcessInstanceEntity;
import org.activiti.cloud.services.query.rest.ProcessInstanceService;
import org.activiti.cloud.services.query.rest.payload.ProcessInstanceSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ProcessInstanceControllerHelper {

    private final ProcessInstanceRepository processInstanceRepository;
    private final ProcessInstanceService processInstanceService;

    public ProcessInstanceControllerHelper(
        ProcessInstanceRepository processInstanceRepository,
        ProcessInstanceService processInstanceService
    ) {
        this.processInstanceRepository = processInstanceRepository;
        this.processInstanceService = processInstanceService;
    }

    public Page<ProcessInstanceEntity> findAllProcessInstances(Predicate predicate, Pageable pageable) {
        Page<ProcessInstanceEntity> processInstances = processInstanceService.findAll(predicate, pageable);
        return mapAllSubprocesses(processInstances, pageable);
    }

    public Page<ProcessInstanceEntity> findAllProcessInstancesWithVariables(
        Predicate predicate,
        List<String> variableKeys,
        Pageable pageable
    ) {
        Page<ProcessInstanceEntity> processInstances = processInstanceService.findAllWithVariables(
            predicate,
            variableKeys,
            pageable
        );
        return mapAllSubprocesses(processInstances, pageable);
    }

    public ProcessInstanceEntity findById(String processInstanceId) {
        ProcessInstanceEntity processInstance = processInstanceService.findById(processInstanceId);
        return processInstanceRepository.mapSubprocesses(processInstance);
    }

    public Page<ProcessInstanceEntity> searchProcessInstances(
        ProcessInstanceSearchRequest searchRequest,
        Pageable pageable
    ) {
        Page<ProcessInstanceEntity> processInstances = processInstanceService.search(searchRequest, pageable);
        return mapAllSubprocesses(processInstances, pageable);
    }

    public Page<ProcessInstanceEntity> searchSubprocesses(
        String processInstanceId,
        Predicate predicate,
        Pageable pageable
    ) {
        Page<ProcessInstanceEntity> processInstanceSubprocesses = processInstanceService.subprocesses(
            processInstanceId,
            predicate,
            pageable
        );
        return mapAllSubprocesses(processInstanceSubprocesses, pageable);
    }

    public Page<ProcessInstanceEntity> mapAllSubprocesses(
        Page<ProcessInstanceEntity> processInstances,
        Pageable pageable
    ) {
        return processInstanceRepository.mapSubprocesses(processInstances, pageable);
    }
}
