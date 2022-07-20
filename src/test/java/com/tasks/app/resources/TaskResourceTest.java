package com.tasks.app.resources;

import com.tasks.app.db.TaskService;
import com.tasks.app.entity.Task;
import com.tasks.app.utils.Utils;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TaskResourceTest {
    private static final TaskService taskService = mock(TaskService.class);
    public static final ResourceExtension resourceExtension = ResourceExtension.builder()
            .addResource(new TaskResource(taskService))
            .build();
    private static Task task;

    @BeforeEach
    public void beforeEach() {
        task = Utils.getGeneratedTask();
    }

    @Test
    @DisplayName("Verify task is found by id")
    void getTaskSuccess() {
        when(taskService.findTaskById(anyString())).thenReturn(Optional.of(task));
        Task found = resourceExtension.target("/tasks/" + task.getId() + "").request().get(Task.class);
        Assertions.assertEquals(found.getId(), task.getId());
        verify(taskService).findTaskById(task.getId());
    }

    @Test
    @DisplayName("Verify task is found by id")
    void getTaskNotFound() {
        when(taskService.findTaskById(anyString())).thenReturn(Optional.empty());
        final Response response = resourceExtension.target("/tasks/" + task.getId() + "").request().get();
        Assertions.assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.NOT_FOUND.getStatusCode());
        verify(taskService).findTaskById(task.getId());
    }

    @Test
    @DisplayName("")
    void getAllTaskSuccess() {
        List<Task> listOfTasks = new ArrayList<>();
        listOfTasks.add(Utils.getGeneratedTask());
        listOfTasks.add(Utils.getGeneratedTask());
        when(taskService.findAllTasks()).thenReturn(listOfTasks);
        List<Task> received_tasks = resourceExtension.target("/tasks/").request().get(new GenericType<>() {
        });
        Assertions.assertEquals(listOfTasks, received_tasks, "Posted tasks and received tasks are differ");
        verify(taskService).findAllTasks();
    }

    @DisplayName("Post a new task and verify it")
    @Test
    public void postNewTask() {
        task = Utils.getGeneratedTask();
        when(taskService.insertTask(any(Task.class))).thenReturn(task);
        Response response =
                resourceExtension.target("/tasks").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(task,
                        MediaType.APPLICATION_JSON_TYPE));
        verify(taskService).insertTask(task);
        Assertions.assertEquals(response.getStatusInfo(), Response.Status.OK);
        Assertions.assertEquals(response.readEntity(Task.class), task);
    }

    @DisplayName("Update task and verify it")
    @Test
    public void updateTask(){
        Task newTask = Utils.getGeneratedTask();
        newTask.setId(task.getId());
        when(taskService.updateTask(any(Task.class), anyString())).thenReturn(newTask);
        Response response = resourceExtension.target("/tasks/"+task.getId()+"").request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(newTask, MediaType.APPLICATION_JSON_TYPE));
        verify(taskService).updateTask(newTask, task.getId());
        Assertions.assertEquals(response.getStatusInfo(), Response.Status.OK);
        Assertions.assertEquals(response.readEntity(Task.class), newTask);
    }

    @Test
    @DisplayName("Delete task and verify it absent")
    public void deleteTask(){
        when(taskService.deleteTask(anyString())).thenReturn(true);
        Response response = resourceExtension.target("/tasks/"+task.getId()+"").request().delete();
        verify(taskService).deleteTask(task.getId());
        Assertions.assertEquals(response.getStatusInfo(), Response.Status.OK);
        Assertions.assertEquals(response.readEntity(Boolean.class), true);
    }
}