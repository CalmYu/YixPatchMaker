package yu.rainash.yix.patchmaker;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public interface RemoverFilter {

    int PROTECT = 1;

    int REMOVE = 2;

    int filter(String className);

}