package org.s4s0l.gradle.bootcker

import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeDown
import com.avast.gradle.dockercompose.tasks.ComposeUp
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Matcin Wielgus
 */
class BootckerExtension {
    Project project;

    BootckerExtension(Project project) {
        this.project = project
    }

    def methodMissing(String name, args) {
        validateArgs(args)
        Task taskWrapped = project.tasks.getByName(name)
        assert taskWrapped != null: "Task named $name not found in project"


        ComposeUp composeUpTask = project.tasks.create("bootckerComposeUp-$name", ComposeUp)
        ComposeDown composeDownTask = project.tasks.create("bootckerComposeDown-$name", ComposeDown)
        def taskExtension = new BootckerTaskExtension(project, composeUpTask, composeDownTask)

        composeUpTask.extension = taskExtension
        composeUpTask.downTask = composeDownTask
        composeDownTask.extension = taskExtension



        Closure c = args[0]
        c.delegate = taskExtension
        c.call()







        BootckerPrepareTask prepareTask = project.tasks.create("bootckerPrepare-$name",
                BootckerPrepareTask)
        prepareTask.wrappedTask = taskWrapped;
        prepareTask.extension = taskExtension;



        taskExtension.composeFiles.forEach {
            it.applyCustomizer({ it == 'bootcker' }) {
                prepareTask.dependsOn project.tasks.getByName('assemble')
                [:]
            }
            it.applyCustomizer({ it.startsWith('bootcker:') }) {
                def referencedProject = BootckerPrepareTask.findProject(project, it.image)
                prepareTask.dependsOn referencedProject.tasks.getByName('assemble')
                [:]
            }
        }

        BootcerWrappedTaskSetupTask wrappedTaskSetupTask = project.tasks.create("bootckerWrappedSetup-$name",
                BootcerWrappedTaskSetupTask)

        wrappedTaskSetupTask.extension = taskExtension
        wrappedTaskSetupTask.wrappedTask = taskWrapped

        composeUpTask.dependsOn prepareTask
        wrappedTaskSetupTask.dependsOn composeUpTask
        taskWrapped.dependsOn wrappedTaskSetupTask
        taskWrapped.finalizedBy composeDownTask
        return null
    }


    def validateArgs(Object args) {
        if (!args instanceof Closure) {
            throw new RuntimeException("bootcker extension needs to have configuration closure present")
        }
    }
}


class BootckerTaskExtension extends ComposeExtension {
    private final project;

    BootckerTaskExtension(Project project, ComposeUp upTask, ComposeDown downTask) {
        super(project, upTask, downTask)
        this.project = project;
    }

    private getExistingComposeFiles() {
        String[] composeFiles = useComposeFiles.empty ? ['docker-compose.yml', 'docker-compose.override.yml'] : useComposeFiles
        return composeFiles
                .findAll { project.file(it).exists() }
                .collect { project.file(it) }
    }

    Collection<ComposeFile> getComposeFiles() {
        existingComposeFiles
                .collect { new ComposeFile(it) };
    }
}