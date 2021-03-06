/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.scope.registration.rest.api;

import org.wso2.carbon.auth.scope.registration.dao.ScopeDAO;
import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;

import java.util.List;

/**
 * In memory ScopeDAO object used for mocking an erroneous scope DAO layer
 */
public class ScopeTestExceptionDAO implements ScopeDAO {

    private static final String ERROR_MESSAGE = "This is an expected exception";

    @Override
    public void addScope(Scope scope) throws ScopeDAOException {
        throw new ScopeDAOException(ERROR_MESSAGE);
    }

    @Override
    public List<Scope> getScopesWithPagination(Integer offset, Integer limit) throws ScopeDAOException {
        throw new ScopeDAOException(ERROR_MESSAGE);
    }

    @Override
    public Scope getScopeByName(String name) throws ScopeDAOException {
        throw new ScopeDAOException(ERROR_MESSAGE);
    }

    @Override
    public boolean isScopeExists(String scopeName) throws ScopeDAOException {
        throw new ScopeDAOException(ERROR_MESSAGE);
    }

    @Override
    public void deleteScopeByName(String name) throws ScopeDAOException {
        throw new ScopeDAOException(ERROR_MESSAGE);
    }

    @Override
    public void updateScopeByName(Scope updatedScope) throws ScopeDAOException {
        throw new ScopeDAOException(ERROR_MESSAGE);
    }
}
