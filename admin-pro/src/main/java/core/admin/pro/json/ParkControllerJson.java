package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.AdminUser;
import core.admin.domain.BranchNetwork;
import core.admin.domain.Park;
import core.admin.service.BranchNetworkService;
import core.admin.service.ParkService;
import core.admin.service.UserService;
import core.admin.util.SessionUtils;
import org.apache.commons.lang3.StringUtils;
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

@RestController
@RequestMapping(value = "/json/admin/park", method = {RequestMethod.GET, RequestMethod.POST})
public class ParkControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(ParkControllerJson.class);

    @Autowired
    private ParkService parkService;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchNetworkService branchNetworkService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {

            if (id != null && id.longValue() > 0) {
                Park park = parkService.queryParkById(id);
                view.addAttribute("park", park);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "park-edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = parkService.deletePark(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(HttpServletRequest request,Park park,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
                park.setUserId(sessionAdminUser.getId().intValue());
            }
            long rows = parkService.savePark(park);
            view.addAttribute("park", park);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(HttpServletRequest request,@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if(sessionAdminUser!=null && sessionAdminUser.getUserType()!=null && 2!=sessionAdminUser.getUserType())
            {
                BranchNetwork branchNetwork = branchNetworkService.queryBranchNetworkById(Long.valueOf(sessionAdminUser.getNetworkId()));
               if(branchNetwork!=null){

                   search.put("provCode",branchNetwork.getProvCode());
                   if(!"市辖区".equals(branchNetwork.getCityCode())){
                       search.put("cityCode",branchNetwork.getCityCode());
                       search.put("areaCode",branchNetwork.getAreaCode());
                   }else{
                       search.put("areaCode",branchNetwork.getAreaCode());
                   }

               }

            }

            String keyword = request.getParameter("keyword");
            if(!StringUtils.isEmpty(keyword)){
                search.put("name", keyword);
                view.addAttribute("keyword", keyword);
            }

            Page<Park> page = parkService.queryParkPage(currentPage, pageSize, search);
            //放入page对象。
            view.addAttribute("page", page);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "park-list";
    }

    @RequestMapping(value = "/view", method = {RequestMethod.GET, RequestMethod.POST})
    public String listView(@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       Model view) {
        String msg ="<p id='popP[id]' class='popText'>地址：[address]</br>电话:[mobile]</br></p>";

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            Page<Park> page = parkService.queryParkPage(currentPage, pageSize,search);
          List<Park> parkList= page.getResult();
            for (Park park:parkList){

                String reMsg = msg.replace("[id]",park.getId()+"").replace("[address]", park.getLonLatMsg()).replace("[mobile]",park.getContact());
                sb.append("[");
                sb.append(park.getLon());
                sb.append(",");
                sb.append(park.getLat());
                sb.append(",\"");
                sb.append(reMsg);
                sb.append("\",\"");
                sb.append(park.getName());
                sb.append("\"],");

            }
            sb.append("]");
            //放入page对象。
            view.addAttribute("msg", sb.toString());
            System.out.println("msg=="+sb.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "park-view";
    }

}
