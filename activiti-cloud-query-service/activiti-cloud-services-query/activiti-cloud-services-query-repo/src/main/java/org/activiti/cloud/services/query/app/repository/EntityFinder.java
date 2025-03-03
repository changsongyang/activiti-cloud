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

import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public class EntityFinder {

    public <T, ID> T findById(CrudRepository<T, ID> repository, ID id, String notFoundMessage) {
        Optional<T> findResult = repository.findById(id);
        return getEntity(findResult, notFoundMessage);
    }

    private <T> T getEntity(Optional<T> result, String notFoundMessage) {
        return result.orElseThrow(() -> new EntityNotFoundException(notFoundMessage));
    }

    public <T> T findOne(QuerydslPredicateExecutor<T> predicateExecutor, Predicate predicate, String notFoundMessage) {
        Optional<T> findResult = predicateExecutor.findOne(predicate);
        return getEntity(findResult, notFoundMessage);
    }
}
