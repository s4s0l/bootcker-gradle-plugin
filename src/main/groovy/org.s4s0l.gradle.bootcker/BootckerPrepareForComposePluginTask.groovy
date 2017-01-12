package org.s4s0l.gradle.bootcker

import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeDown
import com.avast.gradle.dockercompose.tasks.ComposeUp
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

/**
 * @author Matcin Wielgus
 */
class BootckerPrepareForComposePluginTask extends DefaultTask {


    static void createWithComposeAndTaskSetup(Project project, Task taskWrapped, String tmpDirectoryName, Closure c) {
        String taskName = taskWrapped.name
        assert taskWrapped != null: "Task named $taskName not found in project"


        ComposeUp composeUpTask = project.tasks.create("bootckerComposeUp-$taskName", ComposeUp)
        ComposeDown composeDownTask = project.tasks.create("bootckerComposeDown-$taskName", ComposeDown)

        def taskExtension = new BootckerPrepareForComposePluginTaskExtension(project, composeUpTask, composeDownTask)
        composeUpTask.extension = taskExtension
        composeUpTask.downTask = composeDownTask
        composeDownTask.extension = taskExtension
        c.delegate = taskExtension
        c.call()

        BootckerPrepareForComposePluginTask prepareTask = create("bootckerPrepare-$taskName",
                project, tmpDirectoryName, taskExtension)


        def wrappedTaskSetupTask = BootcerWrappedTaskSetupTask.create("bootckerWrappedSetup-$taskName", project, taskWrapped, taskExtension)

        composeUpTask.dependsOn prepareTask
        wrappedTaskSetupTask.dependsOn composeUpTask
        taskWrapped.dependsOn wrappedTaskSetupTask
        taskWrapped.finalizedBy composeDownTask
    }




    static BootckerPrepareForComposePluginTask  create(String name, Project project, String  tempDirectoryName, BootckerPrepareForComposePluginTaskExtension taskExtension) {
        BootckerPrepareForComposePluginTask prepareTask = project.tasks.create(name,
                BootckerPrepareForComposePluginTask)
        prepareTask.tempDirectoryName = tempDirectoryName
        prepareTask.extension = taskExtension

        taskExtension.createProjectDependencies(prepareTask)
        return prepareTask
    }


    BootckerPrepareForComposePluginTaskExtension extension
    String tempDirectoryName

    BootckerPrepareForComposePluginTask() {
        group = 'bootcker'
        description = 'Prepares compose yml files and creates dependencies based on it'
    }

    @TaskAction
    void up() {
        def preparator = new BootckerComposePreparator(project, tempDirectoryName)

        extension.useComposeFiles = preparator.prepare(extension.getExistingComposeFiles()).values().toArray()
    }

}


class BootckerPrepareForComposePluginTaskExtension extends ComposeExtension implements ComposeFilesContainer {
    private final meProject

    BootckerPrepareForComposePluginTaskExtension(Project project, ComposeUp upTask, ComposeDown downTask) {
        super(project, upTask, downTask)
        this.meProject = project
    }

    @Override
    Map<String, String> getDeclaredComposeFiles() {
        return useComposeFiles.withIndex().collectEntries { it, index -> [index, it] }
    }

    @Override
    Project getProject() {
        return this.meProject
    }
}
