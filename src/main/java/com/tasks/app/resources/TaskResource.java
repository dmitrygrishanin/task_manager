package com.tasks.app.resources;

import com.google.inject.Inject;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.entity.Task;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/tasks")
public class TaskResource {

    @Inject
    public TaskDAO taskDAO;

  //  @Inject
   // public TaskResource(TaskDAO taskDAO) {
     //   this.taskDAO = taskDAO;
 //   }

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
        if (task.getId().isEmpty()) {
            task.setId(UUID.randomUUID().toString());
            taskDAO.insertTask(task);
        } else {
            taskDAO.updateTask(task, task.getId());
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<Task> updateTaskById(@PathParam("id") String id, Task task) {
        taskDAO.updateTask(task, id);
        return taskDAO.findTaskById(id);
    }

    @DELETE
    @Path("/{id}")
    public String deleteTaskById(@PathParam("id") String id) {
        taskDAO.deleteTask(id);
        return "Task is removed successfully!";
    }
}
