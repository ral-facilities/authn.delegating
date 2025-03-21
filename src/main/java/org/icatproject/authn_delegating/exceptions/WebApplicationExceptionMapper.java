package org.icatproject.authn_delegating.exceptions;

import java.util.Map;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	private static final Logger logger = LoggerFactory.getLogger(WebApplicationException.class);
	private static final Jsonb jsonb = JsonbBuilder.create();

	@Override
	public Response toResponse(WebApplicationException e) {

		logger.info("{}", e.toString());

		int statusCode = e.getResponse().getStatus();

		String entity = jsonb.toJson(Map.of(
			"code", String.valueOf(statusCode),
			"message", e.getMessage()
		));

		return Response
			.status(statusCode)
			.entity(entity)
			.build();
	}
}
