package core.admin.pro.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Author    : liuxianglong
 * CreateTime:  15/12/12  20:58
 * <p/>
 * Version: 1.0
 * <p/>
 */
public class UserUtils {

   static Map<Integer,String> userMap = new HashMap<>();
    private UserUtils() {}
    private static core.admin.util.UserUtils single=null;
    //静态工厂方法
    public static core.admin.util.UserUtils getInstance() {
        if (single == null) {
            single = new core.admin.util.UserUtils();
        }
        return single;
    }
public void setUserMap(Map userP){
    userMap = userP;
}
    public String getUser(Integer id) {

         if(userMap!=null && userMap.size()>0){

             return userMap.get(id);
         }

        return null;
    }
}
