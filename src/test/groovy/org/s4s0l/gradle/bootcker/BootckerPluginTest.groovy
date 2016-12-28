package org.s4s0l.gradle.bootcker

import com.avast.gradle.dockercompose.tasks.ComposeDown
import com.avast.gradle.dockercompose.tasks.ComposeUp
import org.gradle.api.tasks.testing.Test
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
            sourceSets {
                intTest
            }

            task('intTest', type: Test) {
            }

            task('intTest2', type: Test) {
            }
            task('intTest3', type: Test) {
            }
            task('intTest4', type: Test) {
            }
            bootcker {
                test {
                    useComposeFiles = ["../../projects/standalone/src/test/resources/docker-compose.yml"]
                }
                runAround(intTest) {
                    useComposeFiles = ["../../projects/standalone/src/test/resources/docker-compose.yml"]
                }
                runAround('intTest2') {
                    useComposeFiles = ["../../projects/standalone/src/test/resources/docker-compose.yml"]
                }
                prepareFor('intTest3') {
                    a = "../../projects/standalone/src/test/resources/docker-compose.yml"
                    b = "../../projects/standalone/src/test/resources/docker-compose.yml"
                    c = "../../projects/standalone/src/test/resources/docker-compose.yml"
                }
                prepareFor(intTest4) {
                    a = "../../projects/standalone/src/test/resources/docker-compose.yml"
                }

            }
        }

        then:
        project.extensions.findByName('bootcker') instanceof BootckerExtension
        project.tasks.getByName("bootckerPrepare-test") instanceof BootckerPrepareForComposePluginTask
        project.tasks.getByName("bootckerComposeUp-test") instanceof ComposeUp
        project.tasks.getByName("bootckerComposeDown-test") instanceof ComposeDown
        project.tasks.getByName("bootckerPrepare-test").dependsOn.contains(project.tasks.getByName("assemble"))
        project.tasks.getByName("test").dependsOn.contains(project.tasks.getByName("bootckerWrappedSetup-test"))

        project.tasks.getByName("bootckerPrepare-intTest") instanceof BootckerPrepareForComposePluginTask
        project.tasks.getByName("bootckerComposeUp-intTest") instanceof ComposeUp
        project.tasks.getByName("bootckerComposeDown-intTest") instanceof ComposeDown
        project.tasks.getByName("bootckerPrepare-intTest").dependsOn.contains(project.tasks.getByName("assemble"))


        project.tasks.getByName("bootckerPrepare-intTest2") instanceof BootckerPrepareForComposePluginTask
        project.tasks.getByName("bootckerComposeUp-intTest2") instanceof ComposeUp
        project.tasks.getByName("bootckerComposeDown-intTest2") instanceof ComposeDown
        project.tasks.getByName("bootckerPrepare-intTest2").dependsOn.contains(project.tasks.getByName("assemble"))


        project.tasks.getByName("bootckerPrepareRule-intTest3") instanceof BootckerPrepareForRuleTask
        project.tasks.getByName("bootckerPrepareRule-intTest3").dependsOn.contains(project.tasks.getByName("assemble"))
        project.tasks.getByName("intTest3").dependsOn.contains(project.tasks.getByName("bootckerPrepareRule-intTest3"))

        project.tasks.getByName("bootckerPrepareRule-intTest4") instanceof BootckerPrepareForRuleTask
        project.tasks.getByName("bootckerPrepareRule-intTest4").dependsOn.contains(project.tasks.getByName("assemble"))
        project.tasks.getByName("intTest4").dependsOn.contains(project.tasks.getByName("bootckerPrepareRule-intTest4"))

//        when:
//        project.tasks.getByName("bootckerPrepare-test").up();
//
//        then:
//        1 ==1

    }
}
