package org.s4s0l.gradle.bootcker

import com.avast.gradle.dockercompose.tasks.ComposeDown
import com.avast.gradle.dockercompose.tasks.ComposeUp
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Matcin Wielgus
 */
class BootckerExtension {
    Project project

    BootckerExtension(Project project) {
        this.project = project
    }

    def prepareFor(Task taskWrapped, Closure c) {
        BootckerPrepareForRuleTask.create("bootckerPrepareRule-${taskWrapped.name}", project, taskWrapped, c)
    }

    def prepareFor(String name, Closure c) {
        Task taskWrapped = project.tasks.getByName(name)
        prepareFor(taskWrapped, c)
    }

    def methodMissing(String taskName, args) {
        Closure c = args[0]
        runAround(taskName, c)
    }

    def runAround(String task, Closure c) {
        Task taskWrapped = project.tasks.getByName(task)
        runAround(taskWrapped, c)
    }

    def runAround(Task taskWrapped, Closure c) {
        BootckerPrepareForComposePluginTask.createWithComposeAndTaskSetup(project, taskWrapped,
                "bootcker.${Utils.friendlyProjectName(project.name)}.${taskWrapped.name}", c)
        return null
    }


}



