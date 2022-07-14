package com.tasks.app.resources;

import com.tasks.app.entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

@Testcontainers
public class TaskCacheTest extends BaseTest {

    @BeforeEach
    public void clearCache(){
        cacheManager.clearCache();
    }

    @Test
    @DisplayName("Add task to cache and find it by id")
    public void addTaskToCacheAndFindItById() {
        Task task = getGeneratedTask();
        cacheManager.setTaskToCache(task);
        Optional<Task> received_task = cacheManager.getTaskFromCache(task.getId());
        Assertions.assertTrue(received_task.isPresent(), "Task should be present in cache");
        Assertions.assertEquals(task, received_task.get(), "Received task should be equal to posted task");
    }

    @Test
    @DisplayName("Add multiple tasks to cache and receive them")
    public void addMultipleTasksToCache() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(getGeneratedTask());
        tasks.add(getGeneratedTask());
        cacheManager.setTasksToCache(tasks);
        List<Task> received_tasks = cacheManager.getTasksFromCache();
        Collections.sort(tasks);
        Collections.sort(received_tasks);
        Assertions.assertFalse(received_tasks.isEmpty(), "List of received tasks shouldn't be empty");
        Assertions.assertEquals(tasks, received_tasks, "List of posted tasks should be equal to list of received tasks");
    }

    @Test
    @DisplayName("Add task to cache and delete it")
    public void addTaskToCacheAndDeleteIt() {
        Task task = getGeneratedTask();
        cacheManager.setTaskToCache(task);
        Optional<Task> received_task = cacheManager.getTaskFromCache(task.getId());
        Assertions.assertTrue(received_task.isPresent(), "Task should be present in cache");
        cacheManager.deleteTaskFromCache(task.getId());
        received_task = cacheManager.getTaskFromCache(task.getId());
        Assertions.assertFalse(received_task.isPresent(), "Task should be absent in cache");
    }
}
