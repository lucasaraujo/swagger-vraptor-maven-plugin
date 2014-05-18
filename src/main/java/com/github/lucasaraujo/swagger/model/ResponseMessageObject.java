/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.lucasaraujo.swagger.model;

import java.io.Serializable;

/**
 * The <code>ResponseMessageObject</code> describes a single possible response
 * message that can be returned from the operation call, and maps to the
 * {@link OperationObject#responseMessages} field in the {@link OperationObject}
 * . Each Response Message allows you to give further details as to why the HTTP
 * status code may be received.
 *
 * @author Lucas Araújo
 * @see <a
 * href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#525-response-message-object">Swagger
 * RESTful API Documentation</a>
 */
public class ResponseMessageObject implements Serializable {

    private static final long serialVersionUID = -5661837640967025885L;

    /**
     * <b>Required.</b> The HTTP status code returned. The value SHOULD be one
     * of the status codes as described in <a
     * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">RFC 2616 -
     * Section 10</a>.
     */
    public int code;

    /**
     * <b>Required.</b> The explanation for the status code. It SHOULD be the
     * reason an error is received if an error status code is used.
     */
    public String message;

    /**
     * The return type for the given response.
     */
    public String responseModel;

}
