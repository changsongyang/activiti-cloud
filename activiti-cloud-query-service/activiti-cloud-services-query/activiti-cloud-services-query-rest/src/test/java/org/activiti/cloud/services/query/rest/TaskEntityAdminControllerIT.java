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

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.activiti.cloud.services.query.rest.TestTaskEntityBuilder.buildDefaultTask;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityManagerFactory;
import java.util.Collections;
import org.activiti.api.runtime.conf.impl.CommonModelAutoConfiguration;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.cloud.alfresco.argument.resolver.AlfrescoPageRequest;
import org.activiti.cloud.alfresco.config.AlfrescoWebAutoConfiguration;
import org.activiti.cloud.conf.QueryRestWebMvcAutoConfiguration;
import org.activiti.cloud.services.query.app.repository.EntityFinder;
import org.activiti.cloud.services.query.app.repository.ProcessDefinitionRepository;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateGroupRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateUserRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.VariableRepository;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.activiti.cloud.services.security.TaskLookupRestrictionService;
import org.activiti.core.common.spring.security.policies.SecurityPoliciesManager;
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
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(TaskAdminController.class)
@Import(
    { QueryRestWebMvcAutoConfiguration.class, CommonModelAutoConfiguration.class, AlfrescoWebAutoConfiguration.class }
)
@EnableSpringDataWebSupport
@AutoConfigureMockMvc
@WithMockUser
@TestPropertySource("classpath:application-test.properties")
public class TaskEntityAdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessInstanceAdminService processInstanceAdminService;

    @MockBean
    private ProcessInstanceRepository processInstanceRepository;

    @MockBean
    private TaskCandidateUserRepository taskCandidateUserRepository;

    @MockBean
    private TaskCandidateGroupRepository taskCandidateGroupRepository;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private VariableRepository processVariableRepository;

    @MockBean
    private EntityFinder entityFinder;

    @MockBean
    private SecurityManager securityManager;

    @MockBean
    private SecurityPoliciesManager securityPoliciesManager;

    @MockBean
    private ProcessDefinitionRepository processDefinitionRepository;

    @MockBean
    private SecurityPoliciesProperties securityPoliciesProperties;

    @MockBean
    private TaskLookupRestrictionService taskLookupRestrictionService;

    @MockBean
    private TaskPermissionsHelper taskPermissionsHelper;

    @MockBean
    private ProcessInstanceService processInstanceService;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        assertThat(processInstanceAdminService).isNotNull();
        assertThat(processInstanceService).isNotNull();
        assertThat(entityManagerFactory).isNotNull();
    }

    @Test
    public void allTasksShouldReturnAllResultsUsingAlfrescoMetadataWhenMediaTypeIsApplicationJson() throws Exception {
        //given
        AlfrescoPageRequest pageRequest = new AlfrescoPageRequest(11, 10, PageRequest.of(0, 20));

        given(taskRepository.findAll(any(Predicate.class), eq(pageRequest)))
            .willReturn(new PageImpl<>(Collections.singletonList(buildDefaultTask()), pageRequest, 12));

        //when
        MvcResult result = mockMvc
            .perform(get("/admin/v1/tasks?skipCount=11&maxItems=10").accept(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isOk())
            .andReturn();

        assertThatJson(result.getResponse().getContentAsString())
            .node("list.pagination.skipCount")
            .isEqualTo(11)
            .node("list.pagination.maxItems")
            .isEqualTo(10)
            .node("list.pagination.count")
            .isEqualTo(1)
            .node("list.pagination.hasMoreItems")
            .isEqualTo(false)
            .node("list.pagination.totalItems")
            .isEqualTo(12);
    }

    @Test
    public void allTasksShouldReturnAllResultsUsingHalWhenMediaTypeIsApplicationHalJson() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(1, 10);

        given(taskRepository.findAll(any(Predicate.class), eq(pageRequest)))
            .willReturn(new PageImpl<>(Collections.singletonList(buildDefaultTask()), pageRequest, 11));

        //when
        mockMvc
            .perform(get("/admin/v1/tasks?page=1&size=10").accept(MediaTypes.HAL_JSON_VALUE))
            //then
            .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldUseAlfrescoMetadataWhenMediaTypeIsApplicationJson() throws Exception {
        //given
        TaskEntity taskEntity = buildDefaultTask();
        given(entityFinder.findById(eq(taskRepository), eq(taskEntity.getId()), anyString())).willReturn(taskEntity);

        Predicate restrictionPredicate = mock(Predicate.class);
        given(taskRepository.findAll(restrictionPredicate)).willReturn(Collections.singletonList(taskEntity));

        //when
        this.mockMvc.perform(
                get("/admin/v1/tasks/{taskId}", taskEntity.getId()).accept(MediaType.APPLICATION_JSON_VALUE)
            )
            //then
            .andExpect(status().isOk());
    }
}
