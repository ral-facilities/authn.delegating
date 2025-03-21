package org.icatproject.authn_delegating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.icatproject.authentication.PasswordChecker;
import org.icatproject.utils.AddressChecker;
import org.icatproject.utils.AddressCheckerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@ApplicationScoped
public class DelegatingAuthenticator {

	private static final Logger logger = LoggerFactory.getLogger(DelegatingAuthenticator.class);
	private static final Jsonb jsonb = JsonbBuilder.create();

	@Inject
	@ConfigProperty(name="authn_delegating.secretToken")
	String secretToken;

	@Inject
	@ConfigProperty(name="authn_delegating.ip")
	AddressChecker addressChecker;

	public static class RequestJsonParam {
		public String ip;
		public List<Map<String, String>> credentials;
	};

	@POST
	@Path("authenticate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> authenticate(@FormParam("json") String jsonString) {

		RequestJsonParam requestJsonParam;
		try {
			requestJsonParam = jsonb.fromJson(jsonString, RequestJsonParam.class);
		} catch (JsonbException e) {
			throw new BadRequestException("json parameter was not in the correct form");
		}

		if (requestJsonParam.ip == null || requestJsonParam.ip.isEmpty()) {
			throw new BadRequestException("ip is required");
		}
		try {
			if (!addressChecker.check(requestJsonParam.ip)) {
				throw new ForbiddenException("Authentication not allowed from your IP address");
			}
		} catch (AddressCheckerException e) {
			throw new ForbiddenException("Authentication not allowed from your IP address");
		}

		if (requestJsonParam.credentials == null || requestJsonParam.credentials.isEmpty()) {
			throw new BadRequestException("credentials is required");
		}
		Map<String, String> credentials = new HashMap<>();
		requestJsonParam.credentials.forEach((y) -> credentials.putAll(y));

		String mechanism = credentials.get("mechanism");
		String username = credentials.get("username");
		String token = credentials.get("token");

		if (token == null || token.isEmpty()) {
			throw new BadRequestException("token cannot be null or an empty string");
		}

		if (!PasswordChecker.verify(token, secretToken)) {
			throw new ForbiddenException("The token does not match");
		}

		if (mechanism != null && mechanism.isEmpty()) {
			throw new BadRequestException("mechanism cannot be an empty string");
		}

		if (username == null || username.isEmpty()) {
			throw new BadRequestException("username cannot be null or an empty string");
		}

		logger.info("Authenticated {}/{}", mechanism, username);

		if (mechanism == null) {
			return Map.of("username", username);
		} else {
			return Map.of("username", username, "mechanism", mechanism);
		}
	}

	@GET
	@Path("description")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getDescription() {
		return Map.of("keys", List.of(
			Map.of("name", "mechanism"),
			Map.of("name", "username"),
			Map.of("name", "token", "hide", true)
		));
	}
}
