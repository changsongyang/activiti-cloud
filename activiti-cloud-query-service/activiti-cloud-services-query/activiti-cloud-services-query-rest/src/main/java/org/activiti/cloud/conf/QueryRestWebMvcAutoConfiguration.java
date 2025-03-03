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
package org.activiti.cloud.conf;

import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedModelAssembler;
import org.activiti.cloud.services.query.app.repository.EntityFinder;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateGroupRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateUserRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.VariableRepository;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.activiti.cloud.services.query.rest.ProcessInstanceAdminService;
import org.activiti.cloud.services.query.rest.ProcessInstanceSearchService;
import org.activiti.cloud.services.query.rest.ProcessInstanceService;
import org.activiti.cloud.services.query.rest.ProcessVariableService;
import org.activiti.cloud.services.query.rest.QueryLinkRelationProvider;
import org.activiti.cloud.services.query.rest.TaskControllerHelper;
import org.activiti.cloud.services.query.rest.TaskPermissionsHelper;
import org.activiti.cloud.services.query.rest.assembler.ApplicationRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.assembler.IntegrationContextRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.assembler.ProcessDefinitionRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.assembler.ProcessInstanceRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.assembler.ProcessInstanceVariableRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.assembler.ServiceTaskRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.assembler.TaskRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.assembler.TaskVariableRepresentationModelAssembler;
import org.activiti.cloud.services.query.rest.helper.ProcessInstanceAdminControllerHelper;
import org.activiti.cloud.services.query.rest.helper.ProcessInstanceControllerHelper;
import org.activiti.cloud.services.query.rest.predicate.QueryDslPredicateAggregator;
import org.activiti.cloud.services.security.ProcessDefinitionFilter;
import org.activiti.cloud.services.security.ProcessDefinitionKeyBasedRestrictionBuilder;
import org.activiti.cloud.services.security.ProcessDefinitionRestrictionService;
import org.activiti.cloud.services.security.ProcessInstanceFilter;
import org.activiti.cloud.services.security.ProcessInstanceRestrictionService;
import org.activiti.cloud.services.security.ProcessInstanceVariableFilter;
import org.activiti.cloud.services.security.ProcessVariableLookupRestrictionService;
import org.activiti.cloud.services.security.ProcessVariableRestrictionService;
import org.activiti.cloud.services.security.TaskLookupRestrictionService;
import org.activiti.cloud.services.security.TaskVariableLookupRestrictionService;
import org.activiti.core.common.spring.security.policies.SecurityPoliciesManager;
import org.activiti.core.common.spring.security.policies.conf.SecurityPoliciesProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class QueryRestWebMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProcessDefinitionRepresentationModelAssembler processDefinitionRepresentationModelAssembler() {
        return new ProcessDefinitionRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceRepresentationModelAssembler processInstanceRepresentationModelAssembler() {
        return new ProcessInstanceRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceVariableRepresentationModelAssembler processInstanceVariableRepresentationModelAssembler() {
        return new ProcessInstanceVariableRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskRepresentationModelAssembler taskRepresentationModelAssembler() {
        return new TaskRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceTaskRepresentationModelAssembler serviceTaskRepresentationModelAssembler() {
        return new ServiceTaskRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public IntegrationContextRepresentationModelAssembler integrationContextRepresentationModelAssembler() {
        return new IntegrationContextRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskVariableRepresentationModelAssembler taskVariableRepresentationModelAssembler() {
        return new TaskVariableRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryLinkRelationProvider processDefinitionRelProvider() {
        return new QueryLinkRelationProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskLookupRestrictionService taskLookupRestrictionService(SecurityManager securityManager) {
        return new TaskLookupRestrictionService(securityManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDefinitionKeyBasedRestrictionBuilder serviceNameRestrictionBuilder(
        SecurityPoliciesManager securityPoliciesManager,
        SecurityPoliciesProperties securityPoliciesProperties
    ) {
        return new ProcessDefinitionKeyBasedRestrictionBuilder(securityPoliciesManager, securityPoliciesProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceVariableFilter processInstanceVariableFilter() {
        return new ProcessInstanceVariableFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessVariableRestrictionService processVariableRestrictionService(
        SecurityPoliciesManager securityPoliciesManager,
        ProcessInstanceVariableFilter processInstanceVariableFilter,
        ProcessDefinitionKeyBasedRestrictionBuilder restrictionBuilder
    ) {
        return new ProcessVariableRestrictionService(
            securityPoliciesManager,
            processInstanceVariableFilter,
            restrictionBuilder
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessVariableLookupRestrictionService variableLookupRestrictionService(
        ProcessVariableRestrictionService restrictionService
    ) {
        return new ProcessVariableLookupRestrictionService(restrictionService);
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskVariableLookupRestrictionService taskVariableLookupRestrictionService(
        TaskLookupRestrictionService taskLookupRestrictionService
    ) {
        return new TaskVariableLookupRestrictionService(taskLookupRestrictionService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceFilter processInstanceFilter() {
        return new ProcessInstanceFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceRestrictionService processInstanceRestrictionService(
        SecurityPoliciesManager securityPoliciesManager,
        ProcessInstanceFilter processInstanceFilter,
        ProcessDefinitionKeyBasedRestrictionBuilder restrictionBuilder,
        SecurityManager securityManager
    ) {
        return new ProcessInstanceRestrictionService(
            securityPoliciesManager,
            processInstanceFilter,
            restrictionBuilder,
            securityManager
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDefinitionFilter processDefinitionFilter() {
        return new ProcessDefinitionFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDefinitionRestrictionService processDefinitionRestrictionService(
        SecurityPoliciesManager securityPoliciesManager,
        ProcessDefinitionKeyBasedRestrictionBuilder restrictionBuilder,
        ProcessDefinitionFilter processDefinitionFilter
    ) {
        return new ProcessDefinitionRestrictionService(
            securityPoliciesManager,
            restrictionBuilder,
            processDefinitionFilter
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskControllerHelper taskControllerHelper(
        TaskRepository taskRepository,
        TaskCandidateUserRepository taskCandidateUserRepository,
        TaskCandidateGroupRepository taskCandidateGroupRepository,
        ProcessVariableService processVariableService,
        AlfrescoPagedModelAssembler<TaskEntity> pagedCollectionModelAssembler,
        TaskRepresentationModelAssembler taskRepresentationModelAssembler,
        TaskLookupRestrictionService taskLookupRestrictionService,
        SecurityManager securityManager
    ) {
        return new TaskControllerHelper(
            taskRepository,
            taskCandidateUserRepository,
            taskCandidateGroupRepository,
            processVariableService,
            pagedCollectionModelAssembler,
            new QueryDslPredicateAggregator(),
            taskRepresentationModelAssembler,
            taskLookupRestrictionService,
            securityManager
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationRepresentationModelAssembler applicationRepresentationModelAssembler() {
        return new ApplicationRepresentationModelAssembler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskPermissionsHelper taskPermissionsHelper(
        SecurityManager securityManager,
        TaskControllerHelper taskControllerHelper
    ) {
        return new TaskPermissionsHelper(securityManager, taskControllerHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessVariableService processVariableService(VariableRepository variableRepository) {
        return new ProcessVariableService(variableRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceSearchService processInstanceSearchService(
        ProcessInstanceRepository processInstanceRepository,
        ProcessVariableService processVariableService,
        SecurityManager securityManager
    ) {
        return new ProcessInstanceSearchService(processInstanceRepository, processVariableService, securityManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceService processInstanceService(
        ProcessInstanceRepository processInstanceRepository,
        TaskRepository taskRepository,
        ProcessInstanceSearchService processInstanceSearchService,
        ProcessInstanceRestrictionService processInstanceRestrictionService,
        SecurityPoliciesManager securityPoliciesApplicationService,
        SecurityManager securityManager,
        EntityFinder entityFinder
    ) {
        return new ProcessInstanceService(
            processInstanceRepository,
            taskRepository,
            processInstanceSearchService,
            processInstanceRestrictionService,
            securityPoliciesApplicationService,
            securityManager,
            entityFinder
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceAdminService processInstanceAdminService(
        ProcessInstanceRepository processInstanceRepository,
        ProcessInstanceSearchService processInstanceSearchService,
        EntityFinder entityFinder
    ) {
        return new ProcessInstanceAdminService(
            processInstanceRepository,
            processInstanceSearchService,
            entityFinder,
            new QueryDslPredicateAggregator()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceControllerHelper processInstanceControllerHelper(
        ProcessInstanceRepository processInstanceRepository,
        ProcessInstanceService processInstanceService
    ) {
        return new ProcessInstanceControllerHelper(processInstanceRepository, processInstanceService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessInstanceAdminControllerHelper processInstanceAdminControllerHelper(
        ProcessInstanceRepository processInstanceRepository,
        ProcessInstanceAdminService processInstanceAdminService,
        ProcessInstanceControllerHelper processInstanceControllerHelper
    ) {
        return new ProcessInstanceAdminControllerHelper(
            processInstanceRepository,
            processInstanceAdminService,
            processInstanceControllerHelper
        );
    }
}
