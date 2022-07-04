package com.tasks.app.resources;

import com.google.inject.Inject;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.db.TaskService;
import com.tasks.app.entity.Task;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/tasks")
public class TaskResource {

    private final TaskDAO taskDAO;
    private final TaskService taskService;

    @Inject
    public TaskResource(TaskDAO taskDAO){
        this.taskDAO = taskDAO;
        taskService = new TaskService(taskDAO);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> listOfTasks() {
        return taskDAO.getAllTasks();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<Task> getTaskById(@PathParam("id") String id) {
        return taskDAO.findTaskById(id);
    }

    @POST
    public void insertTask(Task task) {
       taskService.insertTask(task);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<Task> updateTaskById(@PathParam("id") String id, Task task) {
        return taskService.updateTask(task,id);
    }

    @DELETE
    @Path("/{id}")
    public void deleteTaskById(@PathParam("id") String id) {
        taskDAO.deleteTask(id);
    }
}
