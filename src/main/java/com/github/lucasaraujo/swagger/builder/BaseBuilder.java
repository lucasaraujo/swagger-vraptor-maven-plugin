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

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.joda.time.DateTime;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Patch;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;

import com.github.lucasaraujo.swagger.model.ApiDeclaration;
import com.github.lucasaraujo.swagger.model.DataTypeFields;
import com.github.lucasaraujo.swagger.model.ItemsObject;
import com.github.lucasaraujo.swagger.model.ModelObject;
import com.github.lucasaraujo.swagger.model.PropertyObject;
import com.google.common.base.Joiner;
import com.wordnik.swagger.annotations.ApiOperation;

public class BaseBuilder {

	static String formatAbsolutePath(String path) {
		path = ensureAbsolutePath(path);
		return ensureOneLevelAbsolutePath(path);
	}

	static String ensureAbsolutePath(String path) {
		return path.replaceFirst("(^[^/].?)", "/$1");
	}

	static String ensureOneLevelAbsolutePath(String path) {
		path = path.replaceAll("(.)\\W", "$1_");
		return path.replaceFirst("^/$", "/_");
	}

	static String joinPaths(String first, String second) {
		first = first.replaceFirst("/$", "");
		second = ensureAbsolutePath(second);
		return first + second;
	}

	static String[] methodPaths(Method method) {
		String[] methodName = new String[] {method.getName()};
		Get get = method.getAnnotation(Get.class);
		if (get != null) {
			String[] value = get.value();
			return (value != null && value.length > 0) ? value : methodName;
		}
		Post post = method.getAnnotation(Post.class);
		if (post != null) {
			String[] value = post.value();
			return (value != null && value.length > 0) ? value : methodName;
		}
		Put put = method.getAnnotation(Put.class);
		if (put != null) {
			String[] value = put.value();
			return (value != null && value.length > 0) ? value : methodName;
		}
		Patch patch = method.getAnnotation(Patch.class);
		if (patch != null) {
			String[] value = patch.value();
			return (value != null && value.length > 0) ? value : methodName;
		}
		Delete delete = method.getAnnotation(Delete.class);
		if (delete != null) {
			String[] value = delete.value();
			return (value != null && value.length > 0) ? value : methodName;
		}
		return methodName;
	}

	static String httpMethod(ApiOperation apiOperation, Method method) throws MojoExecutionException {
		if (apiOperation != null && apiOperation.httpMethod() != null && apiOperation.httpMethod().length() > 0) {
			return apiOperation.httpMethod().toUpperCase();
		}
		Get get = method.getAnnotation(Get.class);
		if (get != null) {
			return "GET";
		}
		Post post = method.getAnnotation(Post.class);
		if (post != null) {
			return "POST";
		}
		Put put = method.getAnnotation(Put.class);
		if (put != null) {
			return "PUT";
		}
		Patch patch = method.getAnnotation(Patch.class);
		if (patch != null) {
			return "PATCH";
		}
		Delete delete = method.getAnnotation(Delete.class);
		if (delete != null) {
			return "DELETE";
		}
		throw new MojoExecutionException(String.format("Could not determinate HTTP method for %s#%s(%s)",
				method.getClass().getName(), method.getName(), Joiner.on(", ").join(method.getParameterTypes())));
	}

	static String nicknameFromMethod(Method method) {
		for (Method classMethod : method.getClass().getMethods()) {
			if (classMethod.getName().equals(method.getName()) && !classMethod.equals(method)) {
				List<String> names = new ArrayList<String>();
				for (Class<?> type : method.getParameterTypes()) {
					names.add(type.getSimpleName());
				}
				return method.getName() + "_" + Joiner.on("_").join(names);
			}
		}
		return method.getName();
	}

	static void setDataTypeFields(DataTypeFields dataTypeFields, Class<?> objClass, ApiDeclaration apiDeclaration) {
		if (objClass.isArray()) {
			Class<?> componentType = objClass.getComponentType();
			setDataTypeFields(dataTypeFields, componentType, true, false, apiDeclaration);
		} else if (objClass.isAssignableFrom(Set.class)) {
			setDataTypeFields(dataTypeFields, Object.class, true, true, apiDeclaration);
		} else if (objClass.isAssignableFrom(Collection.class)) {
			setDataTypeFields(dataTypeFields, Object.class, true, false, apiDeclaration);
		} else {
			setDataTypeFields(dataTypeFields, objClass, false, false, apiDeclaration);
		}
	}

	static Class<?>[] fieldGenericTypesForClass(Field field, Class<?> clazz) {
		Type searchType = field.getGenericType();
		if (clazz == null && searchType instanceof GenericArrayType) {
			Type type = ((GenericArrayType) searchType).getGenericComponentType();
			Class<?> componentClass = (type instanceof Class<?>) ? (Class<?>) type : Object.class;
			return new Class<?>[] {componentClass};
		}
		if (clazz == null && searchType instanceof Class<?> && ((Class<?>) searchType).isArray()) {
			return new Class<?>[] {((Class<?>) searchType).getComponentType()};
		}

		Class<?> searchClass = classForType(searchType);
		Map<Type, Map<String, Class<?>>> genericsInfo = new HashMap<Type, Map<String, Class<?>>>();

		while (searchClass != null) {
			Class<?>[] arguments = BaseBuilder.typeArguments(searchType, genericsInfo);
			if (searchClass.equals(clazz)) {
				return arguments;
			}

			for (Type genericInteface : searchClass.getGenericInterfaces()) {
				arguments = BaseBuilder.typeArguments(genericInteface, genericsInfo);
				Class<?> interfaceClass = classForType(genericInteface);
				if (clazz.equals(interfaceClass)) {
					return arguments;
				}
				for (Type superInterface : interfaceClass.getGenericInterfaces()) {
					arguments = BaseBuilder.typeArguments(superInterface, genericsInfo);
					Class<?> superClass = classForType(superInterface);
					if (clazz.equals(superClass)) {
						return arguments;
					}
				}
			}
			searchType = searchClass.getGenericSuperclass();
			searchClass = classForType(searchType);
		}

		return new Class<?>[] {};
	}

	static Class<?> classForType(Type type) {
		Class<?> clazz = Object.class;
		if (type instanceof Class<?>) {
			clazz = (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			if (((ParameterizedType) type).getRawType() instanceof Class<?>) {
				clazz = (Class<?>) ((ParameterizedType) type).getRawType();
			}
		} else if (type instanceof TypeVariable<?>) {
			Object genericDeclaration = ((TypeVariable<?>) type).getGenericDeclaration();
			if (genericDeclaration instanceof Class<?>) {
				clazz = (Class<?>) genericDeclaration;
			}
		} else if (type instanceof WildcardType) {
	    	Type[] upperBounds = ((WildcardType) type).getUpperBounds();
	    	Type[] lowerBounds = ((WildcardType) type).getLowerBounds();
	    	if (upperBounds.length > 0 && !upperBounds[0].equals(Object.class)) {
	    		clazz = BaseBuilder.classForType(upperBounds[0]);
	    	} else if (lowerBounds.length > 0) {
	    		clazz = BaseBuilder.classForType(lowerBounds[0]);
	    	}
		}
		return clazz;
	}

	static Class<?>[] typeArguments(Type type) {
		return typeArguments(type, new HashMap<Type, Map<String, Class<?>>>());
	}

	static Class<?>[] typeArguments(Type type, Map<Type, Map<String, Class<?>>> genericsInfo) {

        if (type instanceof Class<?> && ((Class<?>) type).isArray()) {
        	Class<?> clazz = ((Class<?>) type).getComponentType();
        	return new Class<?>[] {clazz};
        }
        if (type instanceof GenericArrayType) {
        	Type componentType = ((GenericArrayType) type).getGenericComponentType();
        	Class<?> clazz = ((componentType instanceof Class<?>) ? (Class<?>) componentType : Object.class);
        	return new Class<?>[] {clazz};
        }
        if (type instanceof TypeVariable<?>) {
        	Class<?> clazz = null;
        	Object genericDeclartion = ((TypeVariable<?>) type).getGenericDeclaration();
        	Map<String, Class<?>> typeGenericsInfo = genericsInfo.get(genericDeclartion);

        	if (typeGenericsInfo != null) {
        		clazz = typeGenericsInfo.get(((TypeVariable<?>) type).getName());
        	}

        	return new Class<?>[] {clazz == null ? Object.class : clazz};
        }

        if (type instanceof ParameterizedType) {
        	Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        	Class<?>[] actualClassArguments = new Class<?>[actualTypeArguments.length];
        	TypeVariable<?>[] typeParameters = new TypeVariable<?>[actualTypeArguments.length];

        	Type rawType = ((ParameterizedType) type).getRawType();
        	if (rawType instanceof Class<?>) {
        		typeParameters = ((Class<?>) rawType).getTypeParameters();
        		assert typeParameters.length == actualTypeArguments.length;
        	}
        	Map<String, Class<?>> parameterizedInfos = genericsInfo.get(rawType);
        	if (parameterizedInfos == null) {
        		parameterizedInfos = new HashMap<String, Class<?>>();
        	}

        	for (int i = 0; i < actualTypeArguments.length; ++i) {
        		Type actualType = actualTypeArguments[i];
        		String typeParameterName = typeParameters[i].getName();

        		if (actualType instanceof Class<?>) {
        			actualClassArguments[i] = (Class<?>) actualType;
        		}
        		else if (actualType instanceof TypeVariable<?>) {
        			String actualTypeName = ((TypeVariable<?>) actualType).getName();
        			Object genericDeclartion = ((TypeVariable<?>) actualType).getGenericDeclaration();

        			Map<String, Class<?>> actualTypeGenerics = genericsInfo.get(genericDeclartion);
        			if (actualTypeGenerics != null) {
        				actualClassArguments[i] = actualTypeGenerics.get(actualTypeName);
        			}
        			if (actualClassArguments[i] == null) {
        				actualClassArguments[i] = Object.class;
        			}
        		}
        		else { // GenericArrayType, ParameterizedType, WildcardType
        			actualClassArguments[i] = BaseBuilder.classForType(actualType);
        		}

        		parameterizedInfos.put(typeParameterName, actualClassArguments[i]);
        	}

        	if (parameterizedInfos.size() > 0) {
        		genericsInfo.put(rawType, parameterizedInfos);
        	}
        	return actualClassArguments;
        }
        if (type instanceof WildcardType) {
        	Class<?> clazz = BaseBuilder.classForType(type);
            Map<String, Class<?>> typeGenericsInfo = genericsInfo.get(type);
    		if (typeGenericsInfo == null) {
    			typeGenericsInfo = new HashMap<String, Class<?>>();
    			genericsInfo.put(type, typeGenericsInfo);
    		}

        	typeGenericsInfo.put(null, clazz);
        	return new Class<?>[] {clazz};
        }

        return new Class<?>[] {};
	}

	static void setDataTypeFields(DataTypeFields dataTypeFields, Class<?> objClass, boolean isArray, boolean uniqueItems, ApiDeclaration apiDeclaration) {
		ItemsObject primitive = primitive(objClass);
		if (isArray) {
			dataTypeFields.type = "array";
			dataTypeFields.uniqueItems = uniqueItems;
			dataTypeFields.items = new ItemsObject();
			if (primitive == null) {
				dataTypeFields.items.$ref = objClass.getSimpleName();
				updateApiModels(apiDeclaration, objClass);
			} else {
				dataTypeFields.items.type = primitive.type;
				dataTypeFields.items.format = primitive.format;
			}
		}
		else if (objClass.isEnum()) {
			dataTypeFields.type = ItemsObject.STRING.type;
			dataTypeFields.enun = (Enum<?>[]) objClass.getEnumConstants();
		}
		else if (primitive == null) {
			dataTypeFields.$ref = objClass.getSimpleName();
			updateApiModels(apiDeclaration, objClass);
		}
		else {
			dataTypeFields.type = primitive.type;
			dataTypeFields.$ref = primitive.$ref;
			dataTypeFields.format = primitive.format;
		}
		// TODO defaultValue, minimum, maximum
	}

	static void updateApiModels(ApiDeclaration apiDeclaration, Class<?> objClass) {
		String id = objClass.getSimpleName();
		ModelObject model = apiDeclaration.models.get(id);
		if (model != null) {
			return;
		}
		model = new ModelObject();
		model.id = id;
		model.properties = new LinkedHashMap<String, PropertyObject>();
		apiDeclaration.models.put(id, model);
		// TODO description ...
		Field[] fields = objClass.getDeclaredFields();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				Class<?> clazz = classForType(field.getGenericType());
				PropertyObject propertyObject = new PropertyObject();
				if (clazz.isArray()) {
					setDataTypeFields(propertyObject, clazz.getComponentType(), true, false, apiDeclaration);
				} else if (Set.class.isAssignableFrom(clazz)) {
					setDataTypeFields(propertyObject, fieldGenericTypesForClass(field, Set.class)[0], true, true, apiDeclaration);
				} else if (Collection.class.isAssignableFrom(clazz)) {
					setDataTypeFields(propertyObject, fieldGenericTypesForClass(field, Collection.class)[0], true, false, apiDeclaration);
				} else {
					setDataTypeFields(propertyObject, clazz, false, false, apiDeclaration);
				}
				model.properties.put(field.getName(), propertyObject);
			}
		}
	}

	static ItemsObject primitive(Class<?> objClass) {
		if (objClass.equals(Void.class)) {
			return ItemsObject.VOID;
		} else if (objClass.equals(Integer.class) || objClass.equals(int.class)) {
			return ItemsObject.INTEGER;
		} else if (objClass.equals(Long.class) || objClass.equals(long.class)) {
			return ItemsObject.LONG;
		} else if (objClass.equals(Float.class) || objClass.equals(float.class)) {
			return ItemsObject.FLOAT;
		} else if (objClass.equals(Double.class) || objClass.equals(double.class)) {
			return ItemsObject.DOUBLE;
		} else if (objClass.equals(String.class)) {
			return ItemsObject.STRING;
		} else if (objClass.equals(Byte.class) || objClass.equals(byte.class)) {
			return ItemsObject.BYTE;
		} else if (objClass.equals(Boolean.class) || objClass.equals(boolean.class)) {
			return ItemsObject.BOOLEAN;
		} else if (objClass.equals(Date.class)) {
			return ItemsObject.DATE;
		} else if (objClass.equals(DateTime.class)) {
			return ItemsObject.DATE_TIME;
		} else {
			return null;
		}
	}

	static String[] splitMineTypes(String types) {
		if (types != null && types.trim().length() > 0) {
			return types.trim().split("\\s*,\\s*");
		}
		return null;
	}

}
