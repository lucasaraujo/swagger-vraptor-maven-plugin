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

package com.github.lucasaraujo;

import com.github.lucasaraujo.swagger.builder.ApiDeclarationBuilder;
import com.github.lucasaraujo.swagger.builder.ResourceObjectBuilder;
import com.github.lucasaraujo.swagger.model.ApiDeclaration;
import com.github.lucasaraujo.swagger.model.ResourceListing;
import com.github.lucasaraujo.swagger.model.ResourceObject;
import com.wordnik.swagger.annotations.Api;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.reflections.Reflections;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Lucas Araújo
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, configurator = "include-project-dependencies")
public class ApiSourceMojo extends AbstractMojo {

    @Parameter(defaultValue = "true")
    public boolean useOutputFlatStructure;
    @Parameter(required = true)
    private String apiVersion;
    @Parameter(defaultValue = "/", required = true)
    private String basePath;
    @Parameter
    private String[] locations;
    @Parameter(defaultValue = "${basedir}", required = true)
    private File swaggerDirectory;

    public void execute() throws MojoExecutionException {
        getLog().debug(toString());
        getSwaggerDirectory().mkdirs();

        ResourceListing listing = new ResourceListing();
        listing.apiVersion = getApiVersion();
        listing.apis = new ArrayList<ResourceObject>();

        for (Class<?> clazz : getValidClasses()) {
            for (ResourceObject resourceObject : ResourceObjectBuilder.buildOf(clazz)) {
                listing.apis.add(resourceObject);
                ApiDeclaration apiDeclaration = ApiDeclarationBuilder.buildOf(clazz);
                apiDeclaration.apiVersion = apiVersion;
                apiDeclaration.basePath = basePath;
                apiDeclaration.resourcePath = resourceObject.path;

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setSerializationInclusion(Inclusion.NON_NULL);
                    File file = new File(getSwaggerDirectory(), resourceObject.path);
                    file.createNewFile();
                    mapper.writeValue(file, apiDeclaration);
                } catch (Exception e) {
                    throw new MojoExecutionException("Generating file error", e);
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Inclusion.NON_NULL);
        try {
            File file = new File(getSwaggerDirectory(), "service");
            file.createNewFile();
            mapper.writeValue(file, listing);
        } catch (Exception e) {
            throw new MojoExecutionException("Generating file error", e);
        }
    }

    private Set<Class<?>> getValidClasses() {
        Set<Class<?>> classes;
        if (getLocations() != null && getLocations().length > 0) {
            classes = new HashSet<Class<?>>();
            for (String location : getLocations()) {
                Reflections reflections = new Reflections(location);
                classes.addAll(reflections.getTypesAnnotatedWith(Api.class));
            }
        } else {
            classes = new Reflections("").getTypesAnnotatedWith(Api.class);
        }
        return classes;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public File getSwaggerDirectory() {
        return swaggerDirectory;
    }

    public void setSwaggerDirectory(File swaggerDirectory) {
        this.swaggerDirectory = swaggerDirectory;
    }

    public boolean isUseOutputFlatStructure() {
        return useOutputFlatStructure;
    }

    public void setUseOutputFlatStructure(boolean useOutputFlatStructure) {
        this.useOutputFlatStructure = useOutputFlatStructure;
    }

    @Override
    public String toString() {
        return "ApiSourceMojo{" +
                "useOutputFlatStructure=" + useOutputFlatStructure +
                ", apiVersion='" + apiVersion + '\'' +
                ", basePath='" + basePath + '\'' +
                ", locations=" + Arrays.toString(locations) +
                ", swaggerDirectory=" + swaggerDirectory +
                '}';
    }
}
