package com.tasks.app.db;
import com.google.inject.Inject;
import com.tasks.app.entity.Task;
import java.util.Optional;
import java.util.UUID;


public class TaskService{
    private final TaskDAO taskDAO;

    public TaskService(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public Optional<Task> updateTask(Task task, String id) {
        taskDAO.updateTask(task, id);
        return taskDAO.findTaskById(id);
    }

    public void insertTask(Task task) {
        if (task.getId().isEmpty()) {
            task.setId(UUID.randomUUID().toString());
            taskDAO.insertTask(task);
        } else {
            taskDAO.updateTask(task, task.getId());
        }
    }
}
