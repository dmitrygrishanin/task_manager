package com.tasks.app.resources;

import com.google.inject.Inject;
import com.tasks.app.db.TaskService;
import com.tasks.app.entity.Task;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/tasks")
public class TaskResource {
    private final TaskService taskService;

    @Inject
    public TaskResource(TaskService taskService){
        this.taskService = taskService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> listOfTasks() {
        return taskService.listOfTasks();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task findTaskById(@PathParam("id") String id) {
        return taskService.findTaskById(id).orElseThrow(() -> new WebApplicationException("Task not found", 404));
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
        taskService.deleteTask(id);
    }
}
