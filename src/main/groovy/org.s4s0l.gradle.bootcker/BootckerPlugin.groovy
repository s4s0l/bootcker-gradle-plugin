package org.s4s0l.gradle.bootcker


import org.gradle.api.Plugin
import org.gradle.api.Project

class BootckerPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        BootckerExtension extension = project.extensions.create('bootcker', BootckerExtension, project)
    }
}
