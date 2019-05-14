package yu.rainash.yix.patchmaker.utils;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Status;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public abstract class DirectoryInputProxy implements DirectoryInput {

    private DirectoryInput mImpl;

    public DirectoryInputProxy(DirectoryInput impl) {
        mImpl = impl;
    }

    @Override
    public Map<File, Status> getChangedFiles() {
        return mImpl.getChangedFiles();
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
