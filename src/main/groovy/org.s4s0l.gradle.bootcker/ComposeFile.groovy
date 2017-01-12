package org.s4s0l.gradle.bootcker

import org.yaml.snakeyaml.Yaml

import java.util.function.Predicate

/**
 * @author Matcin Wielgus
 */
class ComposeFile {

    final Map<String, Object> composeConfig
    final File originalFile
    final String key

    ComposeFile(String key, File f) {
        this.key = key
        this.composeConfig = new Yaml().load(f.text)
        originalFile = f
    }

    boolean isCurrentProjectNeeded() {
        def services = '2' == composeConfig.get('version') ? ((Map) composeConfig.get('services')) : composeConfig
        services.find {
            it.value?.image == 'bootcker'
        }
    }

    def applyCustomizer(Predicate<String> imageNameMatcher, ServiceCustomizer customizer) {
        def services = '2' == composeConfig.get('version') ? ((Map) composeConfig.get('services')) : composeConfig
        services.findAll {
            it.value?.image != null
        }.findAll {
            imageNameMatcher.test(it.value.image)
        }.forEach {
            k, v ->
                def modify = customizer.modify(v)
                v.putAll(modify)
        }
    }

    void writeToFile(File f) {
        f.text = new Yaml().dump(composeConfig)
    }
}

interface ServiceCustomizer {
    Map<String, Object> modify(Map<String, Object> bootckerServiceConfig)
}


