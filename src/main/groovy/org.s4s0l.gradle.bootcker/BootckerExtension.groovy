package org.s4s0l.gradle.bootcker

import org.gradle.api.Project

/**
 * @author Matcin Wielgus
 */
class BootckerExtension {
    Project project;

    BootckerExtension(Project project) {
        this.project = project
    }
}
