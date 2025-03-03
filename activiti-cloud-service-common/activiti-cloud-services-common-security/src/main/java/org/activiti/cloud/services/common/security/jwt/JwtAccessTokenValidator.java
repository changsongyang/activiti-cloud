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
package org.activiti.cloud.services.common.security.jwt;

import java.util.List;
import java.util.Optional;
import org.activiti.cloud.services.common.security.jwt.validator.ValidationCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtAccessTokenValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAccessTokenValidator.class);

    private final List<ValidationCheck> validationChecks;

    public JwtAccessTokenValidator(List<ValidationCheck> validationChecks) {
        this.validationChecks = validationChecks;
    }

    public boolean isValid(@NonNull JwtAdapter jwtAdapter) {
        return Optional
            .ofNullable(jwtAdapter)
            .map(JwtAdapter::getJwt)
            .map(this::isValid)
            .orElseThrow(() -> new SecurityException("Invalid access token instance"));
    }

    public boolean isValid(Jwt accessToken) {
        return !validationChecks
            .stream()
            .map(check -> {
                boolean valid = check.isValid(accessToken);
                if (!valid) {
                    LOGGER.debug("Token invalid because the {} validation has failed.", check.getClass().toString());
                }
                return valid;
            })
            .anyMatch(b -> b.equals(false));
    }
}
