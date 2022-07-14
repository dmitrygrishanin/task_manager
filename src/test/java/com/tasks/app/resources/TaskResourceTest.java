package com.tasks.app.resources;

import com.tasks.app.entity.Task;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Testcontainers
@ExtendWith(DropwizardExtensionsSupport.class)
public class TaskResourceTest extends BaseTest {

    @Test
    @DisplayName("Verify task is found by id")
    void getTaskSuccess() {
        Task task = getGeneratedTask();
        cacheManager.setTaskToCache(task);
        Task received_task = resourceExtension.target("/tasks/"+task.getId()+"").request().get(Task.class);
        Assertions.assertEquals(task.getId(), received_task.getId(), "Wrong task id.");
    }

    @Test
    @DisplayName("Verify 'Not found' response if task is absent")
    void getTaskNotFound() {
        Response response = resourceExtension.target("/tasks/invalidUUID").request().get();
        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatusInfo().getStatusCode(), "Wrong response status code.");
    }

    @Test
    @DisplayName("Get all tasks and verify them")
    void getAllTasks() {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(getGeneratedTask());
        tasks.add(getGeneratedTask());
        cacheManager.setTasksToCache(tasks);
        List<Task> received_tasks = resourceExtension.target("/tasks").request().get(new GenericType<>(){});
        Assertions.assertEquals(tasks, received_tasks, "Posted tasks and received tasks are differ");
    }

    @Test
    @DisplayName("Post a new task and verify it")
    public void postNewTask(){
        Task task = getGeneratedTask();
        task.setId(null);
        resourceExtension.target("/tasks").request().post(Entity.entity(task, MediaType.APPLICATION_JSON));
        Optional<Task> received_task = taskDAO.findAllTasks().stream().findFirst();
        Assertions.assertTrue(received_task.isPresent(), "Task is absent");
        Assertions.assertEquals(received_task.get().getTask(), task.getTask(), "Task value of received task is differ from posted value");
        Assertions.assertEquals(received_task.get().getStatus(), task.getStatus(), "Status value of received task is differ from posted value");
        Assertions.assertEquals(received_task.get().getPriority(), task.getPriority(), "Priority value of received task is differ from posted value");
    }

    @Test
    @DisplayName("Update task and verify it")
    public void updateTask(){
        Task task = getGeneratedTask();
        Task newTask = getGeneratedTask();
        taskDAO.insertTask(task);
        resourceExtension.target("/tasks/"+task.getId()+"").request().put(Entity.entity(newTask, MediaType.APPLICATION_JSON));
        Optional<Task> received_task = taskDAO.findTaskById(task.getId());
        Assertions.assertTrue(received_task.isPresent(), "Updated task is absent");
        Assertions.assertEquals(task.getId(), received_task.get().getId(), "Id of updated task shouldn't be changed");
        Assertions.assertEquals(newTask.getTask(), received_task.get().getTask(), "Task value is not updated");
        Assertions.assertEquals(newTask.getStatus(), received_task.get().getStatus(), "Status value is not updated");
        Assertions.assertEquals(newTask.getPriority(), received_task.get().getPriority(),  "Priority value is not updated");
    }

    @Test
    @DisplayName("Delete task and verify it absent")
    public void deleteTask(){
        Task task = getGeneratedTask();
        taskDAO.insertTask(task);
        resourceExtension.target("/tasks/"+task.getId()+"").request().delete();
        Optional<Task> received_task = taskDAO.findTaskById(task.getId());
        Assertions.assertFalse(received_task.isPresent(), "Updated task is absent");
    }
}