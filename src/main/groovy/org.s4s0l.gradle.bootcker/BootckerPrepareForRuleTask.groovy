package org.s4s0l.gradle.bootcker

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaForkOptions

/**
 * @author Matcin Wielgus
 */
class BootckerPrepareForRuleTask extends DefaultTask {

    static BootckerPrepareForRuleTask create(String taskName, Project project, Task taskWrapped, Closure extensionClosure){
        BootckerPrepareForRuleExtension taskExtension = new BootckerPrepareForRuleExtension(project)
        taskExtension.with extensionClosure
        BootckerPrepareForRuleTask prepareTask = project.tasks.create(taskName,
                BootckerPrepareForRuleTask)
        prepareTask.wrappedTask = taskWrapped
        prepareTask.extension = taskExtension
        taskExtension.createProjectDependencies(prepareTask)
        taskWrapped.dependsOn prepareTask
        prepareTask
    }

    BootckerPrepareForRuleExtension extension
    Task wrappedTask


    BootckerPrepareForRuleTask() {
        group = 'bootcker'
        description = 'Prepares compose yml files and creates dependencies based on it'
    }

    @TaskAction
    void up() {
        def preparator = new BootckerComposePreparator(project, "bootcker-${project.name}-${wrappedTask.name}")
        JavaForkOptions task = wrappedTask
        preparator.prepare(extension.getExistingComposeFiles()).forEach {
            k, v ->
                task.systemProperties.put("bootcker.${k.key}".toString(), v)
        }
    }

}

class BootckerPrepareForRuleExtension extends LinkedHashMap<String, String>
        implements ComposeFilesContainer {
    final Project project

    BootckerPrepareForRuleExtension(Project project) {
        this.project = project
    }

    @Override
    Map<String, String> getDeclaredComposeFiles() {
        return this
    }

    Project getProject(){
        return this.project
    }

}