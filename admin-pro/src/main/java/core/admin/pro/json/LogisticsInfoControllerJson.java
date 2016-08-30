package core.admin.pro.json;

import com.google.gson.Gson;
import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.*;
import core.admin.service.BranchNetworkService;
import core.admin.service.LogisticsInfoService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.admin.common.AdminConstants.SESSION_USER_KEY;

@RestController
@RequestMapping(value = "/json/admin/logisticsInfo", method = {RequestMethod.GET, RequestMethod.POST})
public class LogisticsInfoControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsInfoControllerJson.class);

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private BranchNetworkService branchNetworkService;

    @Autowired
    private UserService userService;


    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(HttpServletRequest request,@RequestParam(value = "id", required = false, defaultValue = "0") Long id,@RequestParam(value = "mark", required = false, defaultValue = "0") int mark,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);

            //查询
            Map<String, Object> search = new HashMap<String, Object>();

            List<Park> parkList = parkService.queryPark(search);

            view.addAttribute("parkList",parkList);
            if (id != null && id > 0) {
                LogisticsInfo logisticsInfo = logisticsInfoService.queryLogisticsInfoById(id);
                view.addAttribute("logisticsInfo", logisticsInfo);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if(mark==0){
            return "logisticsInfo-edit";
        }else{
            return "logisticsInfo-view";
        }
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = logisticsInfoService.deleteLogisticsInfo(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(HttpServletRequest request,LogisticsInfo logisticsInfo,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
                logisticsInfo.setUserId(sessionAdminUser.getId().intValue());
            }
            logisticsInfo.setParkId(0);
            long rows = logisticsInfoService.saveLogisticsInfo(logisticsInfo);
            view.addAttribute("logisticsInfo", logisticsInfo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(HttpServletRequest request,@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       @RequestParam(value = "id", required = false) Long id,String keyword,
                       Model view) {
        try {


//            List<Park> parkList = parkService.queryPark(new HashMap<String, Object>());
//            if(parkList!=null && parkList.size()>0){
//                Map<Integer,String> parkMap = new HashMap<>();
//                for(Park park:parkList)
//                {
//                    parkMap.put(park.getId(),park.getName());
//                }
//                view.addAttribute("parkMap", parkMap);
//            }
            //查询
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

                if(!StringUtils.isEmpty(keyword)){
                    view.addAttribute("keyword", keyword);

                    search.put("name",keyword);
                }
                Page<LogisticsInfo> page = logisticsInfoService.queryLogisticsInfoPage(currentPage,pageSize,search);

                if(page.getResult()!=null && page.getResult().size()>0){
                    view.addAttribute("page", page);
                }
            }else{
                if(StringUtils.isNotEmpty(keyword)){
                    search.put("name",keyword);
                }
                Page<LogisticsInfo> page = logisticsInfoService.queryLogisticsInfoPage(currentPage, pageSize, search);
                //放入page对象。
                view.addAttribute("page", page);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "logisticsInfo-list";
    }

    @RequestMapping(value = "/editJson", method = {RequestMethod.PUT})
    @ResponseBody
    public String editJson(HttpServletRequest request,@RequestParam(value = "id", required = false, defaultValue = "0") Long id
                       ) {
        try {

            if (id != null && id.longValue() > 0) {
                LogisticsInfo logisticsInfo = logisticsInfoService.queryLogisticsInfoById(id);
                if(logisticsInfo!=null){

                    Park park = parkService.queryParkById(Long.valueOf(logisticsInfo.getParkId()));
                    if(park!=null){
                        logisticsInfo.setProvCode(park.getProvCode());
                        logisticsInfo.setCityCode(park.getCityCode());
                        logisticsInfo.setAreaCode(park.getAreaCode());

                    }
                }
                Gson gson = new Gson();
                String jsonValue =  gson.toJson(logisticsInfo);
                return jsonValue;
            }
            return "{}";

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "{}";
    }

    @RequestMapping(value = "/findInfo", method = {RequestMethod.PUT})
    @ResponseBody
    public String findInfo(HttpServletRequest request,@RequestParam(value = "name", required = true, defaultValue = "0") String name
    ) {
        try {

            if (StringUtils.isNotEmpty(name)) {
                Map<String,Object> pMap = new HashMap<>();
                pMap.put("name",name);
                List<LogisticsInfo> logisticsInfs = logisticsInfoService.queryLogisticsInfo(pMap);
                if(logisticsInfs!=null && logisticsInfs.size()>0){

                    return "{\"c\":1}";
                }
            }
            return "{\"c\":0}";

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "{\"c\":0}";
    }

    @RequestMapping(value = "/listSearch", method = {RequestMethod.PUT})
    @ResponseBody
    public String listSearch(String prov,String areaCode
    ) {
        try {
            Gson gson = new Gson();
            if(StringUtils.isNotEmpty(prov)){

                /**
                 * 当前用户所在地区的所有物流公司列表
                 *
                 */
                Map<String, Object> search = new HashMap<String, Object>();
                search.put("provCode",prov);
                search.put("areaCode", areaCode);

                List<LogisticsInfo> logisticsInfoList = logisticsInfoService.searchLogisticsInfo(search);

                    String jsonValue = gson.toJson(logisticsInfoList);
                    return jsonValue;

            }

            return "{}";

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "{}";
    }

    @RequestMapping(value = "/listSearchByNetWorkId", method = {RequestMethod.PUT})
    @ResponseBody
    public String listSearchByNetWorkId(Integer netId
    ) {
        try {
            Gson gson = new Gson();
            if(netId>0){

                BranchNetwork branchNetwork = branchNetworkService.queryBranchNetworkById(Long.valueOf(netId));

                if(branchNetwork!=null){

                    /**
                     * 当前用户所在地区的所有物流公司列表
                     *
                     */
                    Map<String, Object> search = new HashMap<String, Object>();
                    search.put("provCode",branchNetwork.getProvCode());
                    search.put("areaCode", branchNetwork.getAreaCode());
                        List<LogisticsInfo> logisticsInfoList = logisticsInfoService.searchLogisticsInfo(search);
                        String jsonValue =  gson.toJson(logisticsInfoList);
                        return jsonValue;
                }


            }

            return "{}";

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "{}";
    }


    @RequestMapping(value = "/saveJson", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String saveJson(HttpServletRequest request,String json,
                       Model view) {
        try {

            LogisticsInfo info = new LogisticsInfo();
            info.setParkId(300);
            info.setName("天天快递888");
            info.setContact("13621106126");
            info.setLonLatMsg("上地七街");
            info.setAddress("29号");
            info.setLat(34.22234f);
            info.setLon(45.344f);
            info.setCityCode("菏泽市");
            info.setProvCode("山东省");
            info.setAreaCode("曹县");
            info.setCreateTime(System.currentTimeMillis());
            List<LogisticsLine> lines = new ArrayList<>();

            LogisticsLine line = new LogisticsLine();
            line.setAreaCode("长江区");
            line.setCityCode("济南市");
            line.setProvCode("山东省");
            line.setAddress("济南见那");
            line.setArrivalAddress("dddddd");
            line.setArrivalTime(60);
            line.setCharges("222");
            line.setChargesType(1);

            lines.add(line);
            info.setLineList(lines);
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
                info.setUserId(sessionAdminUser.getId().intValue());
            }
            long rows = logisticsInfoService.saveLogisticsInfo(info);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }


}
