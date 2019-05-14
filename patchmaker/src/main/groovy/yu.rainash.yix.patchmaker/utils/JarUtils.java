package yu.rainash.yix.patchmaker.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class JarUtils {

    /**
     * unzip a jar file
     * @param jarFile
     * @param unJarDir
     */
    public static void unJar(File jarFile, File unJarDir) {
        unJarDir.mkdirs();
        String unZipCmd = String.format("jar xvf %s", jarFile.getAbsolutePath());
        Utils.execJavaCmd(unZipCmd, unJarDir);
    }

    /**
     * directory --> jar
     * @param dir
     * @param outputJar
     * @param removeDir
     */
    public static void packJar(File dir, File outputJar, boolean removeDir) {
        StringBuilder zipCmdBuilder = new StringBuilder();
        File[] packages = dir.listFiles();
        if (packages == null) {
            Utils.deleteDir(dir);
        }
        String tempJarName = DigestUtils.md5Hex(outputJar.getAbsolutePath()) + ".jar";
        zipCmdBuilder.append("jar cvfM " + tempJarName);
        for (File pkg : packages) {
            if (pkg.isFile()) continue;
            String path = pkg.getAbsolutePath().replace(dir.getAbsolutePath(), "");
            if (path.startsWith(File.separator)) {
                path = path.substring(1);
            }
            path += File.separator;
            zipCmdBuilder.append(String.format(" %s", path));
        }
        Utils.execJavaCmd(zipCmdBuilder.toString(), dir);
        File tempJar = new File(dir, tempJarName);
        try {
            FileUtils.copyFile(tempJar, outputJar); // renameTo is not working on windows
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (removeDir) {
            Utils.deleteDir(dir);
        }
    }



}
