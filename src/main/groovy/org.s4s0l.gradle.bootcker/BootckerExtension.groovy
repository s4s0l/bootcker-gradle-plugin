package org.s4s0l.gradle.bootcker

import com.avast.gradle.dockercompose.tasks.ComposeDown
import com.avast.gradle.dockercompose.tasks.ComposeUp
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Matcin Wielgus
 */
class BootckerExtension {
    Project project;

    BootckerExtension(Project project) {
        this.project = project
    }
    def prepareFor(Task taskWrapped, Closure c){
        String taskName = taskWrapped.name


        BootckerPrepareForRuleExtension taskExtension = new BootckerPrepareForRuleExtension(project)
        taskExtension.with c

        BootckerPrepareForRuleTask prepareTask = project.tasks.create("bootckerPrepareRule-$taskName",
                BootckerPrepareForRuleTask)
        prepareTask.wrappedTask = taskWrapped;
        prepareTask.extension = taskExtension;


        createProjectDependencies(prepareTask, taskExtension)
        taskWrapped.dependsOn prepareTask
    }

    def prepareFor(String name, Closure c){
        Task taskWrapped = project.tasks.getByName(name)
        prepareFor(taskWrapped,c)
    }

    def methodMissing(String taskName, args) {
        validateArgs(args)
        Closure c = args[0]
        runAround(taskName, c)
    }

    def runAround(String task, Closure c){
        Task taskWrapped = project.tasks.getByName(task)
        runAround(taskWrapped, c)
    }

    def runAround(Task taskWrapped, Closure c){

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

        BootckerPrepareForComposePluginTask prepareTask = project.tasks.create("bootckerPrepare-$taskName",
                BootckerPrepareForComposePluginTask)
        prepareTask.wrappedTask = taskWrapped;
        prepareTask.extension = taskExtension;

        createProjectDependencies(prepareTask, taskExtension)

        BootcerWrappedTaskSetupTask wrappedTaskSetupTask = project.tasks.create("bootckerWrappedSetup-$taskName",
                BootcerWrappedTaskSetupTask)

        wrappedTaskSetupTask.extension = taskExtension
        wrappedTaskSetupTask.wrappedTask = taskWrapped

        composeUpTask.dependsOn prepareTask
        wrappedTaskSetupTask.dependsOn composeUpTask
        taskWrapped.dependsOn wrappedTaskSetupTask
        taskWrapped.finalizedBy composeDownTask
        return null
    }

    def createProjectDependencies(Task dependantTask, ComposeFilesContainer container){
        container.getExistingComposeFiles().forEach {
            it.applyCustomizer({ it == 'bootcker' }) {
                dependantTask.dependsOn project.tasks.getByName('assemble')
                [:]
            }
            it.applyCustomizer({ it.startsWith('bootcker:') }) {
                def referencedProject = BootckerComposePreparator.findProject(project, it.image)
                dependantTask.dependsOn referencedProject.tasks.getByName('assemble')
                [:]
            }
        }
    }


    def validateArgs(Object args) {
        if (!args instanceof Closure) {
            throw new RuntimeException("bootcker extension needs to have configuration closure present")
        }
    }
}



