package com.tasks.app.resources;

import com.tasks.app.entity.Task;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

@Testcontainers
public class TaskDAOTest extends BaseTest {

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
        Task task = getGeneratedTask();
        taskDAO.insertTask(task);
        Optional<Task> receivedTask = taskDAO.findTaskById(task.getId());
        Assertions.assertTrue(receivedTask.isPresent(), "Inserted task is absent");
        Assertions.assertEquals(receivedTask.get().getId(), task.getId(), "Id of received task is differs from expected.");
        Assertions.assertEquals(receivedTask.get().getTask(), task.getTask(), "Id of received task is differs from inserted task");
        Assertions.assertEquals(receivedTask.get().getPriority(), task.getPriority(), "Priority of received task is differs from inserted task");
        Assertions.assertEquals(receivedTask.get().getStatus(), task.getStatus(), "Status of received task is differs from inserted task");
    }

    @Test
    @DisplayName("Update the task and verify updated values")
    public void updateTask() {
        Task task = getGeneratedTask();
        Task newTask = getGeneratedTask();
        taskDAO.insertTask(task);
        taskDAO.updateTask(newTask, task.getId());
        Optional<Task> receivedTask = taskDAO.findTaskById(task.getId());
        Assertions.assertTrue(receivedTask.isPresent(), "Task is not found after update");
        Assertions.assertEquals(receivedTask.get().getId(), task.getId(), "Id of task shouldn't be updated");
        Assertions.assertEquals(receivedTask.get().getTask(), newTask.getTask(), "Task value is not updated");
        Assertions.assertEquals(receivedTask.get().getPriority(), newTask.getPriority(), "Priority of task is not updated");
        Assertions.assertEquals(receivedTask.get().getStatus(), newTask.getStatus(), "Status of task is not updated");
    }

    @Test
    @DisplayName("Delete the task and verify task is absent")
    public void deleteTask() {
        Task task = getGeneratedTask();
        taskDAO.insertTask(task);
        taskDAO.deleteTask(task.getId());
        Optional<Task> receivedTask = taskDAO.findTaskById(task.getId());
        Assertions.assertFalse(receivedTask.isPresent(), "Task should be absent");
    }
}