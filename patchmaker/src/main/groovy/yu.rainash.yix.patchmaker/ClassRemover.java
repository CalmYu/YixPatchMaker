package yu.rainash.yix.patchmaker;


import yu.rainash.yix.patchmaker.utils.JarUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class ClassRemover {

    private List<RemoverFilter> mFilters = new ArrayList<>();

    public ClassRemover(RemoverFilter filter) {
        if (filter != null) {
            mFilters.add(filter);
        }
    }

    public ClassRemover(List<RemoverFilter> filters) {
        if (filters != null) {
            mFilters = filters;
        }
    }

    public boolean processJar(File jarFile) {
        // unzip
        File unJarDir = new File(jarFile.getParentFile(), DigestUtils.md5Hex(jarFile.getAbsolutePath()));
        JarUtils.unJar(jarFile, unJarDir);

        // remove classes
        processDirectory(unJarDir);

        cleanEmptyClassDirectory(unJarDir);

        if (!unJarDir.exists()) {
            return false;
        }

        // re-zip
        JarUtils.packJar(unJarDir, jarFile, true);
        return true;
    }

    private static void cleanEmptyClassDirectory(File dir) {
        for (int i = 0; i < 20; i++) {
            removeEmptyClassDirectory(dir);
        }
    }

    private static void removeEmptyClassDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                dir.delete();
            } else {
                for (File file : files) {
                    if (!file.getName().endsWith(".class")) {
                        file.delete();
                    }
                    removeEmptyClassDirectory(file);
                }
            }
        }
    }

    public void processDirectory(File directory) {
        Collection<File> classFiles = FileUtils.listFiles(directory, new String[]{"class"}, true);
        for (File classFile : classFiles) {
            String className = classFile.getAbsolutePath().replace(directory.getAbsolutePath(), "");
            className = className.replace(File.separator, ".");
            if (className.startsWith(".")) {
                className = className.substring(1);
            }
            className = className.replace(".class", "");

            for (RemoverFilter filter : mFilters) {
                int result = filter.filter(className);
                if (result == RemoverFilter.PROTECT) {
                    break;
                } else  {
                    boolean r = classFile.delete();
                    break;
                }
            }
        }
    }

}
