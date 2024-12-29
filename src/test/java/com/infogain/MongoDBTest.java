package com.infogain;

import static com.infogain.factories.TestDataFactory.createRandomStudent;
import static com.infogain.utils.ConfigUtil.CONFIG;
import static com.infogain.utils.JsonUtil.serialize;
import static org.assertj.core.api.Assertions.assertThat;

import com.infogain.annotations.FrameworkAnnotaions;
import com.infogain.customexceptions.MongoDBException;
import com.infogain.pojo.Student;
import com.infogain.utils.MongoDBUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class MongoDBTest {

  private static final String CONNECTION_STRING = CONFIG.getString("CONNECTION_STRING");
  private static final String DATABASE_NAME = CONFIG.getString("DATABASE_NAME");
  private static final String COLLECTION_NAME = CONFIG.getString("COLLECTION_NAME");

  private MongoDBUtil mongoDBUtil;

  @BeforeEach
  void setUp() throws MongoDBException {
    mongoDBUtil =
        MongoDBUtil.getInstance()
            .connectClient(CONNECTION_STRING)
            .useDatabase(DATABASE_NAME)
            .useCollection(COLLECTION_NAME);
  }

  @AfterEach
  void tearDown() throws MongoDBException {
    mongoDBUtil.closeClient();
  }

  @Test
  @FrameworkAnnotaions(Author = "Ravi")
  @SneakyThrows
  void shouldInsertStudentDocumentIntoMongoDB() {
    // Arrange
    Student student = createRandomStudent();
    String strStudent = serialize(student);
    Document document = Document.parse(strStudent);

    // Act
    ObjectId insertedId = mongoDBUtil.insertDocument(document).getInsertedId();

    // Assert
    assertThat(insertedId).as("Inserted document ID is not null").isNotNull();

    log.info("Document inserted with ID: {}", insertedId);
  }
}
