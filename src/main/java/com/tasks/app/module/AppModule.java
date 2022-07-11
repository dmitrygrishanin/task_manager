package com.tasks.app.module;

import com.google.inject.*;
import com.tasks.app.Configuration;
import com.tasks.app.cache.CacheManager;
import com.tasks.app.db.TaskDAO;
import com.tasks.app.db.TaskService;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class AppModule extends AbstractModule  {

    @Override
    protected void configure() {}

    @Provides
    @Singleton
    public Jdbi prepareJdbi(Environment environment,
                            Configuration configuration) {
        JdbiFactory factory = new JdbiFactory();
        return factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
    }

    @Provides
    @Singleton
    public Jedis prepareJedis() {
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
        return jedisPool.getResource();
    }

    @Provides
    @Singleton
    public CacheManager prepareCacheManager(Jedis jedis) {
        return new CacheManager(jedis);
    }

    @Provides
    @Singleton
    public TaskDAO providesTaskDAO(Jdbi db) {
        return db.onDemand(TaskDAO.class);
    }

    @Provides
    @Singleton
    public TaskService providesTaskService(TaskDAO taskDAO, CacheManager cacheManager) {
        return new TaskService(taskDAO, cacheManager);
    }
    }