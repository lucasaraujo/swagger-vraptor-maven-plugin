package com.github.lucasaraujo.swagger.model;

import java.io.Serializable;
import java.util.List;

/**
 * The API Object describes one or more operations on a single {@link #path}. In
 * the {@link ApiDeclaration#apis} array, there MUST be only one
 * {@link ApiObject} per path.
 *
 * @see <a
 *      href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#522-api-object">Swagger
 *      RESTful API Documentation</a>
 *
 * @author Lucas Araújo
 *
 */
public class ApiObject implements Serializable {

	private static final long serialVersionUID = -4746639445175635795L;

	/**
	 * <b>Required.</b> The relative path to the operation, from the
	 * {@link ApiDeclaration#basePath}, which this operation describes. The
	 * value SHOULD be in a relative (URL) path format.
	 */
	public String path;

	/**
	 * <i>Recommended.</i> A short description of the resource.
	 */
	public String description;

	/**
	 * <b>Required.</b> A list of the API operations available on this {@link #path}. The array
	 * may include 0 or more operations.
	 */
	public List<OperationObject> operations;
}
