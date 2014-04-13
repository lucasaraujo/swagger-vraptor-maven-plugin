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

package com.github.lucasaraujo.swagger.builder;

import java.lang.reflect.Method;

import org.apache.maven.plugin.MojoExecutionException;

import com.github.lucasaraujo.swagger.model.ApiDeclaration;
import com.github.lucasaraujo.swagger.model.OperationObject;
import com.wordnik.swagger.annotations.ApiOperation;

public class OperationObjectBuilder extends BaseBuilder {

	public static OperationObject buidOf(String path, ApiOperation operation, Method method, ApiDeclaration apiDeclaration) throws MojoExecutionException {
		OperationObject operationObject = new OperationObject();
		operationObject.method = httpMethod(operation, method);
		operationObject.summary = operation.value();
		operationObject.notes = operation.notes();
		operationObject.nickname = nicknameFromMethod(method);
		operationObject.produces = splitMineTypes(operation.produces());
		operationObject.consumes = splitMineTypes(operation.consumes());
		operationObject.parameters = ParameterObjectBuilder.buildOf(operationObject.method, path, method, apiDeclaration);
		// TODO responseMessages

		// Set operation return value
		if (operation.responseContainer().length() > 0) {
			boolean uniqueItems = operation.responseContainer().equalsIgnoreCase("set");
			setDataTypeFields(operationObject, operation.response(), true, uniqueItems, apiDeclaration);
		} else {
			setDataTypeFields(operationObject, operation.response(), apiDeclaration);
		}

		if (method.getAnnotation(Deprecated.class) != null) {
			operationObject.deprecated = "Deprecated";
		}

		return operationObject;
	}

}
