package com.tasks.app.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.tasks.app.entity.Task;
import redis.clients.jedis.Jedis;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class CacheManager {
    private final Jedis jedis;
    private final Gson gson = new Gson();
    private static final String ALL_TASKS = "all_tasks";

    @Inject
    public CacheManager(Jedis jedis) {
        this.jedis = jedis;
    }

    public Optional<Task> getTaskFromCache(String id){
        if (isTaskCached(id)) {
            String json = jedis.get(id);
            return Optional.ofNullable(gson.fromJson(json, Task.class));
        }
        return Optional.empty();
    }

    public void setTaskToCache(String key, Task task){
        jedis.set(key, gson.toJson(task));
    }

    public boolean isTaskCached(String id){
        return jedis.exists(id);
    }

    public Optional<List<Task>> getAllTasksFromCache(){
        if (jedis.exists(ALL_TASKS)) {
            String json = jedis.get(ALL_TASKS);
            Type listType = new TypeToken<List<Task>>() {
            }.getType();
            return Optional.ofNullable(gson.fromJson(json, listType));
        }
        return Optional.empty();
    }

    public void setAllTasksToCache(List<Task> tasks){
        jedis.set(ALL_TASKS, gson.toJson(tasks));
    }

    public void clearCache(){
        jedis.flushDB();
    }
}
