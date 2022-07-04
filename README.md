# Task manager
Before start make sure:
 - docker application is running 
 - postgreSQL server is running

#Building the application
Execute the following command to build and package the application in a jar file:
- mvn clean package

#Running the application
Once the jar file is built, execute the jar file with the following arguments: 

- java -jar target/tasksmanager-1.0-SNAPSHOT.jar server local_properties.yml

To receive the list of tasks perform call:

    GET  http://localhost:8080/tasks/

Receive a task by id:
    
    GET http://localhost:8080/tasks/cd365293-270a-4ce8-9f91-bca08a099f17elt

Insert a new task:

    POST http://localhost:8080/tasks/
    body {
        "task": "Some task 1",
        "status": "new",
        "priority": 2
    }

Update existing task:

    PUT http://localhost:8080/tasks/71670063-07ee-44a6-9baf-a60c296387ea
    body{
        "task": "Some task 2",
        "status": "closed",
        "priority": 3
    }

Delete task:

    DELETE http://localhost:8080/tasks/a94f82e9-fe98-43d8-99f1-09cd66cb98e3






