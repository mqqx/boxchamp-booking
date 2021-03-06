![Java CI with Maven](https://github.com/mqqx/boxchamp-booking/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
# boxchamp-booking

Spring Boot command line application to book [boxchamp](https://boxchamp.io/) classes in advance via REST.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project.

### Prerequisites

Java 8 and Maven are required to build the application.

### Installing

1. Clone or download the repository
2. Maven `mvn install` the project `pom.xml`
3. Run the *.jar with the username, password and preferred course arguments like the following example:

`java -Xmx64m -Xss256k -jar target/boxchamp-booking-0.0.1-SNAPSHOT.jar --username=myboxchamp@email.com --password=mypassword --course=endurance`

`username` = your boxchamp username which is basically your e-mail address  
`password` = your users password  
`course` = the type of class you want to book in advance (currently `Endurance`, `Gymnastics` and `Olympic_Lifting` are supported)
