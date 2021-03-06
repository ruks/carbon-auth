/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Map;

/**
 * Refresh grant handler implementation
 */
public class RefreshGrantHandler implements GrantHandler {

    private static final Logger log = LoggerFactory.getLogger(RefreshGrantHandler.class);
    public static final BearerTokenError MISSING_TOKEN = new BearerTokenError((String) null, (String) null, 401);
    public static final int ALLOWED_MINIMUM_VALIDITY_PERIOD_IN_MILI = 1000000;
    public static final String INVALID_GRANT_ERROR_CODE = "INVALID_GRANT";
    private OAuthDAO oauthDAO;
    private ApplicationDAO applicationDAO;
    private ClientLookup clientLookup;
    private UserNameMapper userNameMapper;

    public RefreshGrantHandler() {

    }

    @Override
    public void init(UserNameMapper userNameMapper, OAuthDAO oauthDAO, UserStoreManager userStoreManager,
                     ApplicationDAO applicationDAO) {

        this.userNameMapper = userNameMapper;
        this.oauthDAO = oauthDAO;
        this.applicationDAO = applicationDAO;
        clientLookup = new ClientLookupImpl(oauthDAO);
    }

    @Override
    public boolean validateGrant(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws AuthException {

        String clientId = (String) context.getParams().get(OAuthConstants.CLIENT_ID);
        AccessTokenDTO accessTokenDTO;
        boolean isExpired;
        String refreshToken = queryParameters.get(OAuthConstants.REFRESH_TOKEN_QUERY_PARAM);

        if (StringUtils.isEmpty(refreshToken)) {
            log.error("valid refresh token is not found");
            context.setErrorObject(OAuth2Error.INVALID_REQUEST);
            return false;
        }

        try {
            accessTokenDTO = oauthDAO.getTokenInfo(refreshToken, clientId);
            boolean isValidGrant = validateGrant(accessTokenDTO);
            if (!isValidGrant) {
                String error = "Invalid Grant provided by the client Id: ";
                log.error(error);
                BearerTokenError invalidGrant = new BearerTokenError(INVALID_GRANT_ERROR_CODE, error, 401);
                context.setErrorObject(invalidGrant);
                return false;
            }

            isExpired = isRefreshTokenExpired(accessTokenDTO);
            if (isExpired) {
                String error = "Refresh token is expired.";
                log.error(error);
                BearerTokenError invalidGrant = new BearerTokenError(INVALID_GRANT_ERROR_CODE, error, 401);
                context.setErrorObject(invalidGrant);
                return false;
            }
            context.getParams().put(OAuthConstants.AUTH_USER, userNameMapper.getLoggedInUserIDFromPseudoName
                    (accessTokenDTO.getAuthUser()));
            String scopes = queryParameters.get(OAuthConstants.SCOPE_QUERY_PARAM);
            if (StringUtils.isNotEmpty(scopes)) {
                context.getParams().put(OAuthConstants.SCOPE_QUERY_PARAM, scopes);
            } else {
                context.getParams().put(OAuthConstants.SCOPE_QUERY_PARAM, accessTokenDTO.getScopes());
            }
            return true;
        } catch (OAuthDAOException e) {
            log.error("Error getting token information from the DB", e);
            throw new OAuthDAOException("Error getting token information from the DB", e);
        }

    }

    /**
     * validate and issue a token using refresh token grant
     *
     * @param authorization   Authorization header client:secret as basic auth header
     * @param context         AccessTokenContext object that stores context information during request processing
     * @param queryParameters Map of query parameters sent
     * @throws AuthException throws if token issuing error occurred
     */
    @Override
    public void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws AuthException {

        String clientId = (String) context.getParams().get(OAuthConstants.CLIENT_ID);
        String user = (String) context.getParams().get(OAuthConstants.AUTH_USER);
        Scope scope = (Scope) context.getParams().get(OAuthConstants.FILTERED_SCOPES);

        TokenIssuer.generateAccessToken(scope, context);
        AccessTokenData accessTokenData = TokenDataUtil.generateTokenData(context);
        accessTokenData.setAuthUser(userNameMapper.getLoggedInPseudoNameFromUserID(user));
        accessTokenData.setClientId(clientId);
        oauthDAO.addAccessTokenInfo(accessTokenData);
        accessTokenData.setAuthUser(user);
    }

    private boolean isRefreshTokenExpired(AccessTokenDTO accessTokenDTO) {

        long issuedTime = accessTokenDTO.getRefreshTokenCreatedTime();
        long refreshValidity = accessTokenDTO.getRefreshTokenValidityPeriod() * 1000L;
        return calculateValidityInMillis(issuedTime, refreshValidity) < ALLOWED_MINIMUM_VALIDITY_PERIOD_IN_MILI;
    }

    public static long calculateValidityInMillis(long issuedTimeInMillis, long validityPeriodMillis) {

        long timestampSkew = 5 * 1000;
        return issuedTimeInMillis + validityPeriodMillis - (System.currentTimeMillis() - timestampSkew);
    }

    private boolean validateGrant(AccessTokenDTO accessTokenDTO) {

        if (accessTokenDTO == null) {
            return false;
        }
        return true;
    }
}
