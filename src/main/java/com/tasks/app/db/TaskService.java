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

    public List<Task> findAllTasks() {
        return cacheManager.getTasksFromCache();
    }

    public Optional<Task> findTaskById(String id) {
        return cacheManager.getTaskFromCache(id);
    }

    public void updateTask(Task task, String id) {
        taskDAO.updateTask(task, id);
        task.setId(id);
        cacheManager.setTaskToCache(task);
    }

    public void insertTask(Task task) {
        if (task.getId() == null) {
            String id = UUID.randomUUID().toString();
            task.setId(id);
            taskDAO.insertTask(task);
            cacheManager.setTaskToCache(task);
        } else {
            updateTask(task, task.getId());
        }
    }

    public void deleteTask(String id) {
        taskDAO.deleteTask(id);
        cacheManager.deleteTaskFromCache(id);
    }
}
