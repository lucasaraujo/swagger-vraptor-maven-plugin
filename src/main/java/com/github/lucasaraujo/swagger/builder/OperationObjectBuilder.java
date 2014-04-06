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
		operationObject.parameters = ParameterObjectBuilder.buildOf(operationObject.method, path, method, apiDeclaration);
		// TODO
//		operationObject.responseMessages = Collections.emptyList();
//		operationObject.consumes = new ArrayList<String>();
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
