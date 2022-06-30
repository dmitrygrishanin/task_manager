package com.tasks.app.module;


import com.google.inject.*;
import com.tasks.app.Configuration;
import com.tasks.app.db.TaskDAO;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;

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
    public TaskDAO providesTaskDAO(Jdbi db) {
        return db.onDemand(TaskDAO.class);
    }

}
