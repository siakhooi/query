package sing.app.query.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bson.Document;
import org.bson.json.JsonParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import sing.app.query.config.MongoQuery;

@ExtendWith(MockitoExtension.class)
class MongoPipelineBuilderTest {

    @Test
    void build_shouldReturnEmptyList_whenNoStages() {
        // Arrange
        MongoQuery query = new MongoQuery();
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertNotNull(pipeline);
        assertTrue(pipeline.isEmpty());
    }

    @Test
    void build_shouldAddMatchStage_whenFilterIsProvided() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter("{'status': 'active'}");
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertEquals(1, pipeline.size());
        Document matchStage = pipeline.get(0);
        assertTrue(matchStage.containsKey("$match"));
        assertEquals("active", matchStage.get("$match", Document.class).getString("status"));
    }

    @Test
    void build_shouldAddProjectStage_whenFieldsAreProvided() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFields("{'name': 1, 'age': 1}");
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertEquals(1, pipeline.size());
        Document projectStage = pipeline.get(0);
        assertTrue(projectStage.containsKey("$project"));
        Document projectFields = projectStage.get("$project", Document.class);
        assertEquals(1, projectFields.getInteger("name"));
        assertEquals(1, projectFields.getInteger("age"));
    }

    @Test
    void build_shouldAddSortStage_whenSortIsProvided() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setSort("{'createdAt': -1}");
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertEquals(1, pipeline.size());
        Document sortStage = pipeline.get(0);
        assertTrue(sortStage.containsKey("$sort"));
        assertEquals(-1, sortStage.get("$sort", Document.class).getInteger("createdAt"));
    }

    @Test
    void build_shouldParsePipelineStages_whenPipelineIsProvided() {
        // Arrange
        MongoQuery query = new MongoQuery();
        String pipelineJson = "[{'$match': {'status': 'active'}}, {'$limit': 10}]";
        query.setPipeline(pipelineJson);
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertEquals(2, pipeline.size());

        // Verify first stage ($match)
        Document matchStage = pipeline.get(0);
        assertTrue(matchStage.containsKey("$match"));
        assertEquals("active", matchStage.get("$match", Document.class).getString("status"));

        // Verify second stage ($limit)
        Document limitStage = pipeline.get(1);
        assertTrue(limitStage.containsKey("$limit"));
        assertEquals(10, limitStage.getInteger("$limit"));
    }

    @Test
    void build_shouldHandleEmptyOrWhitespaceValues() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter(" ");
        query.setFields("");
        query.setSort("  ");
        query.setPipeline("");
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertNotNull(pipeline);
        assertTrue(pipeline.isEmpty());
    }

    @Test
    void build_shouldHandleNullValues() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter(null);
        query.setFields(null);
        query.setSort(null);
        query.setPipeline(null);
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertNotNull(pipeline);
        assertTrue(pipeline.isEmpty());
    }

    @Test
    void build_shouldMaintainCorrectOrderOfStages() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setFilter("{'status': 'active'}");
        query.setFields("{'name': 1}");
        query.setSort("{'createdAt': -1}");
        query.setPipeline("[{'$limit': 5}]");

        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertEquals(4, pipeline.size());

        // Order should be: match (from filter), pipeline stages, project, sort
        assertTrue(pipeline.get(0).containsKey("$match"));
        assertTrue(pipeline.get(1).containsKey("$limit"));
        assertTrue(pipeline.get(2).containsKey("$project"));
        assertTrue(pipeline.get(3).containsKey("$sort"));
    }

    @Test
    void build_shouldHandleEmptyPipelineArray() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setPipeline("[]");
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act
        List<Document> pipeline = builder.build();

        // Assert
        assertNotNull(pipeline);
        assertTrue(pipeline.isEmpty());
    }

    @Test
    void build_shouldThrowExceptionForInvalidPipelineJson() {
        // Arrange
        MongoQuery query = new MongoQuery();
        query.setPipeline("invalid json");
        MongoPipelineBuilder builder = new MongoPipelineBuilder(query);

        // Act & Assert
        assertThrows(JsonParseException.class, builder::build);
    }
}
