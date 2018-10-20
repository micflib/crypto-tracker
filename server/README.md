# Crypto Tracker Backend
This sub-project is the backend server portion of the project.

**Make sure you have Maven and Java 1.7 or greater**

```bash
# change directory to server
cd angular-spring-starter/server

# install the repo with mvn
mvn install

# start the server
mvn spring-boot:run

# the app will be running on port 8080

## Configuration
- **WebSecurityConfig.java**: The server-side authentication configurations.
- **application.yml**: Application level properties i.e the token expire time, token secret etc. You can find a reference of all application properties [here](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).
