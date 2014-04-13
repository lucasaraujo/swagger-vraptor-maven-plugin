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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import br.com.caelum.vraptor.Path;

import com.github.lucasaraujo.swagger.model.ResourceObject;
import com.wordnik.swagger.annotations.Api;

public class ResourceObjectBuilderTest {

	public static final String SHORT_DESCRIPTION = "test controller";
	public static final String DESCRIPTION = "Test Controller class";

	@Api(value = SHORT_DESCRIPTION, description = DESCRIPTION)
	static class FirstController {}

	@Api(value = "")
	@Path({"/one", "two"})
	static class SecondController {}

	@Api(value = "", basePath = "/custom/")
	@Path({"/one", "two"})
	static class ThirdController {}

	@Api(value = "")
	static class FourthController {}

	@Test
	public void it_should_create_an_instace() {
		ResourceObjectBuilder builder = new ResourceObjectBuilder();
		assertNotNull(builder);
	}

	@Test
	public void it_should_set_the_resource_description() {
		List<ResourceObject> resources = ResourceObjectBuilder.buildOf(FirstController.class);
		assertNotNull(resources);
		assertEquals(1, resources.size());
		ResourceObject resource = resources.get(0);
		assertEquals("/test_controller", resource.path);
		assertEquals(DESCRIPTION, resource.description);
	}

	@Test
	public void it_should_handle_two_paths() {
		List<ResourceObject> resources = ResourceObjectBuilder.buildOf(SecondController.class);
		assertNotNull(resources);
		assertEquals(2, resources.size());
		assertEquals("/one", resources.get(0).path);
		assertEquals("", resources.get(0).description);
		assertEquals("/two", resources.get(1).path);
		assertEquals("", resources.get(1).description);
	}

	@Test
	public void it_should_use_custom_base_paths() {
		List<ResourceObject> resources = ResourceObjectBuilder.buildOf(ThirdController.class);
		assertNotNull(resources);
		assertEquals(2, resources.size());
		assertEquals("/custom/one", resources.get(0).path);
		assertEquals("", resources.get(0).description);
		assertEquals("/custom/two", resources.get(1).path);
		assertEquals("", resources.get(1).description);
	}

	@Test
	public void it_should_use_class_name_as_path() {
		List<ResourceObject> resources = ResourceObjectBuilder.buildOf(FourthController.class);
		assertNotNull(resources);
		assertEquals(1, resources.size());
		assertEquals("/Fourth", resources.get(0).path);
		assertEquals("", resources.get(0).description);
	}

}
