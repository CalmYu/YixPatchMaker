package yu.rainash.yix.patchmaker.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class MappingReader {

    private File mMappingFile;

    public MappingReader(File mappingFile) {
        mMappingFile = mappingFile;
    }

    private Map<String, String> rawProguardMap = new HashMap<>();
    private Map<String, String> proguardRawMap = new HashMap<>();

    public void read() {
        System.out.println("read mappingFile: " + mMappingFile.getAbsolutePath());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mMappingFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(" ")) {
                    // class
                    String[] strs = line.split(SPLIT);
                    proguardRawMap.put(strs[1].trim().replace(":", ""), strs[0].trim());
                    rawProguardMap.put(strs[0].trim(), strs[1].trim().replace(":", ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getRawProguardMap() {
        return rawProguardMap;
    }

    public Map<String, String> getProguardRawMap() {
        return proguardRawMap;
    }

    private static final String SPLIT = " -> ";

}
