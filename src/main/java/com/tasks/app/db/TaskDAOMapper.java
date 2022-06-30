package com.tasks.app.db;

import com.tasks.app.entity.Task;
import org.jdbi.v3.core.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskDAOMapper implements RowMapper<Task> {

    @Override
    public Task map(ResultSet resultSet, org.jdbi.v3.core.statement.StatementContext ctx) throws SQLException {
        return new Task(resultSet.getString("id"), resultSet.getString("task"),resultSet.getString("status"),resultSet.getInt("priority"));
    }
}
