package com.tasks.app.resources;

import com.google.inject.util.Providers;
import com.tasks.app.Interceptor.CacheTask;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.entity.Task;
import com.tasks.app.utils.Utils;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TaskInterceptorTest {
    private static final MethodInvocation methodInvocation = mock(MethodInvocation.class);
    private static final CacheManager cacheManager = mock(CacheManager.class);
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final CacheTask cacheTask = new CacheTask(Providers.of(cacheManager), Providers.of(taskDAO));

    @Test
    @DisplayName("Verify interceptor cache is empty")
    public void interceptorCacheIsEmpty() throws Throwable {
        when(Providers.of(cacheManager).get().getTasksFromCache()).thenReturn(new ArrayList<>());
        cacheTask.invoke(methodInvocation);
        verify(methodInvocation).proceed();
    }

    @Test
    @DisplayName("Verify interceptor cache is not empty")
    public void interceptorCacheIsNotEmpty() throws Throwable {
        List<Task> listOfTasks = new ArrayList<>();
        listOfTasks.add(Utils.getGeneratedTask());
        when(Providers.of(cacheManager).get().getTasksFromCache()).thenReturn(listOfTasks);
        cacheTask.invoke(methodInvocation);
        verify(methodInvocation, times(0)).proceed();
    }
}


