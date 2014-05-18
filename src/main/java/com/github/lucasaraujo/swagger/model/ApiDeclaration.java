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
import java.util.List;
import java.util.Map;

/**
 * The API Declaration provides information about an API exposed on a resource.
 * There should be one file per {@link ResourceObject} described. The file MUST
 * be served in the URL described by the {@link ResourceObject#path} field.
 *
 * @author Lucas Araújo
 * @see <a
 * href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#52-api-declaration">Swagger
 * RESTful API Documentation</a>
 */
public class ApiDeclaration implements Serializable {

    private static final long serialVersionUID = -5494180926600729532L;

    /**
     * <b>Required.</b> Specifies the Swagger Specification version being used.
     * It can be used by the Swagger UI and other clients to interpret the API
     * listing. The value MUST be an existing Swagger specification version.
     * Currently, "1.0", "1.1", "1.2" are valid values.
     */
    public String swaggerVersion = "1.2";

    /**
     * Provides the version of the application API (not to be confused by the
     * {@link #swaggerVersion}).
     */
    public String apiVersion;

    /**
     * <b>Required.</b> The root URL serving the API. This field is important as
     * while it is common to have the Resource Listing and API Declarations on
     * the server providing the APIs themselves, it is not a requirement. The
     * API specifications can be served using static files and not generated by
     * the API server itself, so the URL for serving the API cannot always be
     * derived from the URL serving the API specification. The value SHOULD be
     * in the format of a URL.
     */
    public String basePath;

    /**
     * The <i>relative</i> path to the resource, from the {@link #basePath},
     * which this API Specification describes. The value MUST precede with a
     * forward slash (<code>"/"</code>).
     */
    public String resourcePath;

    /**
     * <b>Required.<b> A list of the APIs exposed on this resource. There MUST
     * NOT be more than one API Object per {@link ApiObject#path} in the array.
     */
    public List<ApiObject> apis;

    /**
     * A list of the models available to this resource. Note that these need to
     * be exposed separately for each API Declaration.<br>
     * The Models Object holds a field per model definition, and this is
     * different than the structure of the other objects in the spec. It follows
     * a subset of the <a href="http://json-schema.org/">JSON-Schema</a>
     * specification.<br>
     * Please note that the Models Object is an object containing other object
     * definitions and as such is structured as follows:
     * <p/>
     * <pre>
     * {
     *    "Model1" : {...},
     *    "Model2" : {...},
     *    ...,
     *    "ModelN" : {...}
     * }
     * </pre>
     */
    public Map<String, ModelObject> models;

    /**
     * A list of MIME types the APIs on this resource can produce. This is
     * global to all APIs but can be overridden on specific API calls.
     */
    public String[] produces;

    /**
     * A list of MIME types the APIs on this resource can consume. This is
     * global to all APIs but can be overridden on specific API calls.
     */
    public String[] consumes;

    // TODO Authorizations
}
