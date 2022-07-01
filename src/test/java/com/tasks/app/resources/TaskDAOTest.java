package com.tasks.app.resources;

import com.codahale.metrics.MetricRegistry;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.entity.Task;
import io.dropwizard.db.ManagedPooledDataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import javax.sql.DataSource;
import java.util.*;

@Testcontainers
public class TaskDAOTest {
    static DataSource datasource;
    static Jdbi jdbi;
    private Task task;
    private static TaskDAO dao;
    Random random = new Random();
    String[] status = {"new", "in progress", "closed"};

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:9.6.12")
            .withInitScript("db.sql");

    @BeforeAll
    static void init() {
        PoolProperties properties = new PoolProperties();
        properties.setUrl(postgreSQLContainer.getJdbcUrl());
        properties.setUsername(postgreSQLContainer.getUsername());
        properties.setPassword(postgreSQLContainer.getPassword());
        datasource = new ManagedPooledDataSource(properties, new MetricRegistry());
        jdbi = Jdbi.create(datasource);
        jdbi.installPlugin(new SqlObjectPlugin());
        dao = jdbi.onDemand(TaskDAO.class);
    }

    @Test
    public void VerifyTask() {
        task = new Task();
        task.setId("3");
        task.setTask("Some task description");
        task.setPriority(5);
        task.setStatus("new");
        Assertions.assertEquals(task.getId(), "3");
        Assertions.assertEquals(task.getTask(), "Some task description");
        Assertions.assertEquals(task.getPriority(), 5);
        Assertions.assertEquals(task.getStatus(), "new");
    }

    @Test
    public void insertTaskAndFindById() {
        task = getGeneratedTask();
        dao.insertTask(task);
        Optional<Task> receivedTask = dao.findTaskById(task.getId());
        Assertions.assertTrue(receivedTask.isPresent());
        Assertions.assertEquals(receivedTask.get().getId(), task.getId());
        Assertions.assertEquals(receivedTask.get().getTask(), task.getTask());
        Assertions.assertEquals(receivedTask.get().getPriority(), task.getPriority());
        Assertions.assertEquals(receivedTask.get().getStatus(), task.getStatus());
    }

    @Test
    public void updateTask() {
        Task task = getGeneratedTask();
        Task newTask = getGeneratedTask();
        dao.insertTask(task);
        dao.updateTask(newTask, task.getId());
        Optional<Task> receivedTask = dao.findTaskById(task.getId());
        Assertions.assertTrue(receivedTask.isPresent());
        Assertions.assertEquals(receivedTask.get().getId(), task.getId());
        Assertions.assertEquals(receivedTask.get().getTask(), newTask.getTask());
        Assertions.assertEquals(receivedTask.get().getPriority(), newTask.getPriority());
        Assertions.assertEquals(receivedTask.get().getStatus(), newTask.getStatus());
    }

    @Test
    public void deleteTask() {
        task = getGeneratedTask();
        dao.insertTask(task);
        dao.deleteTask(task.getId());
        Optional<Task> receivedTask = dao.findTaskById(task.getId());
        Assertions.assertFalse(receivedTask.isPresent());
    }

    private Task getGeneratedTask() {
        task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTask(RandomStringUtils.randomAlphanumeric(17));
        task.setPriority(random.nextInt(5 - 1) + 1);
        task.setStatus(status[random.nextInt(status.length)]);
        return task;
    }
}