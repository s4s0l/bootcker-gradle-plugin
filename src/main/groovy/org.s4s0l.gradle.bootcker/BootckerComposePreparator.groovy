package org.s4s0l.gradle.bootcker

import groovy.text.SimpleTemplateEngine
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Matcin Wielgus
 */
class BootckerComposePreparator {

    final Project project
    final String tempDirectoryName

    BootckerComposePreparator(Project project, String tempDirectoryName) {
        this.project = project
        this.tempDirectoryName = tempDirectoryName
    }

/**
 *
 * @param files
 * @return values are absolute paths of resulting yml
 */
    Map<ComposeFile, String> prepare(Collection<ComposeFile> files) {
        def workDir = prepareWorkingDirectory()
        def ret = [:]
        files.forEach {
            it.applyCustomizer({ it == 'bootcker' }) {
                return extractProject(workDir, project)
            }
            it.applyCustomizer({ it.startsWith('bootcker:') }) {
                Project otherProject = findProject(project, it.image as String)
                return extractProject(workDir, otherProject)
            }
            def outFile = new File(workDir, it.originalFile.getName())
            it.writeToFile(outFile)
            ret << [(it): outFile.absolutePath]
        }
        return ret
    }

    protected LinkedHashMap<String, LinkedHashMap<String, String>> extractProject(File workDir, Project otherProject) {
        def serviceDir = new File(workDir, otherProject.name)
        serviceDir.mkdirs()

        def jar = getApplicationJar(otherProject)
        FileUtils.copyFile(jar, new File(serviceDir, jar.name))

        createEntrypointScript(serviceDir)
        createDockerFile(serviceDir, [application_jar: jar.name,
                                      version        : otherProject.version])
        //laaameee!!!
        def friendlyProjectName = otherProject.name.replaceAll("[^0-9a-zA-Z]", " ")
                .toLowerCase().trim().replaceAll("[^0-9a-zA-Z]", ".")
        def friendlyVersion = otherProject.version.toLowerCase()
        return [build: [context: "./${otherProject.name}".toString(), dockerfile: 'Dockerfile'],
                image: "bootcker_${friendlyProjectName}:${friendlyVersion}".toString()] as LinkedHashMap<String, LinkedHashMap<String, String>>
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
        def workDir = new File(project.buildDir, tempDirectoryName)
        if (workDir.exists()) {
            FileUtils.deleteDirectory(workDir)
        }
        workDir.mkdirs()
        return workDir
    }
}
