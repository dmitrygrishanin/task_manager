package com.tasks.app.db;

import com.google.inject.Inject;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.entity.Task;

import javax.swing.text.html.Option;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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

    public Task updateTask(Task task, String id) {
        taskDAO.updateTask(task, id);
        task.setId(id);
        cacheManager.setTaskToCache(task);
        return task;
    }

    public Task insertTask(Task task) {
        if (task.getId() == null) {
            String id = UUID.randomUUID().toString();
            task.setId(id);
            taskDAO.insertTask(task);
            cacheManager.setTaskToCache(task);
        } else {
            updateTask(task, task.getId());
        }
        return task;
    }

    public boolean deleteTask(String id) {
         if(taskDAO.findTaskById(id).isPresent()) {
             taskDAO.deleteTask(id);
             cacheManager.deleteTaskFromCache(id);
             return true;
         }
         else {
             throw new WebApplicationException("Task not found", 404);
         }
    }
}
