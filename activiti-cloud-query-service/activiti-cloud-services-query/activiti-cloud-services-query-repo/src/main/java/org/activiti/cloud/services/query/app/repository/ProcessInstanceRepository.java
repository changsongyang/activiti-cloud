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
package org.activiti.cloud.services.query.app.repository;

import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import org.activiti.cloud.services.query.model.ProcessInstanceEntity;
import org.activiti.cloud.services.query.model.QProcessInstanceEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ProcessInstanceRepository extends PagingAndSortingRepository<ProcessInstanceEntity, String>,
                                                   QuerydslPredicateExecutor<ProcessInstanceEntity>,
                                                   QuerydslBinderCustomizer<QProcessInstanceEntity> {

    @Override
    default void customize(QuerydslBindings bindings,
                           QProcessInstanceEntity root) {

        bindings.bind(String.class).first((StringPath path, String value) -> path.eq(value));
        bindings.bind(root.lastModifiedFrom).first((path, value) -> root.lastModified.after(value));
        bindings.bind(root.lastModifiedTo).first((path, value) -> root.lastModified.before(value));
        bindings.bind(root.startFrom).first((path, value) -> root.startDate.after(value));
        bindings.bind(root.startTo).first((path, value) -> root.startDate.before(value));
        bindings.bind(root.completedFrom).first((path, value) -> root.completedDate.after(value));
        bindings.bind(root.completedTo).first((path, value) -> root.completedDate.before(value));
        bindings.bind(root.name).first((path, value) -> path.like("%"+value.toString()+"%"));
        bindings.bind(root.initiator).first((path, value) -> root.initiator.in(Arrays.asList(value.split(","))));
    }
}
