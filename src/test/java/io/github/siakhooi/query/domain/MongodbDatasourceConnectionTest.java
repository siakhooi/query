package io.github.siakhooi.query.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import io.github.siakhooi.query.config.MongoQuery;

@ExtendWith(MockitoExtension.class)
class MongodbDatasourceConnectionTest {

    @Mock
    private MongoClient mongoClient;

    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private MongoCollection<Document> mongoCollection;

    @Mock
    private FindIterable<Document> findIterable;

    @Mock
    private AggregateIterable<Document> aggregateIterable;

    private MongodbDatasourceConnection datasourceConnection;
    private final String testDatabaseName = "testDB";

    @BeforeEach
    void setUp() {
        datasourceConnection = new MongodbDatasourceConnection(mongoClient, testDatabaseName);
    }

    @Test
    void execute_shouldExecuteSimpleFind_whenNoAggregationNeeded() {
        // Arrange
        MongoQuery query = createBasicQuery();
        Document testDoc = new Document("name", "test").append("value", 123);

        when(mongoClient.getDatabase(testDatabaseName)).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("testCollection")).thenReturn(mongoCollection);
        when(mongoCollection.find()).thenReturn(findIterable);
        stubMongoIterableForEach(findIterable, testDoc);

        // Act
        List<Map<String, Object>> results = datasourceConnection.execute("testQuery", query);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertNotNull(results.get(0).get("name"));
        assertNotNull(results.get(0).get("value"));
        verify(mongoClient).getDatabase(testDatabaseName);
        verify(mongoDatabase).getCollection("testCollection");
        verify(mongoCollection).find();
    }

    @Test
    void execute_shouldExecuteAggregation_whenAggregationNeeded() {
        // Arrange
        MongoQuery query = new MongoQuery("testCollection", "{status: 'active'}", null, null, null);

        Document testDoc = new Document("name", "test").append("status", "active");

        when(mongoClient.getDatabase(testDatabaseName)).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("testCollection")).thenReturn(mongoCollection);
        when(mongoCollection.aggregate(any())).thenReturn(aggregateIterable);
        stubMongoIterableForEach(aggregateIterable, testDoc);

        // Act
        List<Map<String, Object>> results = datasourceConnection.execute("testQuery", query);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertNotNull(results.get(0).get("name"));
        assertNotNull(results.get(0).get("status"));
        verify(mongoCollection).aggregate(any());
    }

    @Test
    void execute_shouldThrowException_whenMongoErrorOccurs() {
        // Arrange
        MongoQuery query = createBasicQuery();

        when(mongoClient.getDatabase(testDatabaseName)).thenThrow(new MongoException("Connection failed"));

        // Act & Assert
        MongoQueryExecutionException exception = assertThrows(
            MongoQueryExecutionException.class,
            () -> datasourceConnection.execute("testQuery", query)
        );
        assertTrue(exception.getMessage().contains("MongoDB driver error"));
        verify(mongoClient).getDatabase(testDatabaseName);
        verifyNoMoreInteractions(mongoClient);
    }

    @Test
    void execute_shouldThrowException_whenQueryIsNull() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> datasourceConnection.execute("testQuery", null)
        );
    }

    @Test
    void execute_shouldThrowException_whenQueryIsInvalid() {
        // Arrange
        MongoQuery query = new MongoQuery(null, null, null, null, null); // Missing collection

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> datasourceConnection.execute("testQuery", query)
        );
    }

    @Test
    void execute_shouldHandleEmptyResultSet() {
        // Arrange
        MongoQuery query = createBasicQuery();

        when(mongoClient.getDatabase(testDatabaseName)).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("testCollection")).thenReturn(mongoCollection);
        when(mongoCollection.find()).thenReturn(findIterable);
        stubMongoIterableForEach(findIterable);

        // Act
        List<Map<String, Object>> results = datasourceConnection.execute("testQuery", query);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(mongoClient).getDatabase(testDatabaseName);
        verify(mongoDatabase).getCollection("testCollection");
        verify(mongoCollection).find();
    }

    @Test
    void execute_shouldRethrowIllegalArgumentException_whenThrownDuringExecution() {
        MongoQuery query = createBasicQuery();

        when(mongoClient.getDatabase(testDatabaseName)).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("testCollection")).thenReturn(mongoCollection);
        when(mongoCollection.find()).thenReturn(findIterable);
        doThrow(new IllegalArgumentException("invalid cursor")).when(findIterable).forEach(any());

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> datasourceConnection.execute("testQuery", query));

        assertEquals("invalid cursor", thrown.getMessage());
    }

    @Test
    void execute_shouldWrapUnexpectedRuntimeException_inMongoQueryExecutionException() {
        MongoQuery query = createBasicQuery();

        when(mongoClient.getDatabase(testDatabaseName)).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("testCollection")).thenReturn(mongoCollection);
        when(mongoCollection.find()).thenReturn(findIterable);
        var cause = new IllegalStateException("unexpected failure");
        doThrow(cause).when(findIterable).forEach(any());

        MongoQueryExecutionException thrown = assertThrows(
                MongoQueryExecutionException.class,
                () -> datasourceConnection.execute("testQuery", query));

        assertTrue(thrown.getMessage().contains("Unexpected error executing MongoDB query"));
        assertSame(cause, thrown.getCause());
    }

    @Test
    void execute_shouldWrapRuntimeExceptionFromAggregationPath() {
        MongoQuery query = new MongoQuery("testCollection", "{ \"x\": 1 }", null, null, null);

        when(mongoClient.getDatabase(testDatabaseName)).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("testCollection")).thenReturn(mongoCollection);
        when(mongoCollection.aggregate(any())).thenReturn(aggregateIterable);
        var cause = new UnsupportedOperationException("aggregate failed");
        doThrow(cause).when(aggregateIterable).forEach(any());

        MongoQueryExecutionException thrown = assertThrows(
                MongoQueryExecutionException.class,
                () -> datasourceConnection.execute("testQuery", query));

        assertTrue(thrown.getMessage().contains("Unexpected error executing MongoDB query"));
        assertSame(cause, thrown.getCause());
    }

    private void stubMongoIterableForEach(MongoIterable<Document> iterable, Document... documents) {
        doAnswer(invocation -> {
            Consumer<Document> consumer = invocation.getArgument(0);
            for (Document document : documents) {
                consumer.accept(document);
            }
            return null;
        }).when(iterable).forEach(any());
    }

    private MongoQuery createBasicQuery() {
        return new MongoQuery("testCollection", null, null, null, null);
    }
}
