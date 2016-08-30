package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.AdminUser;
import core.admin.domain.Menu;
import core.admin.service.MenuService;
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
@RequestMapping(value = "/json/mune")
public class MenuControllerJson {
    private static final Logger logger = LoggerFactory.getLogger(MenuControllerJson.class);

    @Autowired
    private MenuService menuService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit/", method = {RequestMethod.GET})
    @ResponseBody
    public Object edit(@RequestParam(value = "id", required = false) Long id, HttpServletRequest request, @RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if (id != null && id.longValue() > 0) {
                Menu menu = menuService.queryMenuById(id);
                return menu;
            }else {
                Page<Menu> page = menuService.queryMenuPage(currentPage, pageSize, search);
                List menuList=page.getResult();
                return menuList;
            }

//            if(sessionAdminUser!=null && sessionAdminUser.getUserType()!=null && 2!=sessionAdminUser.getUserType())
//            search.put("userId",sessionAdminUser.getId().intValue());
            //查询
//            Map<String, Object> search = new HashMap<String, Object>();


            //放入page对象。
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }
/*
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {

            if (id != null && id.longValue() > 0) {
                Menu menu = menuService.queryMenuById(id);
                view.addAttribute("menu", menu);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "menu-edit";
    }*/

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(HttpServletRequest request,Menu menu,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
//                menu.setUserId(sessionAdminUser.getId().intValue());
            }
            long rows = menuService.saveMenu(menu);
            view.addAttribute("menu", menu);

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

            long rows = menuService.deleteMenu(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }
}
