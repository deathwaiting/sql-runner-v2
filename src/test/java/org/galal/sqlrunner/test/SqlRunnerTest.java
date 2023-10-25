package org.galal.sqlrunner.test;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.galal.sqlrunner.test.TestUtils.readTestResourceAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class SqlRunnerTest {


    @ConfigProperty(name = "org.galal.sql_runner.directory", defaultValue = "sql")
    String directoryPath;

    @ConfigProperty(name = "quarkus.security.users.embedded.plain-text", defaultValue = "true")
    Boolean isSecurityInPlainText;

    @ConfigProperty(name = "quarkus.security.users.embedded.realm-name", defaultValue = "true")
    String realmName;


    private final String username = "test-admin";
    private final String password = "d0ntUseTh1s";

    @Test
    void runSqlFileTest() {
        String expectedJsonStr = readTestResourceAsString("json/expected_data.json");
        var response =
                given()
                        .when()
                        .auth()
                        .basic(username, password)
                        .get("/sql/query_this.sql")
                        .peek();

        response
                .then()
                .statusCode(200)
                .body(jsonEquals(expectedJsonStr));
    }


    @Test
    void fileContentCachingTest() {
        var expectedJsonStr = readTestResourceAsString("json/expected_data.json");

        given()
                .when()
                .auth()
                .basic(username, password)
                .get("/sql/query_this.sql")
                .then()
                .statusCode(200)
                .body(jsonEquals(expectedJsonStr));

        //read the file again
        given()
                .when()
                .auth()
                .basic(username, password)
                .get("/sql/query_this.sql")
                .then()
                .statusCode(200)
                .body(jsonEquals(expectedJsonStr));
    }



    @Test
    void testSecurityProps(){
        assertFalse(isSecurityInPlainText);
        assertEquals("sql-runner", realmName);
    }


    @Test
    void runSqlFileWithParamsTest() {
        String expectedJsonStr = readTestResourceAsString("json/expected_only_bmw.json");

        var response =
                given()
                        .when()
                        .auth()
                        .basic(username, password)
                        .queryParam("brand_name", "BMW")
                        .get("/sql/query_with_params.sql")
                        .peek();
        response
                .then()
                .statusCode(200)
                .body(jsonEquals(expectedJsonStr));
    }


    @Test
    void runSqlFileWithNullParamsTest() {
        String expectedJsonStr = "[]";

        var response =
                given()
                        .when()
                        .auth()
                        .basic(username, password)
                        .queryParam("brand_name")
                        .get("/sql/query_with_params.sql")
                        .peek();
        response
                .then()
                .statusCode(200)
                .body(jsonEquals(expectedJsonStr));
    }


    @Test
    void runSqlFileWithMissingParamsTest() {
            given()
                    .when()
                    .auth()
                    .basic(username, password)
                    .get("/sql/query_with_params.sql")
                    .then()
                    .statusCode(500);
    }
}