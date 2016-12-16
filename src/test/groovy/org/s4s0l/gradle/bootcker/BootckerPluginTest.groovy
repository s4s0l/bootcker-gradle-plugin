package org.s4s0l.gradle.bootcker

import com.avast.gradle.dockercompose.tasks.ComposeDown
import com.avast.gradle.dockercompose.tasks.ComposeUp
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Matcin Wielgus
 */
class BootckerPluginTest extends Specification {
    def "it could be possible to apply plugin"() {
        def project = ProjectBuilder.builder().build()
        when:
        project.plugins.apply 'bootcker'
        then:
        project.extensions.findByName('bootcker') instanceof BootckerExtension
    }


    def "plugin should create proper task dependency tree for standalone project"() {
        def project = ProjectBuilder.builder()
                .withProjectDir(new File("./build/BootckerPluginTestWorkDir"))
                .withGradleUserHomeDir(new File("./build/tmpUserHome"))
                .build()
        when:
        project.with {
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
            bootcker {
                test {
                    useComposeFiles = ["../../projects/standalone/src/test/resources/docker-compose.yml"]
                }
            }
        }

        then:
        project.extensions.findByName('bootcker') instanceof BootckerExtension
        project.tasks.getByName("bootckerPrepare-test") instanceof BootckerPrepareTask
        project.tasks.getByName("bootckerComposeUp-test") instanceof ComposeUp
        project.tasks.getByName("bootckerComposeDown-test") instanceof ComposeDown
        project.tasks.getByName("bootckerPrepare-test").dependsOn.contains(project.tasks.getByName("assemble"))


//        when:
//        project.tasks.getByName("bootckerPrepare-test").up();
//
//        then:
//        1 ==1

    }
}
