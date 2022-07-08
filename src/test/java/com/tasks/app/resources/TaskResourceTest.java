package com.tasks.app.resources;

import com.tasks.app.db.TaskDAO;
import com.tasks.app.db.TaskService;
import com.tasks.app.entity.Task;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TaskResourceTest {
    private static final TaskDAO DAO = mock(TaskDAO.class);
    private static final TaskService taskService = mock(TaskService.class);
    private static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new TaskResource(taskService))
            .build();
    private Task task;

    @BeforeEach
    void setup() {
        task = new Task();
        task.setId("someUUID");
    }

    @AfterEach
    void tearDown() {
        reset(DAO);
    }

    @Test
    void getTaskSuccess() {
        when(DAO.findTaskById("someUUID")).thenReturn(Optional.of(task));
        Task found = EXT.target("/tasks/someUUID").request().get(Task.class);
        assertThat(found.getId()).isEqualTo(task.getId());
        verify(DAO).findTaskById("someUUID");
    }

    @Test
    void getTaskNotFound() {
        when(DAO.findTaskById("invalidUUID")).thenReturn(Optional.empty());
        final Response response = EXT.target("/tasks/invalidUUID").request().get();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        verify(DAO).findTaskById("invalidUUID");
    }
}