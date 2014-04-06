package com.github.lucasaraujo.swagger.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class DataTypeFields extends ItemsObject {

	private static final long serialVersionUID = -7284080457367013235L;

	public String defaultValue;

	@JsonProperty("enum")
	public Enum<?>[] enun;

	public String minimum;

	public String maximum;

	public ItemsObject items;

	@JsonSerialize(include = Inclusion.NON_DEFAULT)
	public boolean uniqueItems;

}
