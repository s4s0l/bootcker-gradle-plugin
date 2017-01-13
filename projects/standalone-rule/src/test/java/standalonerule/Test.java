package standalonerule;

import static com.palantir.docker.compose.connection.waiting.HealthChecks.toRespondOverHttp;
import static io.restassured.RestAssured.when;
import static java.lang.System.getProperty;
import static org.hamcrest.Matchers.equalTo;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.joda.time.Duration;
import org.junit.ClassRule;

/**
 * @author Matcin Wielgus
 */
public class Test {


	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file(System.getProperty("bootcker.someTest"))
			.waitingForService("me", toRespondOverHttp(8080, (port) ->
					port.inFormat("http://$HOST:$EXTERNAL_PORT")), Duration.standardMinutes(1))
			.build();

	@org.junit.Test
	public void checkIfComposedProjectStarted() {
		DockerPort mePort = docker.containers()
				.container("me")
				.port(8080);

		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.baseURI = mePort.inFormat("http://$HOST:$EXTERNAL_PORT");
		// @formatter:off
        Response S = when()
                .get("/");
        S.then()
                .statusCode(200)
                .body(equalTo("Hello World!"));
        // @formatter:off
    }
}
