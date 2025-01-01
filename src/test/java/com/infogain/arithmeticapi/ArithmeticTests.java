package com.infogain.arithmeticapi;

import com.infogain.api.arithmetic.ArithmeticAPI;
import com.infogain.api.arithmetic.ArithmeticResponse;
import com.infogain.asserts.ValidateDB;
import com.infogain.report.ExtentLogger;
import com.infogain.utils.JsonUtil;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@Slf4j
public class ArithmeticTests {

  private static final String ADD_OPERATION_SCHEMA_FILE_PATH = "schemas/add-operation-schema.json";
  private static final String DIVISION_OPERATION_SCHEMA_FILE_PATH = "schemas/division-operation-schema.json";
  private static final String FACTORIAL_OPERATION_SCHEMA_FILE_PATH = "schemas/factorial-operation-schema.json";

  private final ArithmeticAPI arithmeticAPI = ArithmeticAPI.getInstance();
  private ThreadLocal<Long> id = new ThreadLocal<>();
  private final ValidateDB dbValidator = ValidateDB.getInstance();

  @ParameterizedTest(name = "{index} => firstOperand={0}, secondOperand={1}, expectedOutput={2}, expectedStatus={3}")
  @CsvFileSource(resources = "/addition-testdata.csv", numLinesToSkip = 1)
  void assertAdditionOperations(String firstOperand, String secondOperand, String expectedOutput, int expectedStatus) {

    // Act
    Response response = arithmeticAPI.performAddition(firstOperand, secondOperand);
    ArithmeticResponse expectedEntry = JsonUtil.deserialize(response.toString(), ArithmeticResponse.class);
    id.set(response.getBody().jsonPath().getLong("id"));

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response, VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedStatus)
        .hasKeyWithValue("result", expectedOutput)
        .responseTimeBelow(2000)
        .containsValue("id")
        .doesNotContains("error")
        .matchesSchema(ADD_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entry
    dbValidator.validateArithmeticOperationEntryInDatabase(expectedEntry, id.get());
  }

  @ParameterizedTest(name = "{index} => firstOperand={0}, secondOperand={1}, expectedOutput={2}, expectedStatus={3}")
  @CsvFileSource(resources = "/division-testdata.csv", numLinesToSkip = 1)
  void assertDivisionOperations(String firstOperand, String secondOperand, String expectedOutput, int expectedStatus) {

    // Act
    Response response = arithmeticAPI.performDivision(firstOperand, secondOperand);
    ArithmeticResponse expectedEntry = JsonUtil.deserialize(response.toString(), ArithmeticResponse.class);
    id.set(response.getBody().jsonPath().getLong("id"));

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response, VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedStatus)
        .hasKeyWithValue("result", expectedOutput)
        .responseTimeBelow(2000)
        .containsValue("id")
        .doesNotContains("error")
        .matchesSchema(DIVISION_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entry
    dbValidator.validateArithmeticOperationEntryInDatabase(expectedEntry, id.get());
  }

  @ParameterizedTest(name = "{index} => operand={0}, expectedOutput={1}, expectedStatus={2}")
  @CsvFileSource(resources = "/factorial-testdata.csv", numLinesToSkip = 1)
  void assertFactorialOperation(String operand, String expectedOutput, int expectedStatus) {

    // Act
    Response response = arithmeticAPI.getFactorial(operand);
    ArithmeticResponse expectedEntry = JsonUtil.deserialize(response.toString(), ArithmeticResponse.class);
    id.set(response.getBody().jsonPath().getLong("id"));

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response, VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedStatus)
        .hasKeyWithValue("result", expectedOutput)
        .responseTimeBelow(2000)
        .containsValue("id")
        .doesNotContains("error")
        .matchesSchema(FACTORIAL_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entry
    dbValidator.validateArithmeticOperationEntryInDatabase(expectedEntry, id.get());
  }
}
