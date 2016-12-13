package org.s4s0l.gradle.bootcker


import org.gradle.api.Plugin
import org.gradle.api.Project

class BootckerPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println "OK"
        BootckerExtension extension = project.extensions.create('bootcker', BootckerExtension, project)
//        ComposeUp upTask = project.tasks.create('composeUp', ComposeUp)
//        ComposeDown downTask = project.tasks.create('composeDown', ComposeDown)
//        ComposeExtension extension = project.extensions.create('dockerCompose', ComposeExtension, project, upTask, downTask)
//        upTask.extension = extension
//        upTask.downTask = downTask
//        downTask.extension = extension
    }
}
