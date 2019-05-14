package yu.rainash.yix.patchmaker;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class AssetsRemover {

    public static void removeAssets(File dir) {
        Collection<File> allFiles = FileUtils.listFiles(dir, null, true);
        for (File file : allFiles) {
            file.delete();
        }
    }

}
