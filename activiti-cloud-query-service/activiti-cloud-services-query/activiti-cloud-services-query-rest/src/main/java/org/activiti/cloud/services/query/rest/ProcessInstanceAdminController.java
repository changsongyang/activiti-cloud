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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedModelAssembler;
import org.activiti.cloud.api.process.model.QueryCloudProcessInstance;
import org.activiti.cloud.services.query.model.JsonViews;
import org.activiti.cloud.services.query.model.ProcessInstanceEntity;
import org.activiti.cloud.services.query.rest.assembler.ProcessInstanceRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.helper.ProcessInstanceAdminControllerHelper;
import org.activiti.cloud.services.query.rest.payload.ProcessInstanceQueryBody;
import org.activiti.cloud.services.query.rest.payload.ProcessInstanceSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/admin/v1/process-instances",
    produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }
)
public class ProcessInstanceAdminController {

    private final ProcessInstanceAdminService processInstanceAdminService;

    private final ProcessInstanceRepresentationModelAssembler processInstanceRepresentationModelAssembler;

    private final AlfrescoPagedModelAssembler<ProcessInstanceEntity> pagedCollectionModelAssembler;

    private final ProcessInstanceAdminControllerHelper processInstanceAdminControllerHelper;

    @Autowired
    public ProcessInstanceAdminController(
        ProcessInstanceAdminService processInstanceAdminService,
        ProcessInstanceRepresentationModelAssembler processInstanceRepresentationModelAssembler,
        AlfrescoPagedModelAssembler<ProcessInstanceEntity> pagedCollectionModelAssembler,
        ProcessInstanceAdminControllerHelper processInstanceAdminControllerHelper
    ) {
        this.processInstanceAdminService = processInstanceAdminService;
        this.processInstanceRepresentationModelAssembler = processInstanceRepresentationModelAssembler;
        this.pagedCollectionModelAssembler = pagedCollectionModelAssembler;
        this.processInstanceAdminControllerHelper = processInstanceAdminControllerHelper;
    }

    @Operation(summary = "Find process instances", hidden = true)
    @JsonView(JsonViews.General.class)
    @RequestMapping(method = RequestMethod.GET, params = "!variableKeys")
    public PagedModel<EntityModel<QueryCloudProcessInstance>> findAllProcessInstanceAdmin(
        @Parameter(description = PREDICATE_DESC, example = PREDICATE_EXAMPLE) @QuerydslPredicate(
            root = ProcessInstanceEntity.class
        ) Predicate predicate,
        Pageable pageable
    ) {
        return pagedCollectionModelAssembler.toModel(
            pageable,
            processInstanceAdminControllerHelper.findAllProcessInstanceAdmin(predicate, pageable),
            processInstanceRepresentationModelAssembler
        );
    }

    @Operation(summary = "Find process instances")
    @JsonView(JsonViews.ProcessVariables.class)
    @RequestMapping(method = RequestMethod.GET, params = "variableKeys")
    public PagedModel<EntityModel<QueryCloudProcessInstance>> findAllWithVariablesAdmin(
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
            processInstanceAdminControllerHelper.findAllProcessInstanceAdminWithVariables(
                predicate,
                variableKeys,
                pageable
            ),
            processInstanceRepresentationModelAssembler
        );
    }

    @RequestMapping(method = RequestMethod.POST)
    public MappingJacksonValue findAllFromBodyProcessAdmin(
        @Parameter(description = PREDICATE_DESC, example = PREDICATE_EXAMPLE) @QuerydslPredicate(
            root = ProcessInstanceEntity.class
        ) Predicate predicate,
        @RequestBody(required = false) ProcessInstanceQueryBody payload,
        Pageable pageable
    ) {
        ProcessInstanceQueryBody queryBody = Optional.ofNullable(payload).orElse(new ProcessInstanceQueryBody());

        PagedModel<EntityModel<QueryCloudProcessInstance>> pagedModel = pagedCollectionModelAssembler.toModel(
            pageable,
            processInstanceAdminService.findAllFromBody(
                predicate,
                queryBody.getVariableKeys(),
                Collections.emptyList(),
                pageable
            ),
            processInstanceRepresentationModelAssembler
        );

        MappingJacksonValue result = new MappingJacksonValue(pagedModel);
        if (queryBody.hasVariableKeys()) {
            result.setSerializationView(JsonViews.ProcessVariables.class);
        } else {
            result.setSerializationView(JsonViews.General.class);
        }

        return result;
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
            processInstanceAdminService.search(searchRequest, pageable),
            processInstanceRepresentationModelAssembler
        );
    }

    @JsonView(JsonViews.General.class)
    @RequestMapping(value = "/{processInstanceId}", method = RequestMethod.GET)
    public EntityModel<QueryCloudProcessInstance> findByIdProcessAdmin(@PathVariable String processInstanceId) {
        return processInstanceRepresentationModelAssembler.toModel(
            processInstanceAdminControllerHelper.findByIdProcessAdmin(processInstanceId)
        );
    }

    @Operation(summary = "Find application versions for process instances")
    @RequestMapping(value = "/appVersions", method = RequestMethod.GET)
    public Set<String> findProcessAppVersions(
        @Parameter(description = PREDICATE_DESC, example = PREDICATE_EXAMPLE) @QuerydslPredicate(
            root = ProcessInstanceEntity.class
        ) Predicate predicate
    ) {
        return processInstanceAdminService.findAllAppVersions(predicate);
    }
}
