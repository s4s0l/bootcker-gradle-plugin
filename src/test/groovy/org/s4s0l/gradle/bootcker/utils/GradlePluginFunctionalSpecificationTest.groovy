package org.s4s0l.gradle.bootcker.utils
/**
 * @author Matcin Wielgus
 */
class GradlePluginFunctionalSpecificationTest extends GradlePluginFunctionalSpecification {

    def "Inlined project with this plugin enabled should compile successfully"() {
        given:


        buildFile << """

            buildscript {
                repositories {
                    jcenter()
                    maven {
                        url "https://plugins.gradle.org/m2/"
                    }
                    maven {
                        url "\${System.getProperty('bootcker_localrepo') }"
                    }
                }
                dependencies {
                    classpath 'io.spring.gradle:dependency-management-plugin:0.6.0.RELEASE'
                    classpath("org.springframework.boot:spring-boot-gradle-plugin:1.3.6.RELEASE")
                    classpath "com.avast.gradle:docker-compose-gradle-plugin:0.3.7"
                    classpath('se.transmode.gradle:gradle-docker:1.2')
                    classpath "io.franzbecker:gradle-lombok:1.7"
                    classpath "org.s4s0l.gradle:bootcker-gradle-plugin:\${System.getProperty('bootcker_project_version')}"
                }
            }

            group 'org.s4s0l.gradle.bootcker.sample'
            apply plugin: 'java'
            apply plugin: 'io.spring.dependency-management'
            repositories {
                jcenter()
            }
            ext['bouncycastle.version'] = '1.54'
            dependencyManagement {
                imports {
                    mavenBom 'org.springframework.boot:spring-boot-starter-parent:1.4.1.RELEASE'
                    mavenBom 'org.springframework.cloud:spring-cloud-starter-parent:Camden.RELEASE'
                }
            }

            apply plugin: 'spring-boot'
            apply plugin: 'bootcker'
            dependencies {
                compile("org.springframework.boot:spring-boot-starter-web")
            }
        
        """

        file('src/main/java/app1/Main.java') <<
                """
            package app1;
            import org.springframework.boot.*;
            import org.springframework.boot.autoconfigure.*;
            import org.springframework.stereotype.*;
            import org.springframework.web.bind.annotation.*;
            
            @Controller
            @EnableAutoConfiguration
            public class Main {
            
                @RequestMapping("/")
                @ResponseBody
                String home() {
                    return "Hello World!";
                }
            
                public static void main(String[] args) throws Exception {
                    SpringApplication.run(Main.class, args);
                }
            }
        """

        when:
        run 'bootRepackage'

        then:
        noExceptionThrown()

        file("build/libs/${rootProjectName}.jar").exists()

    }

    def "External project template with this plugin enabled should compile successfully"() {
        given:
        useProjectStructure "./projects/simple"

        when:
        run 'bootRepackage'

        then:
        noExceptionThrown()
        file("build/libs/${rootProjectName}.jar").exists()
    }

}