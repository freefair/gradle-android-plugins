package io.freefair.gradle.plugins.android.maven;

import com.android.build.gradle.TestedExtension;
import io.freefair.gradle.plugins.android.AndroidProjectPlugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;
import org.gradle.jvm.tasks.Jar;

import static org.codehaus.groovy.runtime.StringGroovyMethods.capitalize;

public class AndroidSourcesJarPlugin extends AndroidProjectPlugin {

    private Task allSourcesJarTask;

    @Override
    public void apply(Project project) {
        super.apply(project);

        allSourcesJarTask = project.getTasks().create("sourcesJar", asjTask -> {
            asjTask.setDescription("Generate the sources jar for all variants");
            asjTask.setGroup("jar");
        });
    }

    @Override
    protected void withAndroid(TestedExtension extension) {
        super.withAndroid(extension);

        getAndroidVariants().all(variant -> {
            Jar sourcesJarTask = getProject().getTasks().create("sources" + capitalize((CharSequence) variant.getName()) + "Jar", Jar.class, jar -> {
                jar.setDescription("Generate the sources jar for the " + variant.getName() + " variant");
                jar.setGroup("jar");

                jar.setClassifier("sources");
                jar.setAppendix(variant.getName());
                jar.from(variant.getJavaCompiler().property("source"));
            });

            allSourcesJarTask.dependsOn(sourcesJarTask);

            if (publishVariant(variant)) {
                getProject().getArtifacts().add(Dependency.ARCHIVES_CONFIGURATION, sourcesJarTask);
            }
        });
    }
}
