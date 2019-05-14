package yu.rainash.yix.patchmaker;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class HotFixExtension {

    public static final String NAME = "Yix";

    public List<String> fixClasses = new ArrayList<>();

    public List<String> fixSos = new ArrayList<>();

    public String outputFileName = "patch.apk";

}
