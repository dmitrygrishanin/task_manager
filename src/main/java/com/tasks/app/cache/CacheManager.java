package com.tasks.app.cache;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.tasks.app.entity.Task;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import java.util.*;
import java.util.stream.Collectors;

public class CacheManager {
    private final Jedis jedis;
    private final Gson gson = new Gson();
    private static final long EXPIRE_TIME_SECONDS = 300;
    private static final String ALL_TASKS_PATTERN = "all_tasks:";
    private static final String ONE_TASK_PATTERN = "one_task:";

    @Inject
    public CacheManager(Jedis jedis) {
        this.jedis = jedis;
    }

    private void setTaskToCache(String key, Task task) {
        jedis.set(key, gson.toJson(task), new SetParams().ex(EXPIRE_TIME_SECONDS));
    }

    public void setOneTaskToCache(Task task) {
        setTaskToCache(ONE_TASK_PATTERN + task.getId(), task);
    }

    public void setAllTasksToCache(List<Task> tasks) {
        clearCache();
        tasks.forEach(task -> setTaskToCache(ALL_TASKS_PATTERN + ONE_TASK_PATTERN + task.getId(), task));
    }

    public Optional<Task> getTaskFromCache(String id) {
        String key = getKeyByPattern(id);
        String json = jedis.get(key);
        return Optional.ofNullable(gson.fromJson(json, Task.class));
    }

    public List<Task> getAllTasksFromCache() {
        Set<String> allTasks = getKeysByPattern(ALL_TASKS_PATTERN);
        if (!allTasks.isEmpty()) {
            Set<String> keys = getKeysByPattern(ONE_TASK_PATTERN);
            return keys.stream().map(key -> getTaskFromCache(key).get()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void deleteTaskFromCache(String id) {
        String key = getKeyByPattern(id);
        jedis.del(key);
    }

    public void updateTask(Task task, String id) {
        task.setId(id);
        String key = getKeyByPattern(id);
        setTaskToCache(key, task);
    }

    private Set<String> getKeysByPattern(String pattern) {
        return jedis.keys("*" + pattern + "*");
    }

    private String getKeyByPattern(String pattern) {
        return getKeysByPattern(pattern).stream().findFirst().get();
    }

    private void clearCache() {
        jedis.flushDB();
    }
}
