package org.s4s0l.gradle.bootcker

import org.gradle.api.Action
import org.gradle.api.internal.AbstractTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Matcin Wielgus
 */
class BootckerPluginTest extends Specification {
    def "Apply"() {
        def project = ProjectBuilder.builder().build()
        when:
        project.plugins.apply 'bootcker'
        then:
        project.extensions.findByName('bootcker') instanceof BootckerExtension
    }


    def "Sample projects"() {
        def project = ProjectBuilder.builder()
                .withProjectDir(new File("./sample/app1"))
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
        }

        then:
        project.extensions.findByName('bootcker') instanceof BootckerExtension




    }
}
