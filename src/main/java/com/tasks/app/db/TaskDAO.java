package com.tasks.app.db;

import com.tasks.app.entity.Task;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterRowMapper(TaskDAOMapper.class)
public interface TaskDAO {
    @SqlUpdate("delete from task_review where id = :id")
    void deleteTask(@Bind("id") String id);

    @SqlUpdate("update task_review set task = :task, status = :status, priority = :priority where id = :id")
    void updateTask(@BindBean Task task, @Bind("id") String id);

    @SqlUpdate("insert into task_review (id, task, status, priority) values (:id, :task, :status, :priority)")
    void insertTask(@BindBean Task task);

    @SqlQuery("select * from task_review where id = :id")
    Optional<Task> findTaskById(@Bind("id") String id);

    @SqlQuery("select * from task_review")
    List<Task> getAllTasks();
}
