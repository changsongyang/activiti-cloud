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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import org.activiti.api.runtime.conf.impl.CommonModelAutoConfiguration;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.cloud.alfresco.argument.resolver.AlfrescoPageRequest;
import org.activiti.cloud.alfresco.config.AlfrescoWebAutoConfiguration;
import org.activiti.cloud.conf.QueryRestWebMvcAutoConfiguration;
import org.activiti.cloud.services.query.app.repository.ProcessDefinitionRepository;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateGroupRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateUserRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.VariableRepository;
import org.activiti.cloud.services.query.model.ProcessVariableEntity;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(ProcessInstanceVariableController.class)
@Import(
    { QueryRestWebMvcAutoConfiguration.class, CommonModelAutoConfiguration.class, AlfrescoWebAutoConfiguration.class }
)
@EnableSpringDataWebSupport
@AutoConfigureMockMvc
@WithMockUser
public class ProcessInstanceEntityVariableEntityControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VariableRepository variableRepository;

    @MockBean
    private ProcessInstanceRepository processInstanceRepository;

    @MockBean
    private TaskCandidateUserRepository taskCandidateUserRepository;

    @MockBean
    private TaskCandidateGroupRepository taskCandidateGroupRepository;

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
    private TaskRepository taskRepository;

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
    }

    @Test
    public void getVariablesShouldReturnAllResultsUsingAlfrescoMetadataWhenMediaTypeIsApplicationJson()
        throws Exception {
        //given
        AlfrescoPageRequest pageRequest = new AlfrescoPageRequest(11, 10, PageRequest.of(0, 20));

        ProcessVariableEntity variableEntity = buildVariable();

        given(variableRepository.findAll(any(Predicate.class), eq(pageRequest)))
            .willReturn(new PageImpl<>(Collections.singletonList(variableEntity), pageRequest, 12));

        //when
        MvcResult result = mockMvc
            .perform(
                get(
                    "/v1/process-instances/{processInstanceId}/variables?skipCount=11&maxItems=10",
                    variableEntity.getProcessInstanceId()
                )
                    .accept(MediaType.APPLICATION_JSON)
            )
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
    public void getVariablesShouldReturnAllResultsUsingHalWhenMediaTypeIsApplicationHalJson() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(1, 10);

        ProcessVariableEntity variableEntity = buildVariable();

        given(variableRepository.findAll(any(Predicate.class), eq(pageRequest)))
            .willReturn(new PageImpl<>(Collections.singletonList(variableEntity), pageRequest, 11));

        //when
        mockMvc
            .perform(
                get(
                    "/v1/process-instances/{processInstanceId}/variables?page=1&size=10",
                    variableEntity.getProcessInstanceId()
                )
                    .accept(MediaTypes.HAL_JSON_VALUE)
            )
            //then
            .andExpect(status().isOk());
    }

    private ProcessVariableEntity buildVariable() {
        ProcessVariableEntity variableEntity = new ProcessVariableEntity(
            1L,
            String.class.getName(),
            "firstName",
            UUID.randomUUID().toString(),
            "My app",
            "My app",
            "1",
            null,
            null,
            new Date(),
            new Date(),
            UUID.randomUUID().toString()
        );
        variableEntity.setValue("John");
        return variableEntity;
    }
}
