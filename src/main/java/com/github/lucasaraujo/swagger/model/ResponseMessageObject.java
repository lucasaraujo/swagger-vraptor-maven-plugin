package com.github.lucasaraujo.swagger.model;

import java.io.Serializable;

/**
 * The <code>ResponseMessageObject</code> describes a single possible response
 * message that can be returned from the operation call, and maps to the
 * {@link OperationObject#responseMessages} field in the {@link OperationObject}
 * . Each Response Message allows you to give further details as to why the HTTP
 * status code may be received.
 *
 * @see <a
 *      href="https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md#525-response-message-object">Swagger
 *      RESTful API Documentation</a>
 *
 * @author Lucas Araújo
 *
 */
public class ResponseMessageObject implements Serializable {

	private static final long serialVersionUID = -5661837640967025885L;

	/**
	 * <b>Required.</b> The HTTP status code returned. The value SHOULD be one
	 * of the status codes as described in <a
	 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">RFC 2616 -
	 * Section 10</a>.
	 */
	public int code;

	/**
	 * <b>Required.</b> The explanation for the status code. It SHOULD be the
	 * reason an error is received if an error status code is used.
	 */
	public String message;

	/**
	 * The return type for the given response.
	 */
	public String responseModel;

}
