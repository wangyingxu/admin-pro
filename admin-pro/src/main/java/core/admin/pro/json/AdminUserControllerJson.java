package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.domain.*;
import core.admin.page.PaginatedPage;
import core.admin.service.*;
import core.admin.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.admin.common.AdminConstants.SESSION_USER_KEY;
import static core.admin.common.AdminConstants.SESSION_USER_MENU_KEY;

/**
 * DATE:8/14/15 20:03
 * AUTHOR:wangzhen
 */

@RestController
@RequestMapping("admin")
public class AdminUserControllerJson {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserControllerJson.class);
    @Resource
    private UserService userService;

    @Autowired
    private BranchNetworkService branchNetworkService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    @ResponseBody
    public String index(ModelMap map,HttpServletRequest request) {
        AdminUser adminUser = SessionUtils.getSessionValue(request, AdminConstants.SESSION_USER_KEY);
        if (adminUser != null) {
            map.put("username", adminUser.getUsername());
        }
        return "index";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    //网点用户修改
    @RequestMapping(value = "/user/edit", method = {RequestMethod.GET} )
    @ResponseBody
    public String create(@RequestParam(value = "id", required = false) Long id, HttpServletRequest request) {
        try {
            List<Object> data=new ArrayList<Object>();
            if (id != null && id.longValue() > 0) {
                AdminUser user = userService.queryAdminUserById(id);
                if(user.getNetworkId()>0){
                    BranchNetwork branchNetwork = branchNetworkService.queryBranchNetworkById(Long.valueOf(user.getNetworkId()));
                    if(branchNetwork!=null){
                        Map<String, Object> search = new HashMap<String, Object>();
                        search.put("provCode",branchNetwork.getProvCode());
                        search.put("areaCode", branchNetwork.getAreaCode());
                        List<LogisticsInfo> logisticsInfoList = logisticsInfoService.searchLogisticsInfo(search);
                        if(logisticsInfoList!=null && logisticsInfoList.size()>0){

                            data.add(logisticsInfoList);
                        }
                    }

                }

                data.add(user);
            }

            List<BranchNetwork> branchNetworks = branchNetworkService.queryBranchNetwork(new HashMap<String, Object>());
            data.add(branchNetworks);


            List<Role> roleList = roleService.queryRole(new HashMap<String, Object>());
            data.add(roleList);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "user-create";
    }

    //新增
    @RequestMapping(value = "/user/create",method = RequestMethod.POST)
    @ResponseBody
    public String createUser(AdminUser user) {
        if( user.getId()!=null && user.getId()>0){


        }else {
            AdminUser adminUser = userService.findUserByMobile(user.getMobile());
            if(adminUser!=null)
                return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "手机号码已存在！");
        }
         userService.createUser(user);
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/user/findByMobile",method = RequestMethod.PUT)
    @ResponseBody
    public String findByMobile(String mobile) {

            AdminUser adminUser = userService.findUserByMobile(mobile);
            if(adminUser!=null)
                return "{c:1,m:\"手机号码已存在!\"}";

        return "{c:0,m:\"\"}";
    }

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(HttpServletRequest request,String username,String password,ModelMap map) {
        AdminUser loginUser = userService.login(request,username,password);

        if(loginUser != null && loginUser.getStatus() ==1 ){
            Role role = roleService.queryRoleById(loginUser.getRoleId().longValue());
            List<Menu> menuList = menuService.queryMenuByIds(role.getMenuList());
            SessionUtils.setSession(request, AdminConstants.SESSION_USER_MENU_KEY, menuList);
            String url = "redirect:/admin/park/list";
            if(menuList!=null && menuList.size()>0){
                url = "redirect:" + menuList.get(0).getUrl();
            }
            return url;
        }else if(loginUser==null)
        {
            map.addAttribute("msg", String.format(AdminConstants.WEB_IFRAME_ERROR_SCRIPT, "登录失败！用户名/密码错误！"));
          return  "login";
        }else if(loginUser.getStatus()==2)
        {
            map.addAttribute("msg", String.format(AdminConstants.WEB_IFRAME_ERROR_SCRIPT, "用户名已被管理员屏蔽！"));
            return  "login";
        }

        return null;
    }

    @RequestMapping(value = "logout",method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {

           SessionUtils.cleanSession(request,SESSION_USER_KEY);
            SessionUtils.cleanSession(request, SESSION_USER_MENU_KEY);
            return "redirect:/admin/login";
    }

    //网点用户的列表
    @RequestMapping(value = "/user/list",method = {RequestMethod.GET})
    @ResponseBody
    public Object adminUserList(Integer networkId ,Integer page) {
        PaginatedPage<AdminUser> webUserManagers=userService.findAdminUserList(networkId, page);
        List webUserManager=webUserManagers.getDatum();
         return webUserManager;
    }

    @RequestMapping(value = "/user/resetPassword",method = RequestMethod.GET)
    public String resetPassword(Long id,ModelMap map) {
        map.put("id",id);
        return "user-psw";
    }

    @RequestMapping(value = "/user/resetPassword",method = RequestMethod.POST)
    @ResponseBody
    public String resetPassword(Long id,String newPassword) {
         userService.resetPassword(id, newPassword);
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "密码修改成功！");
    }

    //屏蔽或恢复
    @RequestMapping(value = "/user/status",method = RequestMethod.POST)
    @ResponseBody
    public String status(Integer id,Integer status) {

         userService.updateStatus(id, status);

        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "修改状态成功！");
    }
}
