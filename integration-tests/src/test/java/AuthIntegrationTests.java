import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class AuthIntegrationTests {
    private static final Log LOG = LogFactory.getLog(AuthIntegrationTests.class);

    @BeforeAll
    static void beforeAll() {
        // API-Gateway
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    @Disabled
    public void shouldReturnOkWithValidToken() {
        final String loginPayload = """
            {
                "email": "",
                "password": "",
            }
            """;

        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .body("token", Matchers.notNullValue())
            .extract()
            .response();

        LOG.info("Generated token " + response.jsonPath()
            .getString("token"));
    }
}
