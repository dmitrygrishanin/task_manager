package com.tasks.app.Interceptor;

import com.google.inject.Provider;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.entity.Task;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

public class CacheTask implements MethodInterceptor {
    private final Provider<CacheManager> cacheManager;
    private final Provider<TaskDAO> taskDAO;

    public CacheTask(Provider<CacheManager> provider, Provider<TaskDAO> taskDAOProvider) {
        this.cacheManager = provider;
        this.taskDAO = taskDAOProvider;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result = null;
        if (cacheManager.get().getTasksFromCache().isEmpty()) {
            List<Task> tasks = taskDAO.get().findAllTasks();
            cacheManager.get().setTasksToCache(tasks);
            result = methodInvocation.proceed();
            System.out.println(result);
        }
        return result;
    }
}