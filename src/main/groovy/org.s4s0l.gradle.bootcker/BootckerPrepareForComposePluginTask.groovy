package org.s4s0l.gradle.bootcker

import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.tasks.ComposeDown
import com.avast.gradle.dockercompose.tasks.ComposeUp
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction;

/**
 * @author Matcin Wielgus
 */
class BootckerPrepareForComposePluginTask extends DefaultTask {

    BootckerPrepareForComposePluginTaskExtension extension;
    Task wrappedTask

    BootckerPrepareForComposePluginTask() {
        group = 'bootcker'
        description = 'Prepares compose yml files and creates dependencies based on it'
    }

    @TaskAction
    void up() {
        def preparator = new BootckerComposePreparator(project, wrappedTask);

        extension.useComposeFiles = preparator.prepare(extension.getExistingComposeFiles()).values().toArray();
    }

}



class BootckerPrepareForComposePluginTaskExtension extends ComposeExtension implements ComposeFilesContainer{
    private final meProject;

    BootckerPrepareForComposePluginTaskExtension(Project project, ComposeUp upTask, ComposeDown downTask) {
        super(project, upTask, downTask)
        this.meProject = project;
    }

    @Override
    Map<String,String> getDeclaredComposeFiles() {
        return useComposeFiles.withIndex().collectEntries { it, index -> [index, it]}
    }

    @Override
    Project getProject() {
        return this.meProject
    }
}
