package sing.app.query.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sing.app.query.config.MongoQuery;

class MongoQueryValidatorTest {

    @Test
    void validateQuery_shouldThrowException_whenQueryIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> MongoQueryValidator.validateQuery(null)
        );
        assertEquals("MongoQuery cannot be null", exception.getMessage());
    }

    @Test
    void validateQuery_shouldThrowException_whenCollectionIsNull() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setCollection(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> MongoQueryValidator.validateQuery(query)
        );
        assertEquals("MongoDB query requires 'collection'", exception.getMessage());
    }

    @Test
    void validateQuery_shouldThrowException_whenCollectionIsBlank() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setCollection(" ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> MongoQueryValidator.validateQuery(query)
        );
        assertEquals("MongoDB query requires 'collection'", exception.getMessage());
    }

    @Test
    void validateQuery_shouldNotThrow_whenCollectionIsValid() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setCollection("testCollection");

        // Act & Assert (should not throw)
        assertDoesNotThrow(() -> MongoQueryValidator.validateQuery(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnFalse_whenQueryIsNull() {
        // Act & Assert
        assertFalse(MongoQueryValidator.shouldUseAggregation(null));
    }

    @Test
    void shouldUseAggregation_shouldReturnFalse_whenNoAggregationFieldsAreSet() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setCollection("test");

        // Act & Assert
        assertFalse(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenPipelineIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setPipeline("[{$match: {status: 'active'}}]");

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenFilterIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter("{status: 'active'}");

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenFieldsIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFields("{name: 1, status: 1}");

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenSortIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setSort("{createdAt: -1}");

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenMultipleFieldsAreSet() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter("{status: 'active'}");
        query.setFields("{name: 1, status: 1}");
        query.setSort("{createdAt: -1}");

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnFalse_whenFieldsContainOnlyWhitespace() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter("   ");  // Whitespace only
        query.setFields("  ");   // Whitespace only
        query.setSort("");       // Empty string

        // Act & Assert
        assertFalse(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenFieldsContainContent() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter("  {}");  // Whitespace with content

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }
}
