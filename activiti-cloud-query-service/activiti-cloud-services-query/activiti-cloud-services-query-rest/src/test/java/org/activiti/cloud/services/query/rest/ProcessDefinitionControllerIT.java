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

import static org.activiti.cloud.services.query.rest.ProcessDefinitionBuilder.buildDefaultProcessDefinition;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.List;
import org.activiti.api.runtime.conf.impl.CommonModelAutoConfiguration;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.cloud.alfresco.config.AlfrescoWebAutoConfiguration;
import org.activiti.cloud.conf.QueryRestWebMvcAutoConfiguration;
import org.activiti.cloud.services.query.app.repository.ProcessDefinitionRepository;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateGroupRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateUserRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.VariableRepository;
import org.activiti.cloud.services.query.model.QProcessDefinitionEntity;
import org.activiti.cloud.services.security.ProcessDefinitionRestrictionService;
import org.activiti.cloud.services.security.TaskLookupRestrictionService;
import org.activiti.core.common.spring.security.policies.SecurityPoliciesManager;
import org.activiti.core.common.spring.security.policies.SecurityPolicyAccess;
import org.activiti.core.common.spring.security.policies.conf.SecurityPoliciesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProcessDefinitionController.class)
@Import(
    { QueryRestWebMvcAutoConfiguration.class, CommonModelAutoConfiguration.class, AlfrescoWebAutoConfiguration.class }
)
@EnableSpringDataWebSupport
@AutoConfigureMockMvc
@WithMockUser
public class ProcessDefinitionControllerIT {

    private static final String EVERYONE_GROUP = "*";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessDefinitionRepository processDefinitionRepository;

    @MockBean
    private ProcessDefinitionRestrictionService processDefinitionRestrictionService;

    @MockBean
    private SecurityManager securityManager;

    @MockBean
    private ProcessInstanceRepository processInstanceRepository;

    @MockBean
    private TaskCandidateUserRepository taskCandidateUserRepository;

    @MockBean
    private TaskCandidateGroupRepository taskCandidateGroupRepository;

    @MockBean
    private SecurityPoliciesManager securityPoliciesManager;

    @MockBean
    private SecurityPoliciesProperties securityPoliciesProperties;

    @MockBean
    private TaskLookupRestrictionService taskLookupRestrictionService;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private VariableRepository processVariableRepository;

    @MockBean
    private ProcessInstanceAdminService processInstanceAdminService;

    @MockBean
    private ProcessInstanceService processInstanceService;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        assertThat(processInstanceAdminService).isNotNull();
        assertThat(processInstanceService).isNotNull();
        assertThat(entityManagerFactory).isNotNull();
        when(securityManager.getAuthenticatedUserId()).thenReturn("user");
        when(securityManager.getAuthenticatedUserGroups()).thenReturn(List.of());
    }

    @Test
    public void shouldReturnAvailableProcessDefinitions() throws Exception {
        //given
        Predicate predicate = createPredicate();
        PageRequest pageRequest = PageRequest.of(0, 10);
        given(processDefinitionRepository.findAll(predicate, pageRequest))
            .willReturn(new PageImpl<>(Collections.singletonList(buildDefaultProcessDefinition()), pageRequest, 1));

        //when
        mockMvc
            .perform(get("/v1/process-definitions?page=0&size=10").accept(MediaTypes.HAL_JSON_VALUE))
            //then
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnAvailableProcessDefinitionsUsingAlfrescoFormat() throws Exception {
        //given
        Predicate predicate = createPredicate();
        given(processDefinitionRepository.findAll(eq(predicate), any(Pageable.class)))
            .willReturn(
                new PageImpl<>(Collections.singletonList(buildDefaultProcessDefinition()), PageRequest.of(1, 10), 11)
            );

        //when
        mockMvc
            .perform(get("/v1/process-definitions?skipCount=10&maxItems=10").accept(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isOk());
    }

    private Predicate createPredicate() {
        Predicate predicate = mock(Predicate.class);
        given(processDefinitionRestrictionService.restrictProcessDefinitionQuery(any(), eq(SecurityPolicyAccess.READ)))
            .willReturn(predicate);

        BooleanExpression candidateStarterExpression = QProcessDefinitionEntity.processDefinitionEntity.candidateStarterUsers
            .any()
            .userId.eq("user")
            .or(
                QProcessDefinitionEntity.processDefinitionEntity.candidateStarterGroups
                    .any()
                    .groupId.in(List.of(EVERYONE_GROUP))
            );

        return candidateStarterExpression.and(predicate);
    }
}
