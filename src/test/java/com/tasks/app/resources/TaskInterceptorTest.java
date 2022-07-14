package com.tasks.app.resources;

import com.google.inject.util.Providers;
import com.tasks.app.Interceptor.CacheTask;
import com.tasks.app.entity.Task;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TaskInterceptorTest extends BaseTest {
    CacheTask cacheTask;

    @BeforeAll
    @Override
    public void setUp() {
        super.setUp();
        cacheTask = new CacheTask(Providers.of(cacheManager), Providers.of(taskDAO));
    }

    @BeforeEach
    public void beforeEachTest(){
        cacheManager.clearCache();
    }

    @Test
    @DisplayName("Verify task is added to cache after call interceptor")
    public void TaskIsAddedToCache(){
        Task task = getGeneratedTask();
        taskDAO.insertTask(task);
        cacheTask.interceptor();
        Assertions.assertTrue(cacheManager.getTaskFromCache(task.getId()).isPresent(), "Task isn't cached");
    }

    @Test()
    @DisplayName("Verify cache is updated before expiring the time")
    public void CachedNotUpdatedImmediately(){
        Task task1 = getGeneratedTask();
        Task task2 = getGeneratedTask();
        taskDAO.insertTask(task1);
        cacheTask.interceptor();
        taskDAO.insertTask(task2);
        cacheTask.interceptor();
        Assertions.assertFalse(cacheManager.getTaskFromCache(task2.getId()).isPresent(), "Second task shouldn't be cached");
    }
}
