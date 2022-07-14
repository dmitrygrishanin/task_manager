package com.tasks.app.resources;

import com.codahale.metrics.MetricRegistry;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.db.TaskService;
import com.tasks.app.entity.Task;
import io.dropwizard.db.ManagedPooledDataSource;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import redis.clients.jedis.Jedis;
import javax.sql.DataSource;
import java.util.Random;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {
    TaskService taskService;
    Jedis jedis;
    CacheManager cacheManager;
    ResourceExtension resourceExtension;
    DataSource datasource;
    Jdbi jdbi;
    TaskDAO taskDAO;
    Random random = new Random();
    String[] status = {"new", "in progress", "closed"};

    @Container
    public static GenericContainer redis = new GenericContainer("redis:3.0.6")
            .withExposedPorts(6379);

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:9.6.12")
            .withInitScript("db.sql");

    @BeforeAll
    public void setUp() {
        PoolProperties properties = new PoolProperties();
        properties.setUrl(postgreSQLContainer.getJdbcUrl());
        properties.setUsername(postgreSQLContainer.getUsername());
        properties.setPassword(postgreSQLContainer.getPassword());
        datasource = new ManagedPooledDataSource(properties, new MetricRegistry());
        jdbi = Jdbi.create(datasource);
        jdbi.installPlugin(new SqlObjectPlugin());
        taskDAO = jdbi.onDemand(TaskDAO.class);
        jedis = new Jedis(redis.getHost(), redis.getMappedPort(6379));
        cacheManager = new CacheManager(jedis);
        taskService = new TaskService(taskDAO, cacheManager);
        resourceExtension = ResourceExtension.builder()
                .addResource(new TaskResource(taskService))
                .build();
    }

    public Task getGeneratedTask() {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTask(RandomStringUtils.randomAlphanumeric(17));
        task.setPriority(random.nextInt(5 - 1) + 1);
        task.setStatus(status[random.nextInt(status.length)]);
        return task;
    }
}
