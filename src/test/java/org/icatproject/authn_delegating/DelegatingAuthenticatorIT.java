package org.icatproject.authn_delegating;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DelegatingAuthenticatorIT {

	@BeforeAll
	public static void init() {
		basePath = "/authn.delegating";
		baseURI = System.getProperty("serverUrl");
	}

	@Test
	public void testGetDescription() {
		given()
			.when()
				.get("/description")
			.then()
				.statusCode(200)
				.body("keys*.name", hasItems("username", "mechanism", "token"))
				.body("keys.find { it.name == 'token' }.hide", is(true));
	}

	@Test
	public void testAuthenticate() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(200)
				.body("username", is("test_username"))
				.body("mechanism", is("test_mechanism"));
	}

	@Test
	public void testAuthenticateNullMechanism() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": null}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(200)
				.body("username", is("test_username"))
				.body("mechanism", nullValue());
	}

	@Test
	public void testAuthenticateNoMechanism() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"token\": \"AAAAAA\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(200)
				.body("username", is("test_username"))
				.body("mechanism", nullValue());
	}

	@Test
	public void testAuthenticateWrongIp() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.2\", \"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(403);
	}

	@Test
	public void testAuthenticateNullIp() throws Exception {
		given()
			.formParam("json", "{\"ip\": null, \"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}
	@Test
	public void testAuthenticateNoIp() throws Exception {
		given()
			.formParam("json", "{\"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}

	@Test
	public void testAuthenticateInvalidIp() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"aa7g0haei\", \"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(403);
	}

	@Test
	public void testAuthenticateWrongToken() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"token\": \"BBBBBB\"}, {\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(403);
	}

	@Test
	public void testAuthenticateNullToken() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"token\": null}, {\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}

	@Test
	public void testAuthenticateNoToken() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"mechanism\": \"test_mechanism\"}, {\"username\": \"test_username\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}

	@Test
	public void testAuthenticateNullUsername() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": \"test_mechanism\"}, {\"username\": null}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}

	@Test
	public void testAuthenticateNoUsername() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\", \"credentials\": [{\"token\": \"AAAAAA\"}, {\"mechanism\": \"test_mechanism\"}]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}

	@Test
	public void testAuthenticateNoCredentials() throws Exception {
		given()
			.formParam("json", "{\"ip\": \"127.0.0.1\"}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}

	@Test
	public void testAuthenticateInvalidJson() throws Exception {
		given()
			.formParam("json", "{adfhg[ahfd\"]}")
			.when()
				.post("/authenticate")
			.then()
				.statusCode(400);
	}
}
