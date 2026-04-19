package sing.app.query.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class QueryConfigTest {

    private QueryConfig config;

    @BeforeEach
    void setUp() {
        config = new QueryConfig();

        QueryConfig.Query query1 =
                new QueryConfig.Query("q1", "SELECT * FROM table1", null, "conn1");

        QueryConfig.Query query2 =
                new QueryConfig.Query("q2", "SELECT * FROM table2", null, "conn2");

        QueryConfig.Queryset queryset1 = new QueryConfig.Queryset("set1", List.of(query1, query2));

        QueryConfig.Query query3 =
                new QueryConfig.Query("q3", "SELECT * FROM table3", null, "conn3");

        QueryConfig.Queryset queryset2 = new QueryConfig.Queryset("set2", List.of(query3));

        config.setQuerysets(List.of(queryset1, queryset2));
    }

    @Test
    void testGetQueriesReturnsCorrectQueries() {
        List<QueryConfig.Query> queries = config.getQueries("set1");
        assertEquals(2, queries.size());
        assertEquals("q1", queries.get(0).name());
        assertEquals("q2", queries.get(1).name());

        List<QueryConfig.Query> queries2 = config.getQueries("set2");
        assertEquals(1, queries2.size());
        assertEquals("q3", queries2.get(0).name());
    }

    @Test
    void testGetQueriesReturnsEmptyListForUnknownQueryset() {
        List<QueryConfig.Query> queries = config.getQueries("unknown");
        assertTrue(queries.isEmpty());
    }

    @Test
    void testQuerysetAndQueryAccessors() {
        QueryConfig.Queryset qs = new QueryConfig.Queryset("testset", List.of());
        assertEquals("testset", qs.name());
        assertTrue(qs.queries().isEmpty());

        MongoQuery mongoQuery = new MongoQuery("books", "{\"genre\":\"Fiction\"}", null, null, null);
        QueryConfig.Query q = new QueryConfig.Query("testquery", "SELECT 1", mongoQuery, "testconn");

        assertEquals("testquery", q.name());
        assertEquals("SELECT 1", q.queryString());
        assertEquals("books", q.mongoQuery().collection());
        assertEquals("{\"genre\":\"Fiction\"}", q.mongoQuery().filter());
        assertEquals("testconn", q.connection());
    }

    @Test
    void testDefaultQuerysetsIsEmptyList() {
        QueryConfig emptyConfig = new QueryConfig();
        assertNotNull(emptyConfig.getQuerysets());
        assertTrue(emptyConfig.getQuerysets().isEmpty());
    }
}