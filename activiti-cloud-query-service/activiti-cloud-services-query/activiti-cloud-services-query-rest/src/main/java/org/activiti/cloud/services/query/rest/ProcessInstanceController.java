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
package org.activiti.cloud.services.query.rest;

import static org.activiti.cloud.services.query.rest.RestDocConstants.PREDICATE_DESC;
import static org.activiti.cloud.services.query.rest.RestDocConstants.PREDICATE_EXAMPLE;
import static org.activiti.cloud.services.query.rest.RestDocConstants.VARIABLE_KEYS_DESC;
import static org.activiti.cloud.services.query.rest.RestDocConstants.VARIABLE_KEYS_EXAMPLE;

import com.fasterxml.jackson.annotation.JsonView;
import com.querydsl.core.types.Predicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedModelAssembler;
import org.activiti.cloud.api.process.model.QueryCloudProcessInstance;
import org.activiti.cloud.services.query.model.JsonViews;
import org.activiti.cloud.services.query.model.ProcessInstanceEntity;
import org.activiti.cloud.services.query.rest.assembler.ProcessInstanceRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.helper.ProcessInstanceControllerHelper;
import org.activiti.cloud.services.query.rest.payload.ProcessInstanceSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/v1/process-instances",
    produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }
)
public class ProcessInstanceController {

    private final ProcessInstanceRepresentationModelAssembler processInstanceRepresentationModelAssembler;

    private final AlfrescoPagedModelAssembler<ProcessInstanceEntity> pagedCollectionModelAssembler;

    private final ProcessInstanceControllerHelper processInstanceControllerHelper;

    @Autowired
    public ProcessInstanceController(
        ProcessInstanceRepresentationModelAssembler processInstanceRepresentationModelAssembler,
        AlfrescoPagedModelAssembler<ProcessInstanceEntity> pagedCollectionModelAssembler,
        ProcessInstanceControllerHelper processInstanceControllerHelper
    ) {
        this.processInstanceRepresentationModelAssembler = processInstanceRepresentationModelAssembler;
        this.pagedCollectionModelAssembler = pagedCollectionModelAssembler;
        this.processInstanceControllerHelper = processInstanceControllerHelper;
    }

    @Operation(summary = "Find process instances", hidden = true)
    @JsonView(JsonViews.General.class)
    @RequestMapping(method = RequestMethod.GET, params = "!variableKeys")
    public PagedModel<EntityModel<QueryCloudProcessInstance>> findAllProcessInstances(
        @Parameter(description = PREDICATE_DESC, example = PREDICATE_EXAMPLE) @QuerydslPredicate(
            root = ProcessInstanceEntity.class
        ) Predicate predicate,
        Pageable pageable
    ) {
        return pagedCollectionModelAssembler.toModel(
            pageable,
            processInstanceControllerHelper.findAllProcessInstances(predicate, pageable),
            processInstanceRepresentationModelAssembler
        );
    }

    @Operation(summary = "Find process instances")
    @JsonView(JsonViews.ProcessVariables.class)
    @RequestMapping(method = RequestMethod.GET, params = "variableKeys")
    public PagedModel<EntityModel<QueryCloudProcessInstance>> findAllWithVariables(
        @Parameter(description = PREDICATE_DESC, example = PREDICATE_EXAMPLE) @QuerydslPredicate(
            root = ProcessInstanceEntity.class
        ) Predicate predicate,
        @Parameter(description = VARIABLE_KEYS_DESC, example = VARIABLE_KEYS_EXAMPLE) @RequestParam(
            value = "variableKeys",
            required = false,
            defaultValue = ""
        ) List<String> variableKeys,
        Pageable pageable
    ) {
        return pagedCollectionModelAssembler.toModel(
            pageable,
            processInstanceControllerHelper.findAllProcessInstancesWithVariables(predicate, variableKeys, pageable),
            processInstanceRepresentationModelAssembler
        );
    }

    @Operation(summary = "Search process instances")
    @JsonView(JsonViews.ProcessVariables.class)
    @PostMapping("/search")
    public PagedModel<EntityModel<QueryCloudProcessInstance>> searchProcessInstances(
        @RequestBody ProcessInstanceSearchRequest searchRequest,
        Pageable pageable
    ) {
        return pagedCollectionModelAssembler.toModel(
            pageable,
            processInstanceControllerHelper.searchProcessInstances(searchRequest, pageable),
            processInstanceRepresentationModelAssembler
        );
    }

    @JsonView(JsonViews.General.class)
    @RequestMapping(value = "/{processInstanceId}", method = RequestMethod.GET)
    public EntityModel<QueryCloudProcessInstance> findByIdProcess(@PathVariable String processInstanceId) {
        return processInstanceRepresentationModelAssembler.toModel(
            processInstanceControllerHelper.findById(processInstanceId)
        );
    }

    @JsonView(JsonViews.General.class)
    @RequestMapping(value = "/{processInstanceId}/subprocesses", method = RequestMethod.GET)
    public PagedModel<EntityModel<QueryCloudProcessInstance>> subprocesses(
        @PathVariable String processInstanceId,
        @Parameter(description = PREDICATE_DESC, example = PREDICATE_EXAMPLE) @QuerydslPredicate(
            root = ProcessInstanceEntity.class
        ) Predicate predicate,
        Pageable pageable
    ) {
        return pagedCollectionModelAssembler.toModel(
            pageable,
            processInstanceControllerHelper.searchSubprocesses(processInstanceId, predicate, pageable),
            processInstanceRepresentationModelAssembler
        );
    }
}
