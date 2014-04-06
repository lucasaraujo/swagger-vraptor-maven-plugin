package com.github.lucasaraujo.swagger.builder;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.joda.time.DateTime;
import org.junit.Test;

import br.com.caelum.vraptor.Get;

import com.github.lucasaraujo.swagger.model.ApiDeclaration;
import com.github.lucasaraujo.swagger.model.DataTypeFields;
import com.github.lucasaraujo.swagger.model.ItemsObject;
import com.github.lucasaraujo.swagger.model.ModelObject;
import com.github.lucasaraujo.swagger.model.ParameterObject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

public class BaseBuilderTest {

	private static interface GenericTestObject<V, E> extends Collection<E>, Comparable<V> {}
	@SuppressWarnings("serial")
	private static class One<V> extends HashMap<String, V> {}
	@SuppressWarnings("serial")
	private static class Two<A> extends One<A>  {}
	@SuppressWarnings("serial")
	private static class Three<B> extends Two<Integer> {}

	@SuppressWarnings("unused")
	@Api("test")
	private static class TestObject {
		public Three<Double> three;
		public int[] intArray;
		public ArrayList<Integer> integerArrayList;
		public Set<? extends Map<String, String>> dateSet;
		public List<? super String> lowerBoundStringList;
		public GenericTestObject<Integer, String> genericTestObject;
		public Integer integer;

		@Get
		@ApiOperation(value = "", response = TestObject.class)
		public void primitiveParameters(int pInt, Integer pInteger, long plong, Long pLong,
				float pfloat, Float pFloat, double pdouble, Double pDouble,
				String pString, byte pbyte, Byte pByte, boolean pboolean,
				Boolean pBoolean, Date pDate, DateTime pDateTime) {}
	}

	@Test
	public void it_should_discover_map_type_arguments() throws NoSuchFieldException, SecurityException {
		Field field = TestObject.class.getDeclaredField("three");
		Class<?>[] types = BaseBuilder.fieldGenericTypesForClass(field, Map.class);
		assertArrayEquals(new Class<?>[] {String.class, Integer.class}, types);
		System.out.println(types);
	}

	@Test
	public void it_should_discover_array_type_argument() throws NoSuchFieldException, SecurityException {
		Field field = TestObject.class.getDeclaredField("intArray");
		Class<?>[] classes = BaseBuilder.fieldGenericTypesForClass(field, null);
		assertArrayEquals(new Class<?>[] {int.class}, classes);
	}

	@Test
	public void it_should_discover_parameterized_type_argument() throws NoSuchFieldException, SecurityException {
		Field field = TestObject.class.getDeclaredField("integerArrayList");
		Class<?>[] classes = BaseBuilder.fieldGenericTypesForClass(field, ArrayList.class);
		assertArrayEquals(new Class<?>[] {Integer.class}, classes);
	}

	@Test
	public void it_should_discover_interface_parameterized_type_argument() throws NoSuchFieldException, SecurityException {
		Field field = TestObject.class.getDeclaredField("integerArrayList");
		Class<?>[] classes = BaseBuilder.fieldGenericTypesForClass(field, Iterable.class);
		assertArrayEquals(new Class<?>[] {Integer.class}, classes);
	}

	@Test
	public void it_should_discover_parameterized_generic_type_argument() throws NoSuchFieldException, SecurityException {
		Field field = TestObject.class.getDeclaredField("dateSet");
		Class<?>[] classes = BaseBuilder.fieldGenericTypesForClass(field, Set.class);
		assertArrayEquals(new Class<?>[] {Map.class}, classes);
	}

	@Test
	public void it_should_discover_wildcard_type_argument() throws NoSuchFieldException, SecurityException {
		Field field = TestObject.class.getDeclaredField("lowerBoundStringList");
		Class<?>[] classes = BaseBuilder.fieldGenericTypesForClass(field, Collection.class);
		assertArrayEquals(new Class<?>[] {String.class}, classes);
	}

	@Test
	public void it_should_discover_parameterizeds_type_argument() throws NoSuchFieldException, SecurityException {
		Field field = TestObject.class.getDeclaredField("genericTestObject");
		Class<?>[] classes = BaseBuilder.fieldGenericTypesForClass(field, Comparable.class);
		assertArrayEquals(new Class<?>[] {Integer.class}, classes);
	}

	@Test
	public void it_should_set_primitive_types() throws MojoExecutionException {
		ApiDeclaration apiDeclaration = ApiDeclarationBuilder.buidOf(TestObject.class);
		List<ParameterObject> params = apiDeclaration.apis.get(0).operations.get(0).parameters;
		assertEquals(15, params.size());
		assertPrimitive(ItemsObject.INTEGER, params.get(0));
		assertPrimitive(ItemsObject.INTEGER, params.get(1));
		assertPrimitive(ItemsObject.LONG, params.get(2));
		assertPrimitive(ItemsObject.LONG, params.get(3));
		assertPrimitive(ItemsObject.FLOAT, params.get(4));
		assertPrimitive(ItemsObject.FLOAT, params.get(5));
		assertPrimitive(ItemsObject.DOUBLE, params.get(6));
		assertPrimitive(ItemsObject.DOUBLE, params.get(7));
		assertPrimitive(ItemsObject.STRING, params.get(8));
		assertPrimitive(ItemsObject.BYTE, params.get(9));
		assertPrimitive(ItemsObject.BYTE, params.get(10));
		assertPrimitive(ItemsObject.BOOLEAN, params.get(11));
		assertPrimitive(ItemsObject.BOOLEAN, params.get(12));
		assertPrimitive(ItemsObject.DATE, params.get(13));
		assertPrimitive(ItemsObject.DATE_TIME, params.get(14));
	}

	@Test
	public void it_should_create_the_models() throws MojoExecutionException {
		ApiDeclaration apiDeclaration = ApiDeclarationBuilder.buidOf(TestObject.class);
		assertEquals(3, apiDeclaration.models.size());
		ModelObject three = apiDeclaration.models.get("Three");
		ModelObject map = apiDeclaration.models.get("Map"); // TODO handle map?
		ModelObject testObject = apiDeclaration.models.get("TestObject");
		assertNotNull(three);
		assertNotNull(map);
		assertNotNull(testObject);
		assertEquals("Three", three.id);
		assertEquals("Map", map.id);
		assertEquals("TestObject", testObject.id);
		assertEquals(0, three.properties.size());
		assertEquals(0, map.properties.size());
		assertEquals(7, testObject.properties.size());
		assertArrayOf(ItemsObject.INTEGER, testObject.properties.get("intArray"));
		assertArrayOf(ItemsObject.INTEGER, testObject.properties.get("integerArrayList"));
		assertArrayOf(new ItemsObject("Map"), testObject.properties.get("dateSet"));
		assertArrayOf(ItemsObject.STRING, testObject.properties.get("lowerBoundStringList"));
		assertArrayOf(ItemsObject.STRING, testObject.properties.get("genericTestObject"));
	}

	private static void assertPrimitive(ItemsObject primitive, ItemsObject item) {
		assertEquals(primitive.type, item.type);
		assertEquals(primitive.$ref, item.$ref);
		assertEquals(primitive.format, item.format);
	}

	private static void assertArrayOf(ItemsObject componentType, DataTypeFields array) {
		assertEquals("array", array.type);
		assertEquals(componentType.type, array.items.type);
		assertEquals(componentType.$ref, array.items.$ref);
		assertEquals(componentType.format, array.items.format);
	}

}
