package org.s4s0l.gradle.bootcker

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaForkOptions

/**
 * @author Matcin Wielgus
 */
class BootcerWrappedTaskSetupTask extends DefaultTask {

    static BootcerWrappedTaskSetupTask create(String name, Project project,Task taskWrapped, BootckerPrepareForComposePluginTaskExtension taskExtension){
        BootcerWrappedTaskSetupTask wrappedTaskSetupTask = project.tasks.create(name,
                BootcerWrappedTaskSetupTask)

        wrappedTaskSetupTask.extension = taskExtension
        wrappedTaskSetupTask.wrappedTask = taskWrapped
        return wrappedTaskSetupTask
    }

    BootckerPrepareForComposePluginTaskExtension extension
    Task wrappedTask

    BootcerWrappedTaskSetupTask() {
        group = 'bootcker'
        description = 'Exposes system properties to wrapped task'
    }


    @TaskAction
    void setupWrappedTask() {
        extension.exposeAsSystemProperties(wrappedTask as JavaForkOptions)
    }
}
