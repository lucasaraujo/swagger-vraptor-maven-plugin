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
 * The Parameter Object describes a single parameter to be sent in an operation
 * and maps to the {@link OperationObject#parameters} field in the
 * {@link OperationObject}.<br>
 * This object includes the {@link DataTypeFields} in order to describe the type
 * of this parameter. The {@link #type} field MUST be used to link to other
 * models.<br>
 * If {@link #type} is
 * <code>File<code>, the {@link OperationObject#consumes} field MUST be <code>"multipart/form-data"</code>
 * , and the {@link #paramType} MUST be <code>"form"</code>.
 *
 * @author Lucas Araújo
 * @see <a
 * href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#524-parameter-object">Swagger
 * RESTful API Documentation</a>
 */
public class ParameterObject extends DataTypeFields implements Serializable {

    private static final long serialVersionUID = -2088573122922946930L;

    /**
     * <b>Required.</b> The type of the parameter (that is, the location of the
     * parameter in the request). The value MUST be one of these values:
     * <code>"path"</code>, <code>"query"</code>, <code>"body"</code>,
     * <code>"header"</code>, <code>"form"</code>. Note that the values MUST be
     * lower case.
     */
    public String paramType;

    /**
     * <b>Required.</b> The unique name for the parameter. Each
     * <code>name</code> MUST be unique, even if they are associated with
     * different {@link #paramType} values. Parameter names are <i>case
     * sensitive</i>.
     * <ul>
     * <li>If {@link #paramType} is <code>"path"</code>, the </code>name</code>
     * field MUST correspond to the associated path segment from the
     * {@link ApiObject#path} field in the {@link ApiObject}.</li>
     * <li>If {@link #paramType} is <code>"query"</code>, the </code>name</code>
     * field corresponds to the query parameter name.</li>
     * <li>If {@link #paramType} is <code>"body"</code>, the </code>name</code>
     * is used only for Swagger-UI and Swagger-Codegen. In this
     * </code>name</code>, the name MUST be </code>"body"</code>.</li>
     * <li>If {@link #paramType} is <code>"form"</code>, the </code>name</code>
     * field corresponds to the form parameter key.</li>
     * <li>If {@link #paramType} is <code>"header"</code>, the
     * </code>name</code> field corresponds to the header parameter key.</li>
     * </ul>
     *
     * @see <a
     * href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#5241-name-examples">Name
     * Examples</a>
     */
    public String name;

    /**
     * <i>Recommended.</i> A brief description of this parameter.
     */
    public String description;

    /**
     * A flag to note whether this parameter is required. If this field is not
     * included, it is equivalent to adding this field with the value
     * <code>false</code>. The field MUST be included if {@link #paramType} is
     * <code>"path"</code> and MUST have the value <code>true</code>.
     */
    public boolean required;

    /**
     * Another way to allow multiple values for a "query" parameter. If used,
     * the query parameter may accept comma-separated values. The field may be
     * used only if {@link #paramType} is <code>"query"</code>,
     * <code>"header"</code> or <code>"path"</code>.
     */
    public boolean allowMultiple;

}
