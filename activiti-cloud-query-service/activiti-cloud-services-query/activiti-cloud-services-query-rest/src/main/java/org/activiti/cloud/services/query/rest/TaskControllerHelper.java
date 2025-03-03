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

import com.querydsl.core.types.Predicate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedModelAssembler;
import org.activiti.cloud.api.task.model.QueryCloudTask;
import org.activiti.cloud.services.query.app.repository.TaskCandidateGroupRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateUserRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.model.TaskCandidateGroupEntity;
import org.activiti.cloud.services.query.model.TaskCandidateUserEntity;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.activiti.cloud.services.query.rest.assembler.TaskRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.payload.TaskSearchRequest;
import org.activiti.cloud.services.query.rest.predicate.QueryDslPredicateAggregator;
import org.activiti.cloud.services.query.rest.predicate.QueryDslPredicateFilter;
import org.activiti.cloud.services.query.rest.specification.TaskSpecification;
import org.activiti.cloud.services.security.TaskLookupRestrictionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.transaction.annotation.Transactional;

public class TaskControllerHelper {

    private final TaskRepository taskRepository;

    private final TaskCandidateUserRepository taskCandidateUserRepository;

    private final TaskCandidateGroupRepository taskCandidateGroupRepository;

    private final ProcessVariableService processVariableService;

    private final AlfrescoPagedModelAssembler<TaskEntity> pagedCollectionModelAssembler;

    private final QueryDslPredicateAggregator predicateAggregator;

    private final TaskRepresentationModelAssembler taskRepresentationModelAssembler;

    private final TaskLookupRestrictionService taskLookupRestrictionService;

    private final SecurityManager securityManager;

    public TaskControllerHelper(
        TaskRepository taskRepository,
        TaskCandidateUserRepository taskCandidateUserRepository,
        TaskCandidateGroupRepository taskCandidateGroupRepository,
        ProcessVariableService processVariableService,
        AlfrescoPagedModelAssembler<TaskEntity> pagedCollectionModelAssembler,
        QueryDslPredicateAggregator predicateAggregator,
        TaskRepresentationModelAssembler taskRepresentationModelAssembler,
        TaskLookupRestrictionService taskLookupRestrictionService,
        SecurityManager securityManager
    ) {
        this.taskRepository = taskRepository;
        this.taskCandidateUserRepository = taskCandidateUserRepository;
        this.taskCandidateGroupRepository = taskCandidateGroupRepository;
        this.processVariableService = processVariableService;
        this.pagedCollectionModelAssembler = pagedCollectionModelAssembler;
        this.predicateAggregator = predicateAggregator;
        this.taskRepresentationModelAssembler = taskRepresentationModelAssembler;
        this.taskLookupRestrictionService = taskLookupRestrictionService;
        this.securityManager = securityManager;
    }

    public PagedModel<EntityModel<QueryCloudTask>> findAll(
        Predicate predicate,
        VariableSearch variableSearch,
        Pageable pageable,
        List<QueryDslPredicateFilter> filters
    ) {
        Page<TaskEntity> page = findPage(predicate, variableSearch, pageable, filters);
        return pagedCollectionModelAssembler.toModel(pageable, page, taskRepresentationModelAssembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<QueryCloudTask>> findAllWithProcessVariables(
        Predicate predicate,
        VariableSearch variableSearch,
        Pageable pageable,
        List<QueryDslPredicateFilter> filters,
        List<String> processVariableKeys
    ) {
        Page<TaskEntity> page = findPageWithProcessVariables(predicate, variableSearch, pageable, filters);
        processVariableService.fetchProcessVariablesForTasks(page.getContent(), processVariableKeys);
        return pagedCollectionModelAssembler.toModel(pageable, page, taskRepresentationModelAssembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<QueryCloudTask>> searchTasksRestricted(
        TaskSearchRequest taskSearchRequest,
        Pageable pageable
    ) {
        return searchTasks(
            taskSearchRequest,
            pageable,
            TaskSpecification.restricted(
                taskSearchRequest,
                securityManager.getAuthenticatedUserId(),
                securityManager.getAuthenticatedUserGroups()
            )
        );
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<QueryCloudTask>> searchTasksUnrestricted(
        TaskSearchRequest taskSearchRequest,
        Pageable pageable
    ) {
        return searchTasks(taskSearchRequest, pageable, TaskSpecification.unrestricted(taskSearchRequest));
    }

    private PagedModel<EntityModel<QueryCloudTask>> searchTasks(
        TaskSearchRequest taskSearchRequest,
        Pageable pageable,
        TaskSpecification taskSpecification
    ) {
        Page<TaskEntity> tasks = taskRepository.findAll(taskSpecification, pageable);
        fetchTaskCandidateUsers(tasks.getContent());
        fetchTaskCandidateGroups(tasks.getContent());
        processVariableService.fetchProcessVariablesForTasks(
            tasks.getContent(),
            taskSearchRequest.processVariableKeys()
        );
        return pagedCollectionModelAssembler.toModel(pageable, tasks, taskRepresentationModelAssembler);
    }

    public PagedModel<EntityModel<QueryCloudTask>> findAllByInvolvedUserQuery(Predicate predicate, Pageable pageable) {
        Page<TaskEntity> page = findAllByInvolvedUser(predicate, pageable);
        return pagedCollectionModelAssembler.toModel(pageable, page, taskRepresentationModelAssembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<QueryCloudTask>> findAllFromBody(
        Predicate predicate,
        VariableSearch variableSearch,
        Pageable pageable,
        List<QueryDslPredicateFilter> filters,
        List<String> processVariableKeys
    ) {
        if (processVariableKeys == null || processVariableKeys.isEmpty()) {
            return this.findAll(predicate, variableSearch, pageable, filters);
        } else {
            return this.findAllWithProcessVariables(predicate, variableSearch, pageable, filters, processVariableKeys);
        }
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<QueryCloudTask>> findAllByInvolvedUserQueryWithProcessVariables(
        Predicate predicate,
        List<String> processVariableKeys,
        Pageable pageable
    ) {
        Page<TaskEntity> page = findAllByInvolvedUser(predicate, pageable);
        processVariableService.fetchProcessVariablesForTasks(page.getContent(), processVariableKeys);
        return pagedCollectionModelAssembler.toModel(pageable, page, taskRepresentationModelAssembler);
    }

    private Page<TaskEntity> findAllByInvolvedUser(Predicate predicate, Pageable pageable) {
        Predicate conditions = taskLookupRestrictionService.restrictToInvolvedUsersQuery(predicate);
        return taskRepository.findInProcessInstanceScope(conditions, pageable);
    }

    public boolean canUserViewTask(Predicate predicate) {
        Predicate conditions = taskLookupRestrictionService.restrictToInvolvedUsersQuery(predicate);
        return taskRepository.existsInProcessInstanceScope(conditions);
    }

    private Page<TaskEntity> findPage(
        Predicate predicate,
        VariableSearch variableSearch,
        Pageable pageable,
        List<QueryDslPredicateFilter> filters
    ) {
        Predicate extendedPredicate = predicateAggregator.applyFilters(predicate, filters);

        Page<TaskEntity> page;
        if (variableSearch.isSet()) {
            page =
                taskRepository.findByVariableNameAndValue(
                    variableSearch.getName(),
                    variableSearch.getValue(),
                    extendedPredicate,
                    pageable
                );
        } else {
            page = taskRepository.findAll(extendedPredicate, pageable);
        }
        return page;
    }

    private Page<TaskEntity> findPageWithProcessVariables(
        Predicate predicate,
        VariableSearch variableSearch,
        Pageable pageable,
        List<QueryDslPredicateFilter> filters
    ) {
        Predicate extendedPredicate = predicateAggregator.applyFilters(predicate, filters);
        if (variableSearch.isSet()) {
            return taskRepository.findByVariableNameAndValue(
                variableSearch.getName(),
                variableSearch.getValue(),
                extendedPredicate,
                pageable
            );
        } else {
            return taskRepository.findAll(extendedPredicate, pageable);
        }
    }

    private void fetchTaskCandidateUsers(Collection<TaskEntity> tasks) {
        Map<String, Set<TaskCandidateUserEntity>> candidatesByTaskId = taskCandidateUserRepository
            .findByTaskIdIn(tasks.stream().map(TaskEntity::getId).collect(Collectors.toSet()))
            .stream()
            .collect(Collectors.groupingBy(TaskCandidateUserEntity::getTaskId, Collectors.toSet()));
        tasks.forEach(task -> task.setTaskCandidateUsers(candidatesByTaskId.get(task.getId())));
    }

    private void fetchTaskCandidateGroups(Collection<TaskEntity> tasks) {
        Map<String, Set<TaskCandidateGroupEntity>> candidatesByTaskId = taskCandidateGroupRepository
            .findByTaskIdIn(tasks.stream().map(TaskEntity::getId).collect(Collectors.toSet()))
            .stream()
            .collect(Collectors.groupingBy(TaskCandidateGroupEntity::getTaskId, Collectors.toSet()));
        tasks.forEach(task -> task.setTaskCandidateGroups(candidatesByTaskId.get(task.getId())));
    }
}
