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
import java.util.Arrays;
import java.util.Optional;
import org.activiti.cloud.services.query.model.QTaskEntity;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskRepository
    extends
        PagingAndSortingRepository<TaskEntity, String>,
        CustomizedJpaSpecificationExecutor<TaskEntity>,
        QuerydslPredicateExecutor<TaskEntity>,
        QuerydslBinderCustomizer<QTaskEntity>,
        CustomizedTaskRepository,
        CrudRepository<TaskEntity, String> {
    @Override
    default void customize(QuerydslBindings bindings, QTaskEntity root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.eq(value));
        bindings.bind(root.createdFrom).first((path, value) -> root.createdDate.after(value));
        bindings.bind(root.createdTo).first((path, value) -> root.createdDate.before(value));
        bindings.bind(root.lastModifiedFrom).first((path, value) -> root.lastModified.after(value));
        bindings.bind(root.lastModifiedTo).first((path, value) -> root.lastModified.before(value));
        bindings.bind(root.lastClaimedFrom).first((path, value) -> root.claimedDate.after(value));
        bindings.bind(root.lastClaimedTo).first((path, value) -> root.claimedDate.before(value));
        bindings.bind(root.completedFrom).first((path, value) -> root.completedDate.after(value));
        bindings.bind(root.completedTo).first((path, value) -> root.completedDate.before(value));
        bindings.bind(root.dueDateFrom).first((path, value) -> root.dueDate.after(value));
        bindings.bind(root.dueDateTo).first((path, value) -> root.dueDate.before(value));
        bindings
            .bind(root.candidateGroupId)
            .first((path, value) -> root.taskCandidateGroups.any().groupId.in(Arrays.asList(value.split(","))));

        bindings.bind(root.name).first((path, value) -> path.like("%" + value.toString() + "%"));
        bindings.bind(root.description).first((path, value) -> path.like("%" + value.toString() + "%"));

        whitelist(root)
            .excluding(root.variables)
            .excluding(root.processVariables)
            .excluding(root.standalone)
            .apply(bindings);
    }
}
