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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManagerFactory;
import java.util.UUID;
import org.activiti.api.runtime.conf.impl.CommonModelAutoConfiguration;
import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.cloud.alfresco.config.AlfrescoWebAutoConfiguration;
import org.activiti.cloud.conf.QueryRestWebMvcAutoConfiguration;
import org.activiti.cloud.services.query.app.repository.EntityFinder;
import org.activiti.cloud.services.query.app.repository.ProcessInstanceRepository;
import org.activiti.cloud.services.query.app.repository.ProcessModelRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateGroupRepository;
import org.activiti.cloud.services.query.app.repository.TaskCandidateUserRepository;
import org.activiti.cloud.services.query.app.repository.TaskRepository;
import org.activiti.cloud.services.query.app.repository.VariableRepository;
import org.activiti.cloud.services.query.model.ProcessDefinitionEntity;
import org.activiti.cloud.services.query.model.ProcessModelEntity;
import org.activiti.core.common.spring.security.policies.SecurityPoliciesManager;
import org.activiti.core.common.spring.security.policies.conf.SecurityPoliciesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProcessModelController.class)
@EnableSpringDataWebSupport
@AutoConfigureMockMvc
@Import(
    { QueryRestWebMvcAutoConfiguration.class, CommonModelAutoConfiguration.class, AlfrescoWebAutoConfiguration.class }
)
@WithMockUser
public class ProcessModelControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessModelRepository processModelRepository;

    @MockBean
    private SecurityPoliciesManager securityPoliciesManager;

    @MockBean
    private TaskCandidateUserRepository taskCandidateUserRepository;

    @MockBean
    private TaskCandidateGroupRepository taskCandidateGroupRepository;

    @MockBean
    private ProcessInstanceRepository processInstanceRepository;

    @MockBean
    private UserGroupManager userGroupManager;

    @MockBean
    private SecurityManager securityManager;

    @MockBean
    private SecurityPoliciesProperties securityPoliciesProperties;

    @MockBean
    private EntityFinder entityFinder;

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
    }

    @Test
    public void shouldReturnProcessModelById() throws Exception {
        //given
        given(securityPoliciesManager.arePoliciesDefined()).willReturn(true);

        String processDefinitionId = UUID.randomUUID().toString();
        ProcessDefinitionEntity processDefinition = new ProcessDefinitionEntity();
        processDefinition.setKey("processKey");
        processDefinition.setServiceName("serviceName");

        given(securityPoliciesManager.canRead(processDefinition.getKey(), processDefinition.getServiceName()))
            .willReturn(true);

        given(entityFinder.findById(eq(processModelRepository), eq(processDefinitionId), anyString()))
            .willReturn(new ProcessModelEntity(processDefinition, "<model/>"));

        //when
        mockMvc
            .perform(
                get("/v1/process-definitions/{processDefinitionId}/model", processDefinitionId)
                    .accept(MediaType.APPLICATION_XML_VALUE)
            )
            //then
            .andExpect(status().isOk())
            .andExpect(content().xml("<model/>"));
    }

    @Test
    public void shouldThrowExceptionWhenUserCannotReadGivenProcess() throws Exception {
        //given
        given(securityPoliciesManager.arePoliciesDefined()).willReturn(true);

        String processDefinitionId = UUID.randomUUID().toString();
        ProcessDefinitionEntity processDefinition = new ProcessDefinitionEntity();
        processDefinition.setKey("processKey");
        processDefinition.setServiceName("serviceName");

        given(securityPoliciesManager.canRead(processDefinition.getKey(), processDefinition.getServiceName()))
            .willReturn(false);

        given(entityFinder.findById(eq(processModelRepository), eq(processDefinitionId), anyString()))
            .willReturn(new ProcessModelEntity(processDefinition, "<model/>"));

        //when
        mockMvc
            .perform(
                get("/v1/process-definitions/{processDefinitionId}/model", processDefinitionId)
                    .accept(MediaType.APPLICATION_XML_VALUE)
            )
            //then
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("entry.message", is("Operation not permitted for " + processDefinition.getKey())));
    }
}
