package com.github.lucasaraujo.swagger.model;

import java.io.Serializable;

/**
 * The Resource Listing serves as the root document for the API description. It
 * contains general information about the API and an inventory of the available
 * resources.
 *
 * @see <a
 *      href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#512-resource-object">Swagger
 *      RESTful API Documentation</a>
 *
 * @author Lucas Araújo
 *
 */
public class ResourceObject implements Serializable {

	private static final long serialVersionUID = -5638281933740749007L;

	/**
	 * <b>Required.</b> A relative path to the API declaration from the path
	 * used to retrieve the {@link ResourceListing}. This path does not
	 * necessarily have to correspond to the URL which actually serves this
	 * resource in the API but rather where the resource listing itself is
	 * served. The value SHOULD be in a relative (URL) path format.
	 */
	public String path;

	/**
	 * <i>Recommended.</i> A short description of the resource.
	 */
	public String description;

}
