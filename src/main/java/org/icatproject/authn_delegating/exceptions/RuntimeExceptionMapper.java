package org.icatproject.authn_delegating.exceptions;

import java.util.Map;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionMapper.class);
	private static final Jsonb jsonb = JsonbBuilder.create();

	@Override
	public Response toResponse(RuntimeException e) {

		logger.error("Internal Server Error", e);

		String entity = jsonb.toJson(Map.of(
			"code", "500",
			"message", "Internal Server Error"
		));

		return Response
			.status(500)
			.entity(entity)
			.build();
	}
}
