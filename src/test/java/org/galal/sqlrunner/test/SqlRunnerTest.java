package org.galal.sqlrunner.test;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.galal.sqlrunner.test.utils.Sql;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.galal.sqlrunner.test.utils.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.galal.sqlrunner.test.utils.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.galal.sqlrunner.test.utils.TestUtils.readTestResourceAsString;
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
    @Sql(scripts = "sql/test_data_insert.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "sql/clear_test_data.sql", executionPhase = AFTER_TEST_METHOD)
    void runSqlFileTest() {
        String expectedJsonStr = readTestResourceAsString("json/expected_data.json");
        given()
                .when()
                .auth()
                .basic(username, password)
                .get("/run/sql/query_this.sql")
                .peek()
                .then()
                .statusCode(200)
                .body(jsonEquals(expectedJsonStr));
    }


    @Test
    @Sql(scripts = "sql/test_data_insert.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "sql/clear_test_data.sql", executionPhase = AFTER_TEST_METHOD)
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
    @Sql(scripts = "sql/test_data_insert.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "sql/clear_test_data.sql", executionPhase = AFTER_TEST_METHOD)
    void runSqlFileWithParamsTest() {
        String expectedJsonStr = readTestResourceAsString("json/expected_only_bmw.json");

        var response =
                given()
                        .when()
                        .auth()
                        .basic(username, password)
                        .queryParam("brand_name", "BMW")
                        .get("/run/sql/query_with_params.sql")
                        .peek()
                        .then()
                        .statusCode(200)
                        .body(jsonEquals(expectedJsonStr));
    }


    @Test
    @Sql(scripts = "sql/test_data_insert.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "sql/clear_test_data.sql", executionPhase = AFTER_TEST_METHOD)
    void runSqlFileWithNullParamsTest() {
        String expectedJsonStr = "[]";

        var response =
                given()
                        .when()
                        .auth()
                        .basic(username, password)
                        .queryParam("brand_name")
                        .get("/run/sql/query_with_params.sql")
                        .peek()
                        .then()
                        .statusCode(200)
                        .body(jsonEquals(expectedJsonStr));
    }


    @Test
    @Sql(scripts = "sql/test_data_insert.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = "sql/clear_test_data.sql", executionPhase = AFTER_TEST_METHOD)
    void runSqlFileWithMissingParamsTest() {
            given()
                    .when()
                    .auth()
                    .basic(username, password)
                    .get("/run/sql/query_with_params.sql")
                    .then()
                    .statusCode(500);
    }
}