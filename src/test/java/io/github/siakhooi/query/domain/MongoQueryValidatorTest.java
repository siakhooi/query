package io.github.siakhooi.query.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.siakhooi.query.config.MongoQuery;

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
        MongoQuery query = new MongoQuery(null, null, null, null, null);

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
        MongoQuery query = new MongoQuery(" ", null, null, null, null);

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
        MongoQuery query = new MongoQuery("testCollection", null, null, null, null);

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
        MongoQuery query = new MongoQuery("test", null, null, null, null);

        // Act & Assert
        assertFalse(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenPipelineIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery(null, null, null, null, "[{$match: {status: 'active'}}]");

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenFilterIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery(null, "{status: 'active'}", null, null, null);

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenFieldsIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery(null, null, "{name: 1, status: 1}", null, null);

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenSortIsSet() {
        // Arrange
        MongoQuery query = new MongoQuery(null, null, null, "{createdAt: -1}", null);

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenMultipleFieldsAreSet() {
        // Arrange
        MongoQuery query = new MongoQuery(
                null,
                "{status: 'active'}",
                "{name: 1, status: 1}",
                "{createdAt: -1}",
                null);

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnFalse_whenFieldsContainOnlyWhitespace() {
        // Arrange
        MongoQuery query = new MongoQuery(null, "   ", "  ", "", null);

        // Act & Assert
        assertFalse(MongoQueryValidator.shouldUseAggregation(query));
    }

    @Test
    void shouldUseAggregation_shouldReturnTrue_whenFieldsContainContent() {
        // Arrange
        MongoQuery query = new MongoQuery(null, "  {}", null, null, null);

        // Act & Assert
        assertTrue(MongoQueryValidator.shouldUseAggregation(query));
    }
}
