package core.admin.pro.interceptor;

import core.admin.dao.UserMapper;
import core.admin.domain.AdminUser;
import core.admin.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author    : liuxianglong@shuzijiayuan.com
 * CreateTime:  15/12/12  21:10
 * <p/>
 * Version: 1.0
 * <p/>
 */
public class InitData implements ApplicationListener {

    @Autowired
    UserMapper userMapper;
    private static boolean isStart = false;
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (!isStart) {//这个可以解决项目启动加载两次的问题
            isStart= true;
            List<AdminUser> adminUserList = userMapper.findAdminUserList(1,0,1000);

            if(adminUserList!=null && adminUserList.size()>0)
            {
                Map<Integer,String> userMap = new HashMap<>();

                for(AdminUser user:adminUserList)
                {
                    userMap.put(user.getId().intValue(),user.getUsername());
                }

                UserUtils.getInstance().setUserMap(userMap);
            }

        }
    }
}
