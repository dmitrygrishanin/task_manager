package com.tasks.app.resources;

import com.google.inject.util.Providers;
import com.tasks.app.Interceptor.CacheTask;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.db.TaskDAO;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;

public class TaskInterceptorTest {
    private static final MethodInvocation methodInvocation = mock(MethodInvocation.class);
    private static final CacheManager cacheManager = mock(CacheManager.class);
    private static final TaskDAO taskDAO = mock(TaskDAO.class);
    private static final CacheTask cacheTask = new CacheTask(Providers.of(cacheManager), Providers.of(taskDAO));

    @Test
    @DisplayName("Verify interceptor invocation")
    public void interceptorInvocation() throws Throwable {
        when(methodInvocation.proceed()).thenReturn(null);
        cacheTask.invoke(methodInvocation);
        verify(methodInvocation).proceed();
    }
}
