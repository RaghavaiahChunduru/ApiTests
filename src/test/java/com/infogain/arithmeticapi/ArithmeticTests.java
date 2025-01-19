package com.infogain.arithmeticapi;

import static com.infogain.enums.Author.RAVI;
import static com.infogain.enums.Category.REGRESSION;
import static com.infogain.enums.Service.ARITHMETIC_OPERATION;

import com.infogain.annotations.FrameworkAnnotations;
import com.infogain.api.arithmetic.ArithmeticAPI;
import com.infogain.api.arithmetic.ValidArithmeticResponse;
import com.infogain.asserts.ValidateDB;
import com.infogain.report.ExtentLogger;
import com.infogain.utils.JsonUtil;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@Slf4j
@FrameworkAnnotations(Author = { RAVI }, Category = REGRESSION, Service = ARITHMETIC_OPERATION)
public class ArithmeticTests {

  private static final String VALID_ARITHMETIC_OPERATION_SCHEMA_FILE_PATH = "schemas/valid-arithmetic-operation-schema.json";
  private static final String VALID_FACTORIAL_OPERATION_SCHEMA_FILE_PATH = "schemas/valid-factorial-operation-schema.json";

  private static final String INVALID_ARITHMETIC_OPERATION_SCHEMA_FILE_PATH = "schemas/invalid-arithmetic-operation-schema.json";

  private final ArithmeticAPI arithmeticAPI = ArithmeticAPI.getInstance();
  private ThreadLocal<Integer> id = new ThreadLocal<>();
  private final ValidateDB dbValidator = ValidateDB.getInstance();

  @ParameterizedTest(name = "{index} => firstOperand={0}, secondOperand={1}, expectedOutput={2}, expectedStatus={3}")
  @CsvFileSource(resources = "/addition-valid-testdata.csv", numLinesToSkip = 1)
  void assertValidAdditionOperations(Object firstOperand, Object secondOperand, String expectedOutput,
      int expectedStatus) {

    // Act
    Response response = arithmeticAPI.performAddition(firstOperand, secondOperand);
    ValidArithmeticResponse expectedEntry = JsonUtil.deserialize(response.getBody().asString(),
        ValidArithmeticResponse.class);
    id.set(response.getBody().jsonPath().getInt("id"));

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response, VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedStatus)
        .hasKeyWithValue("result", expectedOutput)
        .responseTimeBelow(2000)
        .containsKey("id")
        .doesNotContainsValue("error")
        .matchesSchema(VALID_ARITHMETIC_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entries
    dbValidator.validateEntryInArithmeticOperationTable(expectedEntry, id.get())
        .validateEntryInEventNotificationTable(expectedEntry, id.get());
  }

  @ParameterizedTest(name = "{index} => firstOperand={0}, secondOperand={1}, expectedStatus={2}, expectedCode={3},expectedMessage={4}")
  @CsvFileSource(resources = "/addition-invalid-testdata.csv", numLinesToSkip = 1)
  void assertInvalidAdditionOperations(Object firstOperand, Object secondOperand, String expectedStatus,
      int expectedCode, String expectedMessage) throws Exception {

    // Act
    Response response = arithmeticAPI.performAddition(firstOperand, secondOperand);
    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response,
        VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedCode)
        .hasKeyWithValue("status", expectedStatus)
        .hasKeyWithValue("message", expectedMessage)
        .responseTimeBelow(2000)
        .doesNotContainKey("id")
        .matchesSchema(INVALID_ARITHMETIC_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();
  }

  @ParameterizedTest(name = "{index} => firstOperand={0}, secondOperand={1}, expectedOutput={2}, expectedStatus={3}")
  @CsvFileSource(resources = "/division-valid-testdata.csv", numLinesToSkip = 1)
  void assertValidDivisionOperations(Object firstOperand, Object secondOperand, String expectedOutput,
      int expectedStatus) {

    // Act
    Response response = arithmeticAPI.performDivision(firstOperand, secondOperand);
    ValidArithmeticResponse expectedEntry = JsonUtil.deserialize(response.getBody().asString(),
        ValidArithmeticResponse.class);
    id.set(response.getBody().jsonPath().getInt("id"));

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response, VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedStatus)
        .hasKeyWithValue("result", expectedOutput)
        .responseTimeBelow(2000)
        .containsKey("id")
        .doesNotContainsValue("error")
        .matchesSchema(VALID_ARITHMETIC_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entries
    dbValidator.validateEntryInArithmeticOperationTable(expectedEntry, id.get())
        .validateEntryInEventNotificationTable(expectedEntry, id.get());
  }

  @ParameterizedTest(name = "{index} => firstOperand={0}, secondOperand={1}, expectedStatus={2}, expectedCode={3},expectedMessage={4}")
  @CsvFileSource(resources = "/division-invalid-testdata.csv", numLinesToSkip = 1)
  void assertInvalidDivisionOperations(Object firstOperand, Object secondOperand, String expectedStatus,
      int expectedCode, String expectedMessage) {

    // Act
    Response response = arithmeticAPI.performDivision(firstOperand, secondOperand);
    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response,
        VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedCode)
        .hasKeyWithValue("status", expectedStatus)
        .hasKeyWithValue("message", expectedMessage)
        .responseTimeBelow(2000)
        .doesNotContainKey("id")
        .matchesSchema(INVALID_ARITHMETIC_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();
  }

  @ParameterizedTest(name = "{index} => operand={0}, expectedOutput={1}, expectedStatus={2}")
  @CsvFileSource(resources = "/factorial-valid-testdata.csv", numLinesToSkip = 1)
  void assertValidFactorialOperation(Object operand, String expectedOutput, int expectedStatus) {

    // Act
    Response response = arithmeticAPI.getFactorial(operand);
    ValidArithmeticResponse expectedEntry = JsonUtil.deserialize(response.getBody().asString(),
        ValidArithmeticResponse.class);
    id.set(response.getBody().jsonPath().getInt("id"));

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response, VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedStatus)
        .hasKeyWithValue("result", expectedOutput)
        .responseTimeBelow(2000)
        .containsKey("id")
        .doesNotContainsValue("error")
        .matchesSchema(VALID_FACTORIAL_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entries
    dbValidator.validateEntryInArithmeticOperationTable(expectedEntry, id.get())
        .validateEntryInEventNotificationTable(expectedEntry, id.get());
  }

  @ParameterizedTest(name = "{index} => operand={0}, expectedStatus={1}, expectedCode={2},expectedMessage={3}")
  @CsvFileSource(resources = "/factorial-invalid-testdata.csv", numLinesToSkip = 1)
  void assertInvalidFactorialOperation(Object operand, String expectedStatus,
      int expectedCode, String expectedMessage) {

    // Act
    Response response = arithmeticAPI.getFactorial(operand);

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyArithmeticOperationResponse.assertThat(response,
        VerifyArithmeticOperationResponse.class)
        .statusCodeIs(expectedCode)
        .hasKeyWithValue("status", expectedStatus)
        .hasKeyWithValue("message", expectedMessage)
        .responseTimeBelow(2000)
        .doesNotContainKey("id")
        .matchesSchema(INVALID_ARITHMETIC_OPERATION_SCHEMA_FILE_PATH)
        .assertAll();
  }
}
