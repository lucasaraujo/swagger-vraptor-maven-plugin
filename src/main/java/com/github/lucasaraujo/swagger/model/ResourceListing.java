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

/**
 * The Resource Listing serves as the root document for the API description. It
 * contains general information about the API and an inventory of the available
 * resources.<br>
 * By default, this document SHOULD be served at the <code>/api-docs<code> path.
 *
 * @see <a
 *      href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#51-resource-listing">Swagger
 *      RESTful API Documentation</a>
 *
 * @author Lucas Araújo
 *
 */
public class ResourceListing implements Serializable {

	private static final long serialVersionUID = 1655087114683986903L;

	/**
	 * <b>Required.</b> Specifies the Swagger Specification version being used.
	 * It can be used by the Swagger UI and other clients to interpret the API
	 * listing. The value MUST be an existing Swagger specification version.
	 * Currently, "1.0", "1.1", "1.2" are valid values. The field is of string
	 * value for possible non-numeric versions in the future (for example,
	 * "1.2a").
	 */
	public String swaggerVersion = "1.2";

	/**
	 * <b>Required.<b> Lists the resources to be described by this specification
	 * implementation. The array can have 0 or more elements.
	 */
	public List<ResourceObject> apis;

	/**
	 * Provides the version of the application API (not to be confused by the
	 * {@link #swaggerVersion}).
	 */
	public String apiVersion;

	// TODO Authorizations

}
