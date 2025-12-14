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

        QueryConfig.Query query1 = new QueryConfig.Query();
        query1.setName("q1");
        query1.setQueryString("SELECT * FROM table1");
        query1.setConnection("conn1");

        QueryConfig.Query query2 = new QueryConfig.Query();
        query2.setName("q2");
        query2.setQueryString("SELECT * FROM table2");
        query2.setConnection("conn2");

        QueryConfig.Queryset queryset1 = new QueryConfig.Queryset();
        queryset1.setName("set1");
        queryset1.setQueries(List.of(query1, query2));

        QueryConfig.Query query3 = new QueryConfig.Query();
        query3.setName("q3");
        query3.setQueryString("SELECT * FROM table3");
        query3.setConnection("conn3");

        QueryConfig.Queryset queryset2 = new QueryConfig.Queryset();
        queryset2.setName("set2");
        queryset2.setQueries(List.of(query3));

        config.setQuerysets(List.of(queryset1, queryset2));
    }

    @Test
    void testGetQueriesReturnsCorrectQueries() {
        List<QueryConfig.Query> queries = config.getQueries("set1");
        assertEquals(2, queries.size());
        assertEquals("q1", queries.get(0).getName());
        assertEquals("q2", queries.get(1).getName());

        List<QueryConfig.Query> queries2 = config.getQueries("set2");
        assertEquals(1, queries2.size());
        assertEquals("q3", queries2.get(0).getName());
    }

    @Test
    void testGetQueriesReturnsEmptyListForUnknownQueryset() {
        List<QueryConfig.Query> queries = config.getQueries("unknown");
        assertTrue(queries.isEmpty());
    }

    @Test
    void testQuerysetAndQuerySettersAndGetters() {
        QueryConfig.Queryset qs = new QueryConfig.Queryset();
        qs.setName("testset");
        assertEquals("testset", qs.getName());

        QueryConfig.Query q = new QueryConfig.Query();
        q.setName("testquery");
        q.setQueryString("SELECT 1");
        q.setCollection("books");
        q.setFilter("{\"genre\":\"Fiction\"}");
        q.setConnection("testconn");
        assertEquals("testquery", q.getName());
        assertEquals("SELECT 1", q.getQueryString());
        assertEquals("books", q.getCollection());
        assertEquals("{\"genre\":\"Fiction\"}", q.getFilter());
        assertEquals("testconn", q.getConnection());
    }

    @Test
    void testDefaultQuerysetsIsEmptyList() {
        QueryConfig emptyConfig = new QueryConfig();
        assertNotNull(emptyConfig.getQuerysets());
        assertTrue(emptyConfig.getQuerysets().isEmpty());
    }
}