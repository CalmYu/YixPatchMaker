package yu.rainash.yix.patchmaker.utils;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;

import java.io.IOException;
import java.util.Set;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class TransformProxy extends Transform {

    private Transform realTransform;

    public TransformProxy(Transform realTransform) {
        this.realTransform = realTransform;
    }

    public Transform getRealTransform() {
        return this.realTransform;
    }

    @Override
    public String getName() {
        return realTransform.getName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return realTransform.getInputTypes();
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return realTransform.getScopes();
    }

    @Override
    public boolean isIncremental() {
        return realTransform.isIncremental();
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        this.realTransform.transform(transformInvocation);
    }

}
