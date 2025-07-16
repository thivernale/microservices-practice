import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CustomerIntegrationTests {
    private static final Log LOG = LogFactory.getLog(CustomerIntegrationTests.class);

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8090;
    }

    @Test
    public void shouldReturnCustomersWithValidToken() {
        // TODO write helper to get auth token
        String token = "";

        Response response = RestAssured.given()
            //.header("Authorization", "Bearer " + token)
            .when()
            .get("/api/customer")
            .then()
            .statusCode(200)
            .body("$", CoreMatchers.notNullValue())
            .extract()
            .response();

        LOG.info(response.jsonPath()
            .get("$"));
        LOG.info(response.jsonPath()
            .get("[0].id"));
    }
}
