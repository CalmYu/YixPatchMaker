package yu.rainash.yix.patchmaker.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class IoUtils {

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
