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
import java.util.List;

import yu.rainash.yix.patchmaker.utils.TransformProxy;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class SoRemoveTransform extends TransformProxy {

    private static final String OUTPUT_CONTENT_LOCATION_NAME = "resources";

    private List<String> protecedSos;

    public SoRemoveTransform(Transform realTransform, List<String> protectedSos) {
        super(realTransform);
        this.protecedSos = protectedSos;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        File outDir = outputProvider.getContentLocation(OUTPUT_CONTENT_LOCATION_NAME, getOutputTypes(), getScopes(), Format.DIRECTORY);
        System.out.println("###  so dir" + outDir);
        if (outDir.exists()) {
            Collection<File> soFiles = FileUtils.listFiles(outDir, new String[]{"so"}, true);
            for (File soFile : soFiles) {
                boolean shouldDelete = true;
                if (protecedSos != null && protecedSos.size() > 0) {
                    for (String so : protecedSos) {
                        if (soFile.getName().equals(so)) {
                            shouldDelete = false;
                            break;
                        }
                    }
                }
                if (shouldDelete) {
                    soFile.delete();
                }
            }
        }
    }
}
