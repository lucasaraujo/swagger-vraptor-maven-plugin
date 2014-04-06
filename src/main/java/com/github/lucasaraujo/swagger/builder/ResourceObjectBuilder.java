package com.github.lucasaraujo.swagger.builder;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Path;

import com.github.lucasaraujo.swagger.model.ResourceObject;
import com.wordnik.swagger.annotations.Api;

public class ResourceObjectBuilder extends BaseBuilder {

	public static List<ResourceObject> buildOf(Class<?> clazz) {
		Api api = clazz.getAnnotation(Api.class);
		List<ResourceObject> resourceObjects = new ArrayList<ResourceObject>();
		for (String path : resourceObjectPaths(clazz)) {
			ResourceObject resourceObject = new ResourceObject();
			resourceObject.path = path;
			resourceObject.description = api.description();
			resourceObjects.add(resourceObject);
		}
		return resourceObjects;
	}

	private static String[] resourceObjectPaths(Class<?> clazz) {
		Api api = clazz.getAnnotation(Api.class);
		String basePath = api.basePath();

		String path = api.value();
		if (path.length() > 0) {
			return new String[] {joinPaths(basePath, formatAbsolutePath(path))};
		}

		Path pathClass = clazz.getAnnotation(Path.class);
		if (pathClass != null) {
			String[] paths = pathClass.value();
			for (int i = 0; i < paths.length; ++i) {
				paths[i] = joinPaths(basePath, formatAbsolutePath(paths[i]));
			}
			return paths;
		}

		path = clazz.getSimpleName().replaceFirst("Controller$", "");
		return new String[] {formatAbsolutePath(path)};
	}

}
