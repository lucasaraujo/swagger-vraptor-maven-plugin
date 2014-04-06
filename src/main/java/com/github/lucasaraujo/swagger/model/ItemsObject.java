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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This object is used to describe the value types used inside an array. Out of
 * the {@DataTypeFields} it can include either the
 * {@link #type} + @{link #format} fields <i>OR</i> the {@link #$ref} field
 * (when referencing a model). The rest of the listed fields are not applicable.
 *
 * @see <a
 *      href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#434-items-object">Swagger
 *      RESTful API Documentation</a>
 *
 * @author Lucas Araújo
 *
 */
@JsonIgnoreProperties("primitiveClass")
public class ItemsObject implements Serializable {

	private static final long serialVersionUID = -4608123892993151504L;

	public static final ItemsObject VOID = new ItemsObject("void", null);
	public static final ItemsObject INTEGER = new ItemsObject("integer", "int32");
	public static final ItemsObject LONG = new ItemsObject("integer", "int64");
	public static final ItemsObject FLOAT = new ItemsObject("number", "float");
	public static final ItemsObject DOUBLE = new ItemsObject("number", "double");
	public static final ItemsObject STRING = new ItemsObject("string", null);
	public static final ItemsObject BYTE = new ItemsObject("string", "byte");
	public static final ItemsObject BOOLEAN = new ItemsObject("boolean", null);
	public static final ItemsObject DATE = new ItemsObject("string", "date");
	public static final ItemsObject DATE_TIME = new ItemsObject("string", "date-time");

	/**
	 * Different programming languages represent primitives differently. The
	 * Swagger specification supports by name only the primitive types supported
	 * by the <a
	 * href="http://json-schema.org/latest/json-schema-core.html#anchor8"
	 * >JSON-Schema Draft 4</a>. However, in order to allow fine tuning a
	 * primitive definition, an additional {@link #format} field MAY accompany
	 * the {@link #type} primitive to give more information about the type used.
	 * If the {@link #format} field is used, the respective client MUST conform
	 * to the elaborate type.
	 */
	public static final ItemsObject[] PRIMITIVES = new ItemsObject[] { VOID,
			INTEGER, LONG, FLOAT, DOUBLE, STRING, BYTE, BOOLEAN, DATE,
			DATE_TIME, };

	/**
	 * </b>Required (if {@link #$ref} is not used).</b> The return type of the
	 * operation. The value MUST be one of the {@link #PRIMITIVES},
	 * <code>array<code> or a model's id.
	 */
	public String type;

	/**
	 * <b>Required (if {@link #type} is not used).</b> The Model to be used. The
	 * value MUST be a model's id.
	 */
	public String $ref;

	/**
	 * Fine-tuned primitive type definition. The value MUST be one that is
	 * defined under {@link #PRIMITIVES}, corresponding to the right primitive
	 * type.
	 */
	public String format;

	public ItemsObject() {
		this(null, null, null);
	}

	public ItemsObject(String $ref) {
		this(null, null, $ref);
	}

	public ItemsObject(String type, String format) {
		this(type, format, null);
	}

	public ItemsObject(String type, String format, String $ref) {
		this.type = type;
		this.format = format;
		this.$ref = $ref;
	}
}
