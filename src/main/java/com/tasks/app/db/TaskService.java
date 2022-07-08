package com.tasks.app.db;

import com.google.inject.Inject;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.entity.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskService {
    private final TaskDAO taskDAO;
    private final CacheManager cacheManager;

    @Inject
    public TaskService(TaskDAO taskDAO, CacheManager cacheManager) {
        this.taskDAO = taskDAO;
        this.cacheManager = cacheManager;
    }

    public List<Task> listOfTasks() {
        Optional<List<Task>> cachedTasks = cacheManager.getAllTasksFromCache();
        if (cachedTasks.isPresent()) {
            return cachedTasks.get();
        } else {
            List<Task> tasks = taskDAO.getAllTasks();
            cacheManager.setAllTasksToCache(tasks);
            return tasks;
        }
    }

    public Optional<Task> findTaskById(String id) {
        Optional<Task> cachedTask = cacheManager.getTaskFromCache(id);
        if (cachedTask.isPresent()) {
            return cachedTask;
        } else {
            Optional<Task> task = taskDAO.findTaskById(id);
            task.ifPresent(value -> cacheManager.setTaskToCache(value.getId(), value));
            return task;
        }
    }

    public Optional<Task> updateTask(Task task, String id) {
        taskDAO.updateTask(task, id);
        cacheManager.clearCache();
        return taskDAO.findTaskById(id);
    }

    public void insertTask(Task task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
            taskDAO.insertTask(task);
            cacheManager.clearCache();
        } else {
            taskDAO.updateTask(task, task.getId());
        }
    }

    public void deleteTask(String id) {
        taskDAO.deleteTask(id);
        cacheManager.clearCache();
    }
}
