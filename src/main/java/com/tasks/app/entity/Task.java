package com.tasks.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task implements Comparable<Task>{
    private String id;
    private String task;
    private String status;
    private Integer priority;

    @Override
    public int compareTo(Task o) {
        return this.id.compareTo(o.id);
    }
}
