package yu.rainash.yix.patchmaker;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

import yu.rainash.yix.patchmaker.utils.MappingReader;
import yu.rainash.yix.patchmaker.utils.TransformProxy;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class ProxyProguardTransform extends TransformProxy {

    private static final String OUTPUT_CONTENT_LOCATION_NAME = "combined_res_and_classes";

    private List<String> protectPackages;

    private File mappingFile;

    public ProxyProguardTransform(Transform realTransform, List<String> protectPackages, File mappingFile) {
        super(realTransform);
        this.protectPackages = protectPackages;
        this.mappingFile = mappingFile;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        File outJar = outputProvider.getContentLocation(OUTPUT_CONTENT_LOCATION_NAME, getOutputTypes(), getScopes(), Format.JAR);
        if (protectPackages != null && protectPackages.size() > 0) {
            if (mappingFile != null) {
                MappingReader reader = new MappingReader(mappingFile);
                reader.read();
                ClassRemover remover = new ClassRemover(RemoveFilterFactory.proguard(protectPackages, reader.getProguardRawMap()));
                remover.processJar(outJar);
            } else {
                ClassRemover remover = new ClassRemover(RemoveFilterFactory.fromProtect(protectPackages));
                remover.processJar(outJar);
            }
        }

    }
}
