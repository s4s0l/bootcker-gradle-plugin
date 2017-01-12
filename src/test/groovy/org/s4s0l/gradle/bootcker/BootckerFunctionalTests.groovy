package org.s4s0l.gradle.bootcker

import org.s4s0l.gradle.bootcker.utils.GradlePluginFunctionalSpecification

/**
 * @author Matcin Wielgus
 */
class BootckerFunctionalTests extends GradlePluginFunctionalSpecification {

    def "Standalone project should be able to reference self in compose"() {
        given:
        useProjectStructure "./projects/standalone"

        when:
        run 'test'

        then:
        noExceptionThrown()
        file("build/libs/${rootProjectName}.jar").exists()
    }

    def "In multi project builds projects should be able to reference each other in compose"() {
        given:
        useProjectStructure "./projects/multiproject"

        when:
        run 'test'

        then:
        noExceptionThrown()
        file("app1/build/libs/app1.jar").exists()
        file("app2/build/libs/app2.jar").exists()
    }

    def "Standalone with rules project should be able to reference self in docker compose"() {
        given:
        useProjectStructure "./projects/standalone-rule"

        when:
        run 'test'

        then:
        noExceptionThrown()
        file("build/libs/${rootProjectName}.jar").exists()
    }


}
