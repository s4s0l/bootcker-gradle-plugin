package org.s4s0l.gradle.bootcker

import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Matcin Wielgus
 */
trait ComposeFilesContainer {
    //key 2 path
    abstract Map<String, String> getDeclaredComposeFiles()
    abstract Project getProject()

    Map<String,String> getExistingComposeFilesAsFiles() {
        def composeFiles = getDeclaredComposeFiles().empty ? [1:'docker-compose.yml', 2:'docker-compose.override.yml'] : getDeclaredComposeFiles()
        return composeFiles
                .findAll { getProject().file(it.value).exists() }
                .collectEntries { [(it.key):getProject().file(it.value)] }
    }

    Collection<ComposeFile> getExistingComposeFiles() {
        getExistingComposeFilesAsFiles()
                .collect { new ComposeFile("${it.key}", it.value) }
    }

    def createProjectDependencies(Task dependantTask){
        this.getExistingComposeFiles().forEach {
            it.applyCustomizer({ it == 'bootcker' }) {
                dependantTask.dependsOn getProject().tasks.getByName('assemble')
                [:]
            }
            it.applyCustomizer({ it.startsWith('bootcker:') }) {
                def referencedProject = BootckerComposePreparator.findProject(getProject(), it.image)
                dependantTask.dependsOn referencedProject.tasks.getByName('assemble')
                [:]
            }
        }
    }
}
