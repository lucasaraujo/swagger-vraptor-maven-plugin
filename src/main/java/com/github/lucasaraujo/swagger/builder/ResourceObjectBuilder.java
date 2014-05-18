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

import br.com.caelum.vraptor.Path;
import com.github.lucasaraujo.swagger.model.ResourceObject;
import com.wordnik.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.List;

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
            return new String[]{joinPaths(basePath, formatAbsolutePath(path))};
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
        return new String[]{formatAbsolutePath(path)};
    }

}
