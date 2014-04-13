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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.HeaderParam;

import com.github.lucasaraujo.swagger.model.ApiDeclaration;
import com.github.lucasaraujo.swagger.model.ModelObject;
import com.github.lucasaraujo.swagger.model.ParameterObject;
import com.github.lucasaraujo.swagger.model.PropertyObject;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;
import com.wordnik.swagger.annotations.ApiParam;

public class ParameterObjectBuilder extends BaseBuilder {

	private static final Pattern pathParamsPattern = Pattern.compile("\\{([^\\{\\}]*)\\}");;

	public static List<ParameterObject> buildOf(String httpMethod, String path, Method method, ApiDeclaration apiDeclaration) throws MojoExecutionException {

		Map<String, ParameterObject> parameterMap = new LinkedHashMap<String, ParameterObject>();

		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		for (Class<?> clazz : method.getParameterTypes()) {
			parameterTypes.add(clazz);
		}

		List<String> parameterNames = new ArrayList<String>();
		try {
			Paranamer paranamer = new AdaptiveParanamer();
			for (String name : paranamer.lookupParameterNames(method)) {
				parameterNames.add(name);
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Could not get parameter names", e);
		}

		// "header" parameters
		Annotation[][] paramsAnnotations = method.getParameterAnnotations();
		for (int count = 0, i = 0; i < paramsAnnotations.length; ++i) {
			Annotation[] annotations = paramsAnnotations[i];
			for (Annotation annotation : annotations) {
				if (HeaderParam.class.isAssignableFrom(annotation.annotationType())) {
					int idx = i - count;
					ParameterObject parameter = buildOf("header", parameterNames.get(idx), parameterTypes.get(idx), apiDeclaration);
					parameterMap.put(parameter.name, parameter);
					parameterTypes.remove(idx);
					parameterNames.remove(idx);
					++count;
					break;
				}
			}
		}

		// "path" parameters
		Matcher matcher = pathParamsPattern.matcher(path);
		while (matcher.find()) {
			String paramName = matcher.group(1);
			int idx = parameterNames.indexOf(paramName);
			if (idx >= 0) {
				ParameterObject parameter = buildOf("path", paramName, parameterTypes.get(idx), apiDeclaration);
				parameterMap.put(parameter.name, parameter);
				parameterNames.remove(idx);
				parameterTypes.remove(idx);
			}
		}

		if ("GET".equals(httpMethod)) { // GET request do not have body (payload)
			Iterator<Class<?>> typeItr = parameterTypes.iterator();
			Iterator<String> nameItr = parameterNames.iterator();
			while (nameItr.hasNext() && typeItr.hasNext()) {
				ParameterObject parameter = buildOf("query", nameItr.next(), typeItr.next(), apiDeclaration);
				parameterMap.put(parameter.name, parameter);
				nameItr.remove();
				typeItr.remove();
			}
		}

		String type = method.getAnnotation(Consumes.class) == null ? "form" : "body";
		Iterator<Class<?>> typeItr = parameterTypes.iterator();
		Iterator<String> nameItr = parameterNames.iterator();
		while (nameItr.hasNext() && typeItr.hasNext()) {
			String name = nameItr.next();
			Class<?> clazz = typeItr.next();
			if ((clazz.getSimpleName().equals("File"))) {
				ParameterObject parameter = buildOf("form", name, clazz, apiDeclaration);
				parameterMap.put(parameter.name, parameter);
			} else {
				ParameterObject parameter = buildOf(type, name, clazz, apiDeclaration);
				parameterMap.put(parameter.name, parameter);
			}
			nameItr.remove();
			typeItr.remove();
		}

		return new ArrayList<ParameterObject>(parameterMap.values());
	}

	public static ParameterObject buildOf(String type, String name, Class<?> clazz, ApiDeclaration apiDeclaration) {
		ParameterObject parameterObject = new ParameterObject();
		parameterObject.paramType = type;
		setDataTypeFields(parameterObject, clazz, apiDeclaration);
		if (parameterObject.type == null) {
			parameterObject.type = parameterObject.$ref;
			parameterObject.$ref = null;
		}
		ApiParam apiParam = clazz.getAnnotation(ApiParam.class);
		if (apiParam != null) {
			parameterObject.description = apiParam.value();
			parameterObject.required = apiParam.required();
			parameterObject.allowMultiple = apiParam.allowMultiple();
		}

		if (parameterObject.paramType == "body") {
			PropertyObject propertyObject = new PropertyObject();
			propertyObject.$ref = parameterObject.type;
			ModelObject model = new ModelObject();
			model.id = "$" + clazz.getSimpleName();
			model.properties = new HashMap<String, PropertyObject>(1);
			model.properties.put(name, propertyObject);
			model.required = new String[] {name};
			apiDeclaration.models.put(model.id, model);
			parameterObject.name = "body";
			parameterObject.type = model.id;
		} else if (apiParam != null && apiParam.name() != null && apiParam.name().length() > 0) {
			parameterObject.name = apiParam.name();
		} else {
			parameterObject.name = name;
		}
		return parameterObject;
	}

}
