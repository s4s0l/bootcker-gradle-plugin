package org.s4s0l.gradle.bootcker

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

/**
 * @author Matcin Wielgus
 */
class BootcerWrappedTaskSetupTask extends DefaultTask {

    BootckerPrepareForComposePluginTaskExtension extension;
    Task wrappedTask

    BootcerWrappedTaskSetupTask() {
        group = 'bootcker'
        description = 'Exposes system properties to wrapped task'
    }


    @TaskAction
    void setupWrappedTask() {
        extension.exposeAsSystemProperties(wrappedTask)
    }
}
