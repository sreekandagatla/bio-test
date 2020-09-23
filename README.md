# Developer Setup

Face compare API requires:

- JDK 11
- Maven


To start, fetch this code:

`git clone https://github.com/sreekandagatla/bio-test.git`

1. Run maven to install dependencies and package
   - `mvn clean  compile install`   
2. Run spring boot application.
   - `mvn spring-boot:run`
3. Verify deployment by using [swagger end point](http://localhost:8080/swagger-ui.html)
4. Execute Biometric Controller in swagger for face compare in s3.
5. Execute Algorithm Controller in swagger for algorithm details.
