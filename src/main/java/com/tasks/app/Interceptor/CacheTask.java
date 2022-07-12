package com.tasks.app.Interceptor;

import com.google.inject.Provider;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.entity.Task;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CacheTask implements MethodInterceptor {

    Provider<CacheManager> cacheManager;
    Provider<TaskDAO> taskDAO;

    public CacheTask(Provider<CacheManager> provider, Provider<TaskDAO> taskDAOProvider) {
        this.cacheManager = provider;
        this.taskDAO = taskDAOProvider;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        switch (methodInvocation.getMethod().getName()) {
            case "findTaskById" -> {
                String taskId = Arrays.stream(methodInvocation.getArguments()).findFirst().get().toString();
                Optional<Task> cachedTask = cacheManager.get().getTaskFromCache(taskId);
                if (cachedTask.isEmpty()) {
                    Optional<Task> task = taskDAO.get().findTaskById(taskId);
                    task.ifPresent(cacheManager.get()::setTaskToCache);
                }
            }
            case "findAllTasks" -> {
                if (cacheManager.get().getTasksFromCache().isEmpty()) {
                    List<Task> tasks = taskDAO.get().getAllTasks();
                    cacheManager.get().setTasksToCache(tasks);
                }
            }
        }
        return methodInvocation.proceed();
    }
}