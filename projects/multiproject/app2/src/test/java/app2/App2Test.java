package app2;

import static io.restassured.RestAssured.when;
import static java.lang.System.getProperty;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * @author Matcin Wielgus
 */
public class App2Test {

	@org.junit.Test
	public void checkIfOtherComposedProjectStarted() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.baseURI = "http://" + getProperty("other.host") + ":"
				+ getProperty("other.tcp.8080");
		// @formatter:off
        Response S = when()
                .get("/");
        S.then()
                .statusCode(200)
                .body(equalTo("Hello World, from App1. PropertyValue=null."));
        // @formatter:off
    }
}
