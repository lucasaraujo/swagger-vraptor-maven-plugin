package com.github.lucasaraujo.swagger.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;

import com.github.lucasaraujo.swagger.model.ApiDeclaration;
import com.github.lucasaraujo.swagger.model.ParameterObject;
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

		Iterator<Class<?>> typeItr = parameterTypes.iterator();
		Iterator<String> nameItr = parameterNames.iterator();
		while (nameItr.hasNext() && typeItr.hasNext()) {
			String name = nameItr.next();
			Class<?> clazz = typeItr.next();
			if ((clazz.getSimpleName().equals("File"))) {
				ParameterObject parameter = buildOf("form", name, clazz, apiDeclaration);
				parameterMap.put(parameter.name, parameter);
			} else {
				ParameterObject parameter = buildOf("body", name, clazz, apiDeclaration);
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
		if (parameterObject.paramType == "body") {
			parameterObject.name = "body";
		} else if (apiParam != null && apiParam.name() != null && apiParam.name().length() > 0) {
			parameterObject.name = apiParam.name();
		} else {
			parameterObject.name = name;
		}
		if (apiParam != null) {
			parameterObject.description = apiParam.value();
			parameterObject.required = apiParam.required();
			parameterObject.allowMultiple = apiParam.allowMultiple();
		}
		return parameterObject;
	}

}
