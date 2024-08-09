# Talkdesk Java Service Reference Implementation

This project purpose is to act as a basis to any Java service being developed in Talkdesk. It does follow an opinionated 
approach on how to do things, using [Quarkus](http://quarkus.io) as the framework of choice to build the service.

Some functionalities, common to most services, are implemented in the 
[Athena](https://github.com/talkdesk-tdx/tdx-athena) libraries.
Some examples are account validation, permissions, pagination, log format, bugsnag notifications
and generic error handling.

## Pre Requisites

* [OpenJDK 17](https://adoptium.net/temurin/releases/?version=17)
* [Maven 3.8.5](https://maven.apache.org/download.cgi)

## Build

Just use Maven to build the project with the following command:

```bash
mvn package
``` 

## Project Contents

Right now the project covers the following scenarios:
* Create / Update a database schema.
* Interact with a database. Create, Read, Update, Delete and Retrieve data.
* Expose a REST API to perform data operations.

## Project Structure

The project is split into several packages:
* entity (contains JPA / Hibernate entity model).
* mapper (the required MapStruct code to map objects).
* model (the api model representation).
* repository (the JPA / Hibernate bean to perform entity operations).
* resource (the JAX-RS / REST endpoints).
* validations (Bean validation definitions and implementations)

Each class is documented with the technical decisions and rationales that should be used when developing these services.

### Libraries

#### Lombok
[Lombok](https://projectlombok.org) is a code generator that allows you to annotate pojos and automatically generate 
constructors, getters, setters, equals, toString and many more. 

It is used to speed up development and to keep classes clean and remove error prone actions when boiler plate code 
needs to be regenerated. 

##### Additional Setup / IDE Plugin

To enable full support of Lombok in an IDE, we need to install its correspondent [Lombok Plugin](https://projectlombok.org/setup/overview).

[Here](https://www.baeldung.com/lombok-ide) is an additional link to a tutorial explaining how to install and configure
Lombok Plugin on IntelliJ and Eclipse.

#### MapStruct
[MapStruct](https://mapstruct.org) is a code generator that greatly simplifies the implementation of mappings between 
Java bean types based on a convention over configuration approach.

It is used to map objects between the data layer and the API layer. 

#### Quarkus
[Quarkus](https://quarkus.io) is a Kubernetes Native Java stack tailored for GraalVM & OpenJDK HotSpot, crafted from 
the best of breed Java libraries and standards, including MicroProfile, Hibernate, RESTEasy and others.

It is used as the framework to lay down the required services. 

### Guides

#### Pesistence with Hibernate and Panache
TODO

#### Database Migrations with Flyway
TODO

#### Map objects with MapStruct
TODO

#### REST APIs with MicroProfile
TODO

#### REST API's Documentation
TODO

Please refer also to the [Publish Swagger files in Talkdesk API Gateway](https://talkdesk.atlassian.net/wiki/spaces/TDXE/pages/1114538209/Publish+Swagger+files+in+Talkdesk+API+Gateway) 
documentation.  

#### Request authorization, permission from TDX-Athena

#### Installation
```xml script
<dependency>
    <groupId>com.td.athena</groupId>
    <artifactId>td-athena-security</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### Policy Usage

In microservices, we often need to check policies to make sure one can only perform actions he's authorized. 
Talkdesk also requires policies to be enforced both in frontend and in backend. 
To check what a user can do or not, SRE provides us a remote endpoint.

This Athena security extension has a set of classes and features to help teams enforce policies without having to deal with boiler plate.

First the configuration. There isn't much do do aside from setting up the remote serveur to reach out to.

```
com.td.athena.security.policy.PermissionsClient/mp-rest/url=http://localhost:9000
``` 

Then, you are ready to use the available annotations to secure your microservices.
Let's take for example the following 2 simple resource

```java
@Path("/colors")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@ApplicationScoped
@Secured
@RequiresPolicies(value = {"user"}, allowSystemBypass = false)
public static class ColorResource {

    @GET
    @Path("/yellow")
    @RequiresPolicy("yellow")
    public String yellow() {
        return "ok";
    }

    @GET
    @Path("/yellow-or-red")
    @RequiresPolicy("yellow")
    @RequiresPolicy("red")
    public String yellowAndOrRed() {
        return "ok";
    }

    @GET
    @Path("/orange")
    public String classOrange() {
        return "ok";
    }
}

@Path("/guest")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@ApplicationScoped
@Secured
public static class GuestResource {

    @GET
    @RequiresPolicies(value = {@RequiresPolicy("permissionA"),
                           @RequiresPolicy("permissionB")},
                  evaluate = RequiresPolicies.Evaluate.ALL)
    public String authenticated() {
        return "ok";
    }

    public String guest() {
         return "ok";
    }
}
```

`@Secured` must be used in all resources or CDI beans you need to secure.
It triggers the policy interceptor. 
Without this annotation, the interceptor won't get called and nothing will be enforced.

Then, you can specify one or more `@RequiresPolicy` on either the class or individual methods.
In order to stay consistent with other annotations and behavior of the Java platform, if the interceptor finds annotations
in both the class and the method, the method will always be used. 
The interceptor will never use both at the same time (union).
If nothing is found in methods, then the interceptor will look at the class level.

`@RequiresPolicy` is a `@Repeatable` annotation and therefor, you can add as many times this annotation as you want.
Again, for consistency reasons, as soon as one policy is valid, the interceptor will let the request go.

In the `GuestResource`, there is an `authenticated` method. 
In there, as you can see, we decided to override the default behavior and require both `permissionA` and `permissionB` to be enforced.
This is done by using `RequiresPolicies.Evaluate.ALL` enum in the `@RequiresPolicies`.

The `@RequiresPolicies` has an option to bypass user policies, named `allowSystemBypass`, to be used when requests are
initiated internally and the JWT of the request chain is an account level token without any user information.

Finally, if a class level annotation is used, it will apply to all methods in the class.


#### rest client authentication from TDX-Athena
TODO

#### Service health checks from TDX-Athena
TODO

#### Error handling from TDX-Athena
TODO 

#### Pagination from TDX-Athena

##### Installation

pom.xml
```xml
<dependency>
    <groupId>com.td.athena</groupId>
    <artifactId>td-athena-pagination</artifactId>
    <version>${td-athena.version}</version>
</dependency>
```
##### Usage

##### DTOs
For each DTO that you need to paginate, you need to extend **LinksPageItems** or the **PageItems** classes.
* **LinksPageItems** can be extended if you want to include the links in the response
* **PageItems** can be extended for simple responses without links.

See the UserPaginated dto on the project:
```java
public class UserPaginated extends LinksPageItems<UserPaginated.Users>
```

On both cases, you will have to implement getItems method. In order to comply with TalkDesk Api-Gateway,
you must define a new class, **Users** in which attribute is the list of the object you are returning.
This class should also implement **ListItem<T>** so that implements the getList method

```java
 public static class Users implements ListItem<UserRead>
```

This is needed to generate the proper openAPI schema.

##### Sorting
This is optional. The SortHelper is provided to handle, in a generic way the sorting attributes.
You just need to create an enum like **UserSortOption** implementing the **SortOption** interface. 
```java
public enum UserSortOption implements SortOption
```

Please mind that to sort desc you must use `key:desc` and `key:asc` to sort asc. The **UserSortOption** example shows the examples firstName:desc and firstName:asc for the value firstName.

##### Links
This is optional. If you want to use the **LinksPageItems**, to generate the links.

##### Query parameter validation
Annotating a JAX-RS endpoint with **@PaginatedResource** will trigger an interceptor that will enforce the input of
 the pagination guidelines set by Talkdesk here: https://talkdesk.github.io/api-docs/#collection-resources-must-support-pagination.
The current understanding of the guidelines is that all APIs should work on a 1-based index and they must have a 
'per_page' and a 'page' query parameters.

By default, the interceptor will apply the following validations:
* If 'per_page' or 'page' are less than 1, throw an exception.
* If 'page' is absent, then set it to the default value.

The error codes thrown by the library are handed to the client application, which must implement the 
*PaginatedResourceErrorCode* interface.
Pages that are out of bonds must be handled by the client as well.

#### Multi-tenancy from TDX-Athena
TODO

#### Documentation
TODO

#### Application Provisioning

TODO - We need this to create the 1st endpoint and the 1st API docs PR. 
