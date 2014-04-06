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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The Operation Object describes a single operation on a {@link ApiObject#path}
 * .<br>
 * This object includes the {@link DataTypeFields} in order to describe the
 * return value of the operation. The {@link DataTypeFields#type} field MUST be
 * used to link to other models.<br>
 * This is the only object where the {@link DataTypeFields#type} MAY have the
 * value of <code>void</code> to indicate that the operation returns no value.
 *
 * @see <a
 *      href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#523-operation-object">Swagger
 *      RESTful API Documentation</a>
 *
 * @author Lucas Araújo
 *
 */
public class OperationObject extends DataTypeFields implements Serializable {

	private static final long serialVersionUID = -8671325057513527251L;

	/**
	 * <b>Required.</b> The HTTP method required to invoke this operation. The
	 * value MUST be one of the following values: <code>"GET"</code>,
	 * <code>"POST"</code>, <code>"PUT"</code>, <code>"PATCH"</code>,
	 * <code>"DELETE"</code>, <code>"OPTIONS"</code>. Note that the values MUST
	 * be in uppercase.
	 */
	public String method;

	/**
	 * A short summary of what the operation does. For maximum readability in
	 * the swagger-ui, this field SHOULD be less than 120 characters.
	 */
	public String summary;

	/**
	 * A verbose explanation of the operation behavior.
	 */
	public String notes;

	/**
	 * <b>Required.</b> A unique id for the operation that can be used by tools
	 * reading the output for further and easier manipulation. For example,
	 * Swagger-Codegen will use the nickname as the method name of the operation
	 * in the client it generates. The value MUST be alphanumeric and may
	 * include underscores. Whitespsace characters are not allowd.
	 */
	public String nickname;

	/**
	 * Not implemented.
	 */
	public Map<?, ?> authorizations = Collections.emptyMap();

	/**
	 * <b>Required.</b> The inputs to the operation. If no parameters are
	 * needed, an empty array MUST be included.
	 */
	public List<ParameterObject> parameters;

	/**
	 * Lists the possible response statuses that can return from the operation.
	 */
	public List<ResponseMessageObject> responseMessages;

	/**
	 * A list of MIME types this operation can produce. This is overrides the
	 * global {@link ApiDeclaration#produces} definition at the root of the API
	 * Declaration. Each {@link String} value SHOULD represent a MIME type.
	 */
	public List<String> produces;

	/**
	 * A list of MIME types this operation can consume. This is overrides the
	 * global {@link ApiDeclaration#consumes} definition at the root of the API
	 * Declaration. Each {@link String} value SHOULD represent a MIME type.
	 */
	public List<String> consumes;

	/**
	 * Declares this operation to be deprecated. Usage of the declared operation
	 * should be refrained.
	 */
	public String deprecated;

}
