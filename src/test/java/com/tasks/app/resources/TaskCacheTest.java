package com.tasks.app.resources;

import com.tasks.app.cache.CacheManager;
import com.tasks.app.entity.Task;
import com.tasks.app.utils.Utils;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;
import java.util.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskCacheTest {
    private static CacheManager cacheManager;
    private static Task task;

    @Container
    public static GenericContainer redis = new GenericContainer("redis:3.0.6")
            .withExposedPorts(6379);

    @BeforeAll
    public void beforeAll() {
        Jedis jedis = new Jedis(redis.getHost(), redis.getMappedPort(6379));
        cacheManager = new CacheManager(jedis);
    }

    @BeforeEach
    public void clearCache(){
        cacheManager.clearCache();
        task = Utils.getGeneratedTask();
    }

    @Test
    @DisplayName("Add task to cache and find it by id")
    public void addTaskToCacheAndFindItById() {
        cacheManager.setTaskToCache(task);
        Optional<Task> received_task = cacheManager.getTaskFromCache(task.getId());
        Assertions.assertTrue(received_task.isPresent(), "Task should be present in cache");
        Assertions.assertEquals(task, received_task.get(), "Received task should be equal to posted task");
    }

    @Test
    @DisplayName("Add multiple tasks to cache and receive them")
    public void addMultipleTasksToCache() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(Utils.getGeneratedTask());
        tasks.add(Utils.getGeneratedTask());
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
        cacheManager.setTaskToCache(task);
        Optional<Task> received_task = cacheManager.getTaskFromCache(task.getId());
        Assertions.assertTrue(received_task.isPresent(), "Task should be present in cache");
        cacheManager.deleteTaskFromCache(task.getId());
        received_task = cacheManager.getTaskFromCache(task.getId());
        Assertions.assertFalse(received_task.isPresent(), "Task should be absent in cache");
    }
}
