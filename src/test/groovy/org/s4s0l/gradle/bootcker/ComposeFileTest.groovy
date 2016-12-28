package org.s4s0l.gradle.bootcker

import spock.lang.Specification

/**
 * @author Matcin Wielgus
 */
class ComposeFileTest extends Specification {

    def "should write what was read"() {
        given:
        ComposeFile f = new ComposeFile("a",new File("./projects/standalone/src/test/resources/docker-compose.yml"))
        def destFile = new File("./build/written.yml")

        when:
        f.writeToFile(destFile)

        then:
        destFile.text.contains("image: bootcker")
    }


    def "shoud apply customizer changes"(){
        given:
        ComposeFile f = new ComposeFile("a",new File("./projects/standalone/src/test/resources/docker-compose.yml"))
        def destFile = new File("./build/written2.yml")

        when:
        f.applyCustomizer({it.startsWith("bootcker")}) {
            ['build':['context':'.', 'xxx':'x']]
        }

        then:
        f.composeConfig.services.me.build.context == '.'
        f.composeConfig.services.me.build.xxx == 'x'
        f.composeConfig.services.me.image == 'bootcker'

        when:
        f.writeToFile(destFile)
        def f2 = new ComposeFile("A", destFile);


        then:
        f2.composeConfig.services.me.build.context == '.'
        f2.composeConfig.services.me.build.xxx == 'x'
        f2.composeConfig.services.me.image == 'bootcker'

    }
}
