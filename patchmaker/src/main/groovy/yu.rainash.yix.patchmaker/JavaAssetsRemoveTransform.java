package yu.rainash.yix.patchmaker;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import yu.rainash.yix.patchmaker.utils.JarUtils;
import yu.rainash.yix.patchmaker.utils.TransformProxy;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class JavaAssetsRemoveTransform extends TransformProxy {

    private static final String OUTPUT_CONTENT_LOCATION_NAME = "resources";

    public JavaAssetsRemoveTransform(Transform realTransform) {
        super(realTransform);
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        File outJar = outputProvider.getContentLocation(OUTPUT_CONTENT_LOCATION_NAME, getOutputTypes(), getScopes(), Format.JAR);
        File outDir = outputProvider.getContentLocation(OUTPUT_CONTENT_LOCATION_NAME, getOutputTypes(), getScopes(), Format.DIRECTORY);
        if (outJar.exists()) {
            File unJarDir = new File(outJar.getParent(), "unjar");
            JarUtils.unJar(outJar, unJarDir);
            removeAssetsInDir(unJarDir);
            JarUtils.packJar(unJarDir, outJar, true);
        }
        if (outDir.exists()) {
            removeAssetsInDir(outDir);
        }
    }

    private void removeAssetsInDir(File dir) {
        Collection<File> allFiles = FileUtils.listFiles(dir, null, true);
        for (File file : allFiles) {
            if (!file.getName().endsWith(".class")) {
                file.delete();
            }
        }
    }

}
