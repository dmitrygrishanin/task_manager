package com.tasks.app.resources;

import com.codahale.metrics.MetricRegistry;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.entity.Task;
import com.tasks.app.utils.Utils;
import io.dropwizard.db.ManagedPooledDataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import javax.sql.DataSource;
import java.util.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskDAOTest {
    private static Task task;
    private static TaskDAO taskDAO;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:9.6.12")
            .withInitScript("db.sql");

    @BeforeAll
    public void beforeAll() {
        PoolProperties properties = new PoolProperties();
        properties.setUrl(postgreSQLContainer.getJdbcUrl());
        properties.setUsername(postgreSQLContainer.getUsername());
        properties.setPassword(postgreSQLContainer.getPassword());
        DataSource datasource = new ManagedPooledDataSource(properties, new MetricRegistry());
        Jdbi jdbi = Jdbi.create(datasource);
        jdbi.installPlugin(new SqlObjectPlugin());
        taskDAO = jdbi.onDemand(TaskDAO.class);
    }

    @BeforeEach
    public void beforeEach() {
        task = Utils.getGeneratedTask();
    }

    @Test
    @DisplayName("Verify task entity")
    public void VerifyTask() {
        Task task = new Task();
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
    @DisplayName("Insert a new task and find it by id")
    public void insertTaskAndFindById() {
        taskDAO.insertTask(task);
        Optional<Task> receivedTask = taskDAO.findTaskById(task.getId());
        Assertions.assertTrue(receivedTask.isPresent(), "Inserted task is absent");
        Assertions.assertEquals(receivedTask.get().getId(), task.getId(), "Id of received task is differs from " +
                "expected.");
        Assertions.assertEquals(receivedTask.get().getTask(), task.getTask(), "Id of received task is differs from " +
                "inserted task");
        Assertions.assertEquals(receivedTask.get().getPriority(), task.getPriority(), "Priority of received task is " +
                "differs from inserted task");
        Assertions.assertEquals(receivedTask.get().getStatus(), task.getStatus(), "Status of received task is differs" +
                " from inserted task");
    }

    @Test
    @DisplayName("Update the task and verify updated values")
    public void updateTask() {
        Task newTask = Utils.getGeneratedTask();
        taskDAO.insertTask(task);
        taskDAO.updateTask(newTask, task.getId());
        Optional<Task> receivedTask = taskDAO.findTaskById(task.getId());
        Assertions.assertTrue(receivedTask.isPresent(), "Task is not found after update");
        Assertions.assertEquals(receivedTask.get().getId(), task.getId(), "Id of task shouldn't be updated");
        Assertions.assertEquals(receivedTask.get().getTask(), newTask.getTask(), "Task value is not updated");
        Assertions.assertEquals(receivedTask.get().getPriority(), newTask.getPriority(), "Priority of task is not " +
                "updated");
        Assertions.assertEquals(receivedTask.get().getStatus(), newTask.getStatus(), "Status of task is not updated");
    }

    @Test
    @DisplayName("Delete the task and verify task is absent")
    public void deleteTask() {
        taskDAO.insertTask(task);
        taskDAO.deleteTask(task.getId());
        Optional<Task> receivedTask = taskDAO.findTaskById(task.getId());
        Assertions.assertFalse(receivedTask.isPresent(), "Task should be absent");
    }
}