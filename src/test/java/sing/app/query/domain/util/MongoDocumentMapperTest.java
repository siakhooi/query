package sing.app.query.domain.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class MongoDocumentMapperTest {

    @Test
    void toMap_shouldReturnEmptyMap_whenDocumentIsNull() {
        // Act
        Map<String, Object> result = MongoDocumentMapper.toMap(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toMap_shouldReturnEmptyMap_whenDocumentIsEmpty() {
        // Arrange
        Document doc = new Document();

        // Act
        Map<String, Object> result = MongoDocumentMapper.toMap(doc);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toMap_shouldMapPrimitiveValues() {
        // Arrange
        Document doc = new Document()
            .append("string", "test")
            .append("number", 123)
            .append("boolean", true);

        // Act
        Map<String, Object> result = MongoDocumentMapper.toMap(doc);

        // Assert
        assertEquals(3, result.size());
        assertEquals("test", result.get("string"));
        assertEquals(123, result.get("number"));
        assertEquals(true, result.get("boolean"));
    }

    @Test
    void toMap_shouldConvertObjectIdToString() {
        // Arrange
        ObjectId objectId = new ObjectId();
        Document doc = new Document("id", objectId);

        // Act
        Map<String, Object> result = MongoDocumentMapper.toMap(doc);

        // Assert
        assertEquals(1, result.size());
        assertEquals(objectId.toString(), result.get("id"));
        assertTrue(result.get("id") instanceof String);
    }

    @Test
    void toMap_shouldHandleNestedDocuments() {
        // Arrange
        Document nestedDoc = new Document("nestedKey", "nestedValue");
        Document doc = new Document("topLevel", nestedDoc);

        // Act
        Map<String, Object> result = MongoDocumentMapper.toMap(doc);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get("topLevel") instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) result.get("topLevel");
        assertEquals("nestedValue", nestedMap.get("nestedKey"));
    }

    @Test
    void toMap_shouldHandleMixedContent() {
        // Arrange
        ObjectId objectId = new ObjectId();
        Document nestedDoc = new Document("nestedKey", "nestedValue");
        Document doc = new Document()
            .append("id", objectId)
            .append("name", "test")
            .append("nested", nestedDoc)
            .append("active", true);

        // Act
        Map<String, Object> result = MongoDocumentMapper.toMap(doc);

        // Assert
        assertEquals(4, result.size());
        assertEquals(objectId.toString(), result.get("id"));
        assertEquals("test", result.get("name"));
        assertEquals(true, result.get("active"));

        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) result.get("nested");
        assertEquals("nestedValue", nestedMap.get("nestedKey"));
    }
}
