# bootcker-gradle-plugin

[![License](https://img.shields.io/badge/License-Apache_2.0-7D287B.svg)](https://raw.githubusercontent.com/s4s0l/bootcker-gradle-plugin/master/LICENSE)
[![PluginPortal](https://img.shields.io/badge/PluginPortal-latest-blue.svg)](https://plugins.gradle.org/plugin/org.s4s0l.gradle.bootcker)
[![Download](https://api.bintray.com/packages/sasol-oss/maven/bootcker-gradle-plugin/images/download.svg) ](https://bintray.com/sasol-oss/maven/bootcker-gradle-plugin/_latestVersion)
[![Build Status](https://travis-ci.org/s4s0l/bootcker-gradle-plugin.svg?branch=master)](https://travis-ci.org/s4s0l/bootcker-gradle-plugin)

Gradle plugin for running spring boot based micro services with docker compose during gradle build.

Useful especially when you have multi project gradle build containing a few spring boot applications and you wish to do some integration tests.
The best thing you can find for testing microservicish components in gradle is [https://github.com/avast/docker-compose-gradle-plugin].
Great stuff there, but when is combined with multi project build can leave a lot of boilerplate code and clumsy 
 gradle hacks. Because of how docker tools work, not the plugin:) 
 
 This plugin targets in simplifying gradle configuration to accomplish very particular case of handling 
  docker-compose-gradle-plugin in case tests in one project need to start up containers with spring-boot components from other projects.
  
# Usage  

Working examples can be found in ./projects directory in repository.

## Using [docker-compose-gradle-plugin](https://github.com/avast/docker-compose-gradle-plugin)

Lets's assume there are two projects app1 and app2. Both are spring boot applications 
(they have spring-boot gradle plugin applied and they produce runnable jar). Then in
 app1's build.gradle:

```
//or use old way described on plugin portal (see badge above)
plugins {
  id "org.s4s0l.gradle.bootcker" version HERE_PUT_VERSION_AS_IN_BADGE_ABOVE
}

bootcker {

    //this name is a name of a task as in docker-compose-gradle-plugin 'isRequiredBy'
    //if there is other task used for integration tests use its name here
    // you can also use syntax runAround(intTest) { ... }
    test {
    
        //here can be any configuration from docker-compose-gradle-plugin
        useComposeFiles = ["./src/test/resources/docker-compose.yml"]
    }
    
    //there can be many sesions for other tasks 
    ...
}    
```

The docker-compose.yml can look like:
```
version: '2'
services:
  me:
    #The bootcker image will be replaced by container created for current project
    # in this case app1
    image: bootcker
    command: [--app1.someProperty=propertyValue]
    ports:
     - 8080
  other:
    #this way we reference other projects
    image: bootcker:app2 
    ports:
     - 8080
  #there can be many other service definitions of course...   
```

And thats it.

Plugin will generate Dockerfiles for each project, and rewrite docker-compose.yml
to use them. Be aware that working directory will not be where docker-compose.yml lies.
It will be recreated in build/bootcker-temp-test directory, where you can find how 
the Dockerfiles look like, and run it by hand if you need to.

## Using [docker-compose-rule](https://github.com/palantir/docker-compose-rule)

Using docker compose gradle plugin may be cumbersome if for each test you need different
docker setup. Docker compose rule library makes it possible to run each test against 
different compose file. For details see its documentation. Bootcker can do the same as above 
but for this library too. 
In this usage it generates compose files and docker files for spring boot projects, 
as described above, but does not start compose plugin. Instead it exposes 
created ymls paths as system property to be used during any JavaForkOptions task (Test for example).
``` 
bootcker {

    //in this example test is a gradle task before which we want ymls to be generated
    prepareFor(test) {
          config1 = "./src/test/resources/docker-compose1.yml"
          config2 = "./src/test/resources/docker-compose2.yml"
    }
```
And then in your docker-compose-rule enabled test you can reference yml's via system properties.
In above example there would be 2 properties exposed for each file: `bootcker.config1` and
 `bootcker.config2`. Can be used as follows:
```$java
public class Test {


	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file(System.getProperty("bootcker.config1"))
			.build();
	
	//tests here		
}			

```      


# Based on:

My work is heavily based and inspired by:

* [https://github.com/avast/docker-compose-gradle-plugin] - I used this project as a template, and am using it internally.
* [https://github.com/groovy/groovy-android-gradle-plugin] - From here I've taken approach to testing gradle plugins. 
* [https://github.com/palantir/docker-compose-rule] - Other plugin I find useful.

