# KeepItSimple

Documentation of the APIs: https://documenter.getpostman.com/view/8415140/2s9YJaX3X5

Instructions to setup local database: 

First, we need to create a database named ```keepitsimple_db```

After that, the only required instruction in order to run our app is to run the following query. This will create the required roles for our users.

``` db.roles.insertMany([ { name: "ROLE_USER" }, { name: "ROLE_MODERATOR" }, { name: "ROLE_ADMIN" }] ) ```


Explanations on the design:

- I would have liked to use a microservice architecture, however, given the limited time I had, I prioritized other features such as the search, filters, and the user creation.

- I decided to use MongoDB as a database. I took this decision thinking on the performance and escalation of this system. If we're planning to have millions and millions of tasks, a non sql database is key.

- I would have liked to have the user administration in SQL, and also in a different microservice. Therefore, even though the communication can be more difficult to implement (either with a gateway or direct communication through a grpc connection), it would definitely be more scalable. We would be able to have replicas of each microservice (both users and tasks, even gateway if needed), and also load balancers to access these microservices.

- I apologize if the code is not completely clean. It's Friday, 4:30am and I have to work in a couple hours. I'd have done more if I had more time.


Please feel free to drop any comment, question or suggestions.

