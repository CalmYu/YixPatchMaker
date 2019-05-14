package yu.rainash.yix.patchmaker.utils;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class BasePlugin implements Plugin<Project> {

    private Project mProject;

    @Override
    public void apply(Project project) {
        mProject = project;
        project.afterEvaluate(project1 -> afterEvaluate(project1));
    }

    public void afterEvaluate(Project project) {

    }

    public Project getProject() {
        return mProject;
    }
}
