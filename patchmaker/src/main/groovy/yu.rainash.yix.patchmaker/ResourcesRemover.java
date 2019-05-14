package yu.rainash.yix.patchmaker;

import yu.rainash.yix.patchmaker.utils.IoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class ResourcesRemover {

    public static void removeResources(File resourcesAp) {
        InputStream ins = ResourcesRemover.class.getClassLoader().getResourceAsStream("resources-debug.ap_");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resourcesAp);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = ins.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(ins);
            IoUtils.closeQuietly(fos);
        }
    }

}
