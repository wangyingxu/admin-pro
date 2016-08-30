package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.AdminUser;
import core.admin.domain.Menu;
import core.admin.domain.Role;
import core.admin.service.MenuService;
import core.admin.service.RoleService;
import core.admin.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.admin.common.AdminConstants.SESSION_USER_KEY;

/**
 * Created by pop on 16/1/23.
 */
@RestController
@RequestMapping(value = "/json/role")
public class RoleControllerJson {
    private static final Logger logger = LoggerFactory.getLogger(RoleControllerJson.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object list(HttpServletRequest request, @RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
//            if(sessionAdminUser!=null && sessionAdminUser.getUserType()!=null && 2!=sessionAdminUser.getUserType())
//            search.put("userId",sessionAdminUser.getId().intValue());
            //查询
//            Map<String, Object> search = new HashMap<String, Object>();

            Page<Role> page = roleService.queryRolePage(currentPage, pageSize, search);
            //放入page对象。
            List roles=page.getResult();
            return roles;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try{

            if (id != null && id.longValue() > 0) {
                Role role = roleService.queryRoleById(id);
                view.addAttribute("role", role);
            }

            List<Menu> menuList = menuService.queryMenu(new HashMap<String, Object>());
            view.addAttribute("menuList",menuList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "role-edit";
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(HttpServletRequest request,Role role,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
//                menu.setUserId(sessionAdminUser.getId().intValue());
            }
            String[] menuIds = request.getParameterValues("menuId");
            String menus = "";
            if(menuIds!=null){
                for(int i=0; i<menuIds.length; i++){
                    menus += menuIds[i];
                    if(i<(menuIds.length-1)){
                        menus += ",";
                    }
                }
            }
            role.setMenus(menus);
            long rows = roleService.saveRole(role);
            view.addAttribute("menu", role);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = roleService.deleteRole(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }
}
