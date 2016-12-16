package org.s4s0l.gradle.bootcker

import groovy.text.SimpleTemplateEngine
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction;

/**
 * @author Matcin Wielgus
 */
class BootckerPrepareTask extends DefaultTask {

    BootckerTaskExtension extension;
    Task wrappedTask

    BootckerPrepareTask() {
        group = 'bootcker'
        description = 'Stops and removes all containers of docker-compose project'
    }

    @TaskAction
    void up() {
        def workDir = prepareWorkingDirectory()
        def files = extension.composeFiles
        extension.exposeAsSystemProperties(wrappedTask)
        extension.useComposeFiles = []

        files.forEach {
            it.applyCustomizer({ it == 'bootcker' }) {
                return extractProject(workDir, project)
            }
            it.applyCustomizer({ it.startsWith('bootcker:') }) {
                Project otherProject = findProject(project, it.image);
                return extractProject(workDir, otherProject)
            }
            def outFile = new File(workDir, it.originalFile.getName())
            it.writeToFile(outFile)
            extension.useComposeFiles += outFile.absolutePath
        }
    }

    protected LinkedHashMap<String, LinkedHashMap<String, String>> extractProject(File workDir, Project otherProject) {
        def serviceDir = new File(workDir, otherProject.name)
        serviceDir.mkdirs()

        def jar = getApplicationJar(otherProject)
        FileUtils.copyFile(jar, new File(serviceDir, jar.name))

        createEntrypointScript(serviceDir)
        createDockerFile(serviceDir, [application_jar: jar.name,
                                      version        : otherProject.version])
        return [build: [context: "./${otherProject.name}".toString(), dockerfile: 'Dockerfile']]
    }

    static Project findProject(Project rootProject, String imageName) {
        String projectName = imageName.substring(imageName.indexOf(":"))
        rootProject.project(projectName)
    }

    protected File createDockerFile(File serviceDir, LinkedHashMap<String, Object> templateContext) {
        new File(serviceDir, "Dockerfile") << new SimpleTemplateEngine()
                .createTemplate(this.getClass().getResource('/templates/Dockerfile').text)
                .make(templateContext)
    }

    protected File createEntrypointScript(File serviceDir) {
        new File(serviceDir, "entrypoint.sh") << """#!/bin/sh
                            echo "Running with JAVA_OPTS=\$JAVA_OPTS and argsuments \$@"
                            exec java \$JAVA_OPTS -jar ./service.jar \$@
                            """
    }

    protected File getApplicationJar(Project prj) {
        def filesToCopy = prj.configurations.archives.allArtifacts
                .findAll { it.type == 'jar' }
                .collect { it.file }
        if (filesToCopy.size() == 0) {
            throw new RuntimeException("Unable to find jar archive!")
        }
        if (filesToCopy.size() > 1) {
            throw new RuntimeException("Multiple jar artifacts found, currently only one is supported.")
        }
        def applicationJar = filesToCopy[0]
        applicationJar
    }

    protected File prepareWorkingDirectory() {
        def workDir = new File(project.buildDir, "bootcker-temp-${wrappedTask.name}");
        if (workDir.exists()) {
            FileUtils.deleteDirectory(workDir)
        }
        workDir.mkdirs();
        return workDir;
    }
}
