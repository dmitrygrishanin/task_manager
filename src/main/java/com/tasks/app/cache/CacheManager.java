package com.tasks.app.cache;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.tasks.app.entity.Task;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.resps.ScanResult;
import java.util.*;
import java.util.stream.Collectors;

public class CacheManager {
    private final Jedis jedis;
    private final Gson gson = new Gson();
    private static final long EXPIRE_TIME_SECONDS = 30;

    @Inject
    public CacheManager(Jedis jedis) {
        this.jedis = jedis;
    }

    public void setTaskToCache(Task task) {
        jedis.set(task.getId(), gson.toJson(task), new SetParams().ex(EXPIRE_TIME_SECONDS));
    }

    public void setTasksToCache(List<Task> tasks){
        tasks.forEach(this::setTaskToCache);
    }

    public Optional<Task> getTaskFromCache(String id) {
        String json = jedis.get(id);
        return Optional.ofNullable(gson.fromJson(json, Task.class));
    }

    public List<Task> getTasksFromCache(){
        ScanParams scanParams = new ScanParams().count(100).match("*");
        ScanResult<String> scanResult = jedis.scan("0", scanParams);
        return scanResult.getResult().stream().map(key -> getTaskFromCache(key).get()).collect(Collectors.toList());
    }

    public void deleteTaskFromCache(String id) {
        jedis.del(id);
    }

    public void clearCache(){
        jedis.flushDB();
    }
}
