-- Cassandra sample data for Query microservice
-- Creates metrics keyspace/table and inserts sample CPU usage rows

CREATE KEYSPACE IF NOT EXISTS metrics
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

CREATE TABLE IF NOT EXISTS metrics.cpu (
    hostname text,
    recorded_at timestamp,
    cpu_usage double,
    PRIMARY KEY (hostname, recorded_at)
) WITH CLUSTERING ORDER BY (recorded_at DESC);

INSERT INTO metrics.cpu (hostname, recorded_at, cpu_usage) VALUES ('app-server-1', toTimestamp(now()), 32.5);
INSERT INTO metrics.cpu (hostname, recorded_at, cpu_usage) VALUES ('app-server-2', toTimestamp(now()), 54.1);
INSERT INTO metrics.cpu (hostname, recorded_at, cpu_usage) VALUES ('cache-node-1', toTimestamp(now()), 21.3);
INSERT INTO metrics.cpu (hostname, recorded_at, cpu_usage) VALUES ('cache-node-2', toTimestamp(now()), 27.8);
INSERT INTO metrics.cpu (hostname, recorded_at, cpu_usage) VALUES ('worker-01', toTimestamp(now()), 63.0);
INSERT INTO metrics.cpu (hostname, recorded_at, cpu_usage) VALUES ('worker-02', toTimestamp(now()), 41.7);
