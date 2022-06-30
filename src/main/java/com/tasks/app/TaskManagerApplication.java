package com.tasks.app;

import com.tasks.app.module.AppModule;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class TaskManagerApplication extends Application<Configuration> {
    public static void main(final String[] args) throws Exception {
        new TaskManagerApplication().run(args);
    }

    @Override
    public String getName() {
        return "taskmanager";
    }

    @Override
    public void initialize(final Bootstrap<Configuration> bootstrap) {
      /*  bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        ); */
        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(new AppModule())
                .build());
    }

    @Override
    public void run(final Configuration configuration,
                    final Environment environment) {
    }
}
