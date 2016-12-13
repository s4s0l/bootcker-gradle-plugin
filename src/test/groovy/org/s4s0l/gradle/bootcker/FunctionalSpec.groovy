package org.s4s0l.gradle.bootcker

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestName
import org.junit.rules.TestRule
import spock.lang.Specification

abstract class FunctionalSpec extends Specification  {

    TestName name = new TestName();
    TempFile projectDirectory = new TempFile(name);
    @Rule
    public TestRule chain= RuleChain
            .outerRule(name)
            .around(projectDirectory);

    File getBuildFile() {
        return makeFile('build.gradle')
    }

    File makeFile(String path) {
        def f = file(path)
        if (!f.exists()) {
            f.parentFile.mkdirs()
            f.createNewFile()
        }
        return f
    }

    File file(String path) {
        def file = new File(projectDirectory.root, path)
        assert file.parentFile.mkdirs() || file.parentFile.exists()
        return file
    }

    GradleRunner runner(String gradleVersion, String... args) {
        return GradleRunner.create()
                .withProjectDir(projectDirectory.root)
                .withDebug(true) // always run inline to save memory, especially on CI
                .forwardOutput()
                .withTestKitDir(getTestKitDir())
                .withArguments(args.toList())
                .withGradleVersion(gradleVersion ?: GradleVersion.current().version)
    }

    BuildResult runWithVersion(String gradleVersion, String... args) {
        runner(gradleVersion, args).build()
    }

    BuildResult run(String... args) {
        runner(null, args).build()
    }

    private static File getTestKitDir() {
        def gradleUserHome = System.getenv('GRADLE_USER_HOME')
        if (!gradleUserHome) {
            gradleUserHome = new File(System.getProperty('user.home'), '.gradle').absolutePath
        }
        return new File(gradleUserHome, 'testkit')
    }

    File getLocalRepo() {
        new File('./build/localrepo')
    }
}






class TempFile extends ExternalResource {
    private final TestName name;
    private final File parentFolder;
    File root;

    TempFile(TestName name) {
        this(new File("./build"), name);
    }

    TempFile(File parentFolder, TestName name) {
        this.parentFolder = parentFolder;
        this.name = name
    }

    @Override
    protected void before() throws Throwable {
        root = new File(parentFolder, "test" + name.methodName)
        if(root.exists()){
            delete()
        }
        root.mkdirs()

    }

    void delete() {
        if (root != null) {
            recursiveDelete(root);
        }
    }

    private void recursiveDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                recursiveDelete(each);
            }
        }
        file.delete();
    }
}