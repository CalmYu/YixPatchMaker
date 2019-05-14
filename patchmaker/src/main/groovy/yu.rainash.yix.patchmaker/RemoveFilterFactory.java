package yu.rainash.yix.patchmaker;

import java.util.List;
import java.util.Map;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class RemoveFilterFactory {

    public static RemoverFilter fromProtect(List<String> protectPackages) {
        return new RemoverFilter() {
            @Override
            public int filter(String className) {
                if (protectPackages == null) {
                    return REMOVE;
                }
                for (String protect : protectPackages) {
                    if (className.startsWith(protect)) {
                        return PROTECT;
                    }
                }
                return REMOVE;
            }
        };
    }

    public static RemoverFilter fromStrip(List<String> stripPackages) {
        return new RemoverFilter() {
            @Override
            public int filter(String className) {
                if (stripPackages == null) {
                    return REMOVE;
                }
                for (String strip : stripPackages) {
                    if (className.startsWith(strip)) {
                        return REMOVE;
                    }
                }
                return REMOVE;
            }
        };
    }

    public static RemoverFilter proguard(List<String> protectPackages, Map<String, String> proguardRawMap) {
        return new RemoverFilter() {
            @Override
            public int filter(String className) {
                if (protectPackages == null) {
                    return REMOVE;
                }
                for (String protect : protectPackages) {
                    String rawClassName = proguardRawMap.get(className);
                    if (rawClassName == null && proguardRawMap.size() == 0) {
                        rawClassName = className;
                    }
                    if (rawClassName != null && rawClassName.startsWith(protect)) {
                        return PROTECT;
                    }
                }
                return REMOVE;
            }
        };
    }

    public static RemoverFilter allKeep() {
        return new RemoverFilter() {
            @Override
            public int filter(String className) {
                return RemoverFilter.PROTECT;
            }
        };
    }

}
