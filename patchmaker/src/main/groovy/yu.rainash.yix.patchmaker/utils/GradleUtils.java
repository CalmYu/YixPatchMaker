package yu.rainash.yix.patchmaker.utils;

import com.android.build.api.transform.Transform;
import com.android.build.gradle.internal.pipeline.TransformTask;

import java.lang.reflect.Field;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class GradleUtils {

    /**
     * 替换transform实现
     * @param task
     * @param yourTransform
     */
    public static void replaceTransform(TransformTask task, Transform yourTransform) {
        try {
            Field TransformTask_transform = TransformTask.class.getDeclaredField("transform");
            TransformTask_transform.setAccessible(true);
            TransformTask_transform.set(task, yourTransform);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Transform getTransformFromTask(TransformTask task) {
        try {
            Field TransformTask_transform = TransformTask.class.getDeclaredField("transform");
            TransformTask_transform.setAccessible(true);
            return (Transform) TransformTask_transform.get(task);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
