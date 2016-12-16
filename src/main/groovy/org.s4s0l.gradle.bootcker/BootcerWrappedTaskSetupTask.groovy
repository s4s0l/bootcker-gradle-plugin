package org.s4s0l.gradle.bootcker

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

/**
 * @author Matcin Wielgus
 */
class BootcerWrappedTaskSetupTask extends DefaultTask {

    BootckerTaskExtension extension;
    Task wrappedTask

    BootcerWrappedTaskSetupTask() {
        group = 'bootcker'
        description = 'Stops and removes all containers of docker-compose project'
    }


    @TaskAction
    void setupWrappedTask() {
        extension.exposeAsSystemProperties(wrappedTask)
    }
}
