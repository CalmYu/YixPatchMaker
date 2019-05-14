package yu.rainash.yix.patchmaker;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApkVariantOutput;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.android.build.gradle.internal.pipeline.TransformTask;
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask;
import com.android.build.gradle.tasks.MergeSourceSetFolders;
import com.android.build.gradle.tasks.ProcessAndroidResources;
import com.android.builder.model.BuildType;

import org.antlr.v4.misc.Utils;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yu.rainash.yix.patchmaker.utils.BasePlugin;
import yu.rainash.yix.patchmaker.utils.GradleUtils;
import yu.rainash.yix.patchmaker.utils.ReflectUtils;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class PatchMaker extends BasePlugin {

    private static final String EXTENSION_ANDROID = "android";

    private static final String RESOURCE_KEEP_ALL_RULES = "keep-all-rules.pro";

    private Map<String, Boolean> realMinifyEnabledMap = new HashMap<>();

    @Override
    public void apply(Project project) {
        super.apply(project);

        project.getExtensions().create(HotFixExtension.NAME, HotFixExtension.class);

        final AppExtension appExtension = (AppExtension) project.getExtensions().getByName(EXTENSION_ANDROID);
        // 强制混淆走class处理
        for (com.android.build.gradle.internal.dsl.BuildType bt : appExtension.getBuildTypes()) {
            realMinifyEnabledMap.put(bt.getName(), bt.isMinifyEnabled());
            bt.setMinifyEnabled(true);
        }

    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        makePatch(project);

    }

    private void makePatch(Project project) {
        HotFixExtension config = (HotFixExtension) project.getExtensions().getByName(HotFixExtension.NAME);
        final List<String> fixClasses = config.fixClasses;
        final List<String> fixSos = config.fixSos;

        if (fixClasses == null || fixClasses.size() == 0) {
            throw new RuntimeException("请指定fixClasses再打patch包！");
        }

        final AppExtension appExtension = (AppExtension) project.getExtensions().getByName(EXTENSION_ANDROID);
        DomainObjectSet<ApplicationVariant> variants = appExtension.getApplicationVariants();

        File keepAllRules = copyKeepAllClassRule(project);
        for (com.android.build.gradle.internal.dsl.BuildType bt : appExtension.getBuildTypes()) {
            System.out.println("### BuildType: " + bt.getName() + " minifyEnabled: " + realMinifyEnabledMap.get(bt.getName()));
            if (!realMinifyEnabledMap.get(bt.getName())) {
                // 不混淆就keep all
                bt.getProguardFiles().add(keepAllRules);
            }
        }

        for (final ApplicationVariant variant : variants) {
            String typeStrCapitalize = Utils.capitalize(variant.getName());
            BuildType buildType = variant.getBuildType();

            // 产物输出名字配置
            if (config.outputFileName != null && !"".equals(config.outputFileName)) {
                for (BaseVariantOutput output : variant.getOutputs()) {
                    if (output instanceof ApkVariantOutput) {
                        ((ApkVariantOutput) output).setOutputFileName(config.outputFileName);
                    }
                }
            }

            // 移除class proguard is open done
            final TransformTask proGuardTask = (TransformTask) project.getTasks().findByName(String.format("transformClassesAndResourcesWithProguardFor%s", typeStrCapitalize));
            GradleUtils.replaceTransform(proGuardTask, new ProxyProguardTransform(GradleUtils.getTransformFromTask(proGuardTask), fixClasses, variant.getMappingFile()));


            // 移除jar包中的资源，百度sdk的jar包中包含资源 done
            final TransformTask mergeJavaTask = (TransformTask) project.getTasks().findByName(String.format("transformResourcesWithMergeJavaResFor%s", typeStrCapitalize));
            GradleUtils.replaceTransform(mergeJavaTask, new JavaAssetsRemoveTransform(GradleUtils.getTransformFromTask(mergeJavaTask)));

            // 移除lib库中的so文件 done
            final TransformTask mergeJniTask = (TransformTask) project.getTasks().findByName(String.format("transformNativeLibsWithMergeJniLibsFor%s", typeStrCapitalize));
            GradleUtils.replaceTransform(mergeJniTask, new SoRemoveTransform(GradleUtils.getTransformFromTask(mergeJniTask), fixSos));

            // 移除lib库中的assets文件 done
            MergeSourceSetFolders mergeAssets = (MergeSourceSetFolders) project.getTasks().getByName(String.format("merge%sAssets", typeStrCapitalize));
            mergeAssets.doLast(task -> {
                File mergeAssetsFolder = mergeAssets.getOutputDir();
                AssetsRemover.removeAssets(mergeAssetsFolder);
            });

            // 替换空的ap文件 done
            final ProcessAndroidResources processResourcesTask
                    = (ProcessAndroidResources) project.getTasks().getByName(String.format("process%sResources", typeStrCapitalize));
            processResourcesTask.doLast(task -> {
                if (processResourcesTask instanceof LinkApplicationAndroidResourcesTask) {
                    try {
                        File packageOutputDir = (File) ReflectUtils.invokeMethod(LinkApplicationAndroidResourcesTask.class, processResourcesTask, "getResPackageOutputFolder");
                        System.out.println("### PackageOutputFolder: " + packageOutputDir);
                        Collection<File> files = FileUtils.listFiles(packageOutputDir, new String[]{"ap_"}, true);
                        for (File apFile : files) {
                            ResourcesRemover.removeResources(apFile);
                        }
                    } catch (ReflectUtils.MethodInvokeException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private File copyKeepAllClassRule(Project project) {
        InputStream ins = ResourcesRemover.class.getClassLoader().getResourceAsStream(RESOURCE_KEEP_ALL_RULES);
        File buildDir = project.getBuildDir();
        if (!buildDir.exists()) {
            buildDir.mkdirs();
        }
        File targetFile = new File(buildDir, RESOURCE_KEEP_ALL_RULES);

        byte[] buffer = new byte[4096];
        int len;
        try {
            OutputStream out = new FileOutputStream(targetFile);
            while ((len = ins.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            ins.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

}
