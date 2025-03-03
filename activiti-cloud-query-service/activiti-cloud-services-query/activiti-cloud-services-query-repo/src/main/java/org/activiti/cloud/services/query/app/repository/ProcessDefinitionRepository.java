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

import static org.activiti.cloud.services.query.app.repository.QuerydslBindingsHelper.whitelist;

import com.querydsl.core.types.dsl.StringPath;
import org.activiti.cloud.services.query.model.ProcessDefinitionEntity;
import org.activiti.cloud.services.query.model.QProcessDefinitionEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProcessDefinitionRepository
    extends
        PagingAndSortingRepository<ProcessDefinitionEntity, String>,
        QuerydslPredicateExecutor<ProcessDefinitionEntity>,
        QuerydslBinderCustomizer<QProcessDefinitionEntity>,
        CrudRepository<ProcessDefinitionEntity, String> {
    @Override
    default void customize(QuerydslBindings bindings, QProcessDefinitionEntity root) {
        whitelist(root).apply(bindings);

        bindings.bind(String.class).first((StringPath path, String value) -> path.eq(value));
    }
}
