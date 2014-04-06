package com.github.lucasaraujo.swagger.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;

import br.com.caelum.vraptor.Path;

import com.github.lucasaraujo.swagger.model.ApiDeclaration;
import com.github.lucasaraujo.swagger.model.ApiObject;
import com.github.lucasaraujo.swagger.model.ModelObject;
import com.github.lucasaraujo.swagger.model.OperationObject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

public class ApiDeclarationBuilder extends BaseBuilder {

	public static ApiDeclaration buidOf(Class<?> clazz) throws MojoExecutionException {

		Api api = clazz.getAnnotation(Api.class);
		Path aPath = clazz.getAnnotation(Path.class);

		String resoucePath = (aPath == null) ? "/" : ensureAbsolutePath(aPath.value()[0]);
		ApiDeclaration apiDeclaration = new ApiDeclaration();
		apiDeclaration.apis = new ArrayList<ApiObject>();
		apiDeclaration.models = new HashMap<String, ModelObject>();
		Map<String, ApiObject> pathToApiObject = new HashMap<String, ApiObject>();

		for (Method method : clazz.getMethods()) {
			ApiOperation operation = method.getAnnotation(ApiOperation.class);
			if (operation != null) {
				for (String path : methodPaths(method)) {
					path = ensureAbsolutePath(path);
					ApiObject apiObject = pathToApiObject.get(path);
					if (apiObject == null) {
						apiObject = new ApiObject();
						apiObject.path = ensureAbsolutePath(joinPaths(resoucePath, path));
						apiObject.description = api.description();
						apiObject.operations = new ArrayList<OperationObject>();
						pathToApiObject.put(path, apiObject);
						apiDeclaration.apis.add(apiObject);
					}
					apiObject.operations.add(OperationObjectBuilder.buidOf(path, operation, method, apiDeclaration));
				}
			}
		}
		return apiDeclaration;
	}

}
