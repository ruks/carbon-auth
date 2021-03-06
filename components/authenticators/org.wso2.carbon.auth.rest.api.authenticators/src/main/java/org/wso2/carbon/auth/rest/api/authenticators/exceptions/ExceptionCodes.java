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
package org.wso2.carbon.auth.rest.api.authenticators.exceptions;

/**
 *
 */
public enum ExceptionCodes implements ErrorHandler {
    INTERNAL_ERROR(900967, "General Error", 500, "Server Error Occurred"),
    AUTHENTICATION_FAILURE(900968, "Authentication Failure", 401, "Authentication Failure"),
    MALFORMED_AUTHORIZATION_HEADER_BASIC(900913, "Malformed Authorization Header", 400,
            "Please provide the Authorization : Basic <> token to proceed."),
    MALFORMED_AUTHORIZATION_HEADER_OAUTH(900912, "Malformed Authorization Header", 400,
            "Please provide the Authorization : Bearer <> token to proceed."),
    AUTH_GENERAL_ERROR(900900, "Authorization Error", 403, " Error in authorization"),
    INVALID_SCOPE(900910, "Invalid Scope", 403, " You are not authorized to access the resource."),
    ACCESS_TOKEN_INACTIVE(900904, "Access Token Error", 401, " Access token is inactive."),
    SWAGGER_URL_MALFORMED(900504, "Swagger url malformed", 400, "swagger url is malformed");

    private final long errorCode;
    private final String errorMessage;
    private final int httpStatusCode;
    private final String errorDescription;

    /**
     * @param errorCode        This is unique error code that pass to upper level.
     * @param msg              The error message that you need to pass along with the error code.
     * @param httpErrorCode    This HTTP status code which should return from REST API layer. If you don't want to pass
     *                         a http status code keep it blank.
     * @param errorDescription The error description.
     */
    ExceptionCodes(long errorCode, String msg, int httpErrorCode, String errorDescription) {

        this.errorCode = errorCode;
        this.errorMessage = msg;
        this.httpStatusCode = httpErrorCode;
        this.errorDescription = errorDescription;
    }

    @Override
    public long getErrorCode() {

        return 0;
    }

    @Override
    public String getErrorMessage() {

        return this.errorMessage;
    }

    @Override
    public String getErrorDescription() {

        return this.errorDescription;
    }

    @Override
    public int getHttpStatusCode() {

        return this.httpStatusCode;
    }
}
