package yu.rainash.yix.patchmaker.utils;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileTree;
import org.gradle.process.ExecSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class Utils {

    public static void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    public static void printAllFileName(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                printAllFileName(file);
            } else {
                System.out.println("file: " + file.getAbsolutePath());
            }
        }
    }

    public static void copyFile(File source, File dest) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(dest);
            byte[] buffer = new byte[4096 * 3];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void execCmd(Project project, final File cmdFile, final Object... args) {
        project.exec(new Action<ExecSpec>() {
            @Override
            public void execute(ExecSpec execSpec) {
                execSpec.executable(cmdFile);
                execSpec.args(args);
            }
        });
    }

    public static void printAllFileds(Class clz, Object instance) {
        Field[] fields = clz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                System.out.println(f.getName() + " ===> " + f.get(instance));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void unzip(Project project, final File zipFile, final File targetDir, final String... files) {
        final FileTree zipFiles = project.zipTree(zipFile);
        project.copy(new Action<CopySpec>() {
            @Override
            public void execute(CopySpec copySpec) {
                copySpec.from(zipFiles);
                copySpec.into(targetDir);
                for (String file : files) {
                    copySpec.include(file);
                }
            }
        });
    }

    public static int execJavaCmd(String cmd, File workingDir) {
        BufferedReader br = null;
        InputStreamReader ir = null;
        final BufferedReader er;
        try {
            Process jarProc = Runtime.getRuntime().exec(cmd, null, workingDir);
            ir = new InputStreamReader(jarProc.getInputStream());
            er = new BufferedReader(new InputStreamReader(jarProc.getErrorStream()));
            br = new BufferedReader(ir);
            String l;
            new Thread(){
                @Override
                public void run() {
                    try {
                        String el;
                        while ((el = er.readLine()) != null) {
//                            System.out.println(el);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (er != null) {
                            try {
                                er.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
            while ((l = br.readLine()) != null) {
//                System.out.println(l);
            }
            return jarProc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public static void printFileDir(File dir) {
        File[] fs = dir.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if (f.isDirectory()) {
                    System.out.println("  dir: " + f.getAbsolutePath());
                    printFileDir(f);
                } else {
                    System.out.println("  file: " + f.getAbsolutePath());
                }
            }
        }
    }

    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1, text.length()));
    }

    public static void main(String[] args) {
        File jarFile = new File("C:\\Users\\Administrator\\Desktop\\AndroidProjects\\lib-component\\appdemoplugin\\build\\classRemove\\7a13e6c69646ad05297d7e9efbaf3975.jar");
        File unJarDir = new File("C:\\Users\\Administrator\\Desktop\\AndroidProjects\\lib-component\\appdemoplugin\\build\\classRemove\\aa");
        unJarDir.mkdir();
        JarUtils.unJar(jarFile, unJarDir);
        Collection<File> files = FileUtils.listFiles(unJarDir, null, true);
        for (File f : files) {
            f.delete();
        }
        File newJarFile = new File("C:\\Users\\Administrator\\Desktop\\AndroidProjects\\lib-component\\appdemoplugin\\build\\classRemove\\bb.jar");
        JarUtils.packJar(unJarDir, newJarFile, true);
    }

}
