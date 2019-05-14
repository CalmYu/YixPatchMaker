package yu.rainash.yix.patchmaker.utils;

import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.Status;

import java.util.Set;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public abstract class JarInputProxy implements JarInput {

    private JarInput mImpl;

    public JarInputProxy(JarInput impl) {
        mImpl = impl;
    }

    @Override
    public Status getStatus() {
        return mImpl.getStatus();
    }

    @Override
    public String getName() {
        return mImpl.getName();
    }

    @Override
    public Set<ContentType> getContentTypes() {
        return mImpl.getContentTypes();
    }

    @Override
    public Set<? super Scope> getScopes() {
        return mImpl.getScopes();
    }
}
