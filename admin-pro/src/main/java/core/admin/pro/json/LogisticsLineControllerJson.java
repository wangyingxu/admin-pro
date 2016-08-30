package core.admin.pro.json;

import com.google.gson.Gson;
import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.*;
import core.admin.service.BranchNetworkService;
import core.admin.service.LogisticsInfoService;
import core.admin.service.LogisticsLineService;
import core.admin.service.ParkService;
import core.admin.util.SearchMapUtils;
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
@RequestMapping(value = "/json/admin/logisticsLine", method = {RequestMethod.GET, RequestMethod.POST})
public class LogisticsLineControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsLineControllerJson.class);

    @Autowired
    private LogisticsLineService logisticsLineService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private BranchNetworkService branchNetworkService;



    private static Integer pageSize = 50;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(HttpServletRequest request,@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {

            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            //查询
            Map<String, Object> search = new HashMap<String, Object>();

//
//            if(sessionAdminUser!=null && sessionAdminUser.getUserType()!=null && 2!=sessionAdminUser.getUserType()) {
//
//                BranchNetwork branchNetwork = branchNetworkService.queryBranchNetworkById(Long.valueOf(sessionAdminUser.getNetworkId()));
//                if (branchNetwork != null) {
//
//                    search.put("provCode", branchNetwork.getProvCode());
//                    if (!"市辖区".equals(branchNetwork.getCityCode())) {
//                        search.put("cityCode", branchNetwork.getCityCode());
//                        search.put("areaCode", branchNetwork.getAreaCode());
//                    } else {
//                        search.put("areaCode", branchNetwork.getAreaCode());
//                    }
//
//                }
//
//                Page<Park> list = parkService.queryParkPage(1, 100, search);
//                if (list.getResult() != null && list.getResult().size() > 0) {
//                    List<LogisticsInfo> logisticsInfoList = logisticsInfoService.queryLogisticsInfo(list.getResult());
//                    view.addAttribute("logisticsInfoList", logisticsInfoList);
//                }
//            }else{
                List<LogisticsInfo> logisticsInfoList = logisticsInfoService.queryLogisticsInfo(search);
                view.addAttribute("logisticsInfoList", logisticsInfoList);
//            }

            if (id != null && id.longValue() > 0) {
                LogisticsLine logisticsLine = logisticsLineService.queryLogisticsLineById(id);
                view.addAttribute("logisticsLine", logisticsLine);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "logisticsLine-edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = logisticsLineService.deleteLogisticsLine(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(HttpServletRequest request, LogisticsLine logisticsLine, @RequestParam("infoIds") List<String>infoIds,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
                logisticsLine.setUserId(sessionAdminUser.getId().intValue());
            }
            long rows = logisticsLineService.saveLogisticsLine(logisticsLine,infoIds);
            view.addAttribute("logisticsLine", logisticsLine);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(HttpServletRequest request,@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       @RequestParam(value = "id", required = false) Long id,String fromCode,String toCode,
                       Model view) {
        try {

            List<LogisticsInfo> logisticsInfoList = logisticsInfoService.queryLogisticsInfo(new HashMap<String, Object>());
            if(logisticsInfoList!=null && logisticsInfoList.size()>0){
                Map<Integer,String> infoMap = new HashMap<>();
                for(LogisticsInfo info:logisticsInfoList)
                {
                    infoMap.put(info.getId(),info.getName());
                }
                view.addAttribute("infoMap", infoMap);
            }
            List<Park> parkList = parkService.queryPark(new HashMap<String, Object>());
            if(parkList!=null && parkList.size()>0){
                Map<Integer,String> parkMap = new HashMap<>();
                for(Park info:parkList)
                {
                    parkMap.put(info.getId(),info.getName());
                }
                view.addAttribute("parkMap", parkMap);

            }

            /**
             * 线路查询 从哪 到哪
             */
            if(StringUtils.isNotEmpty(fromCode) && StringUtils.isNotEmpty(toCode)){

                Map<String,Object> fromSearchMap = SearchMapUtils.createSearchMap(fromCode);
//                Map<String,Object> toSearchMap = SearchMapUtils.createSearchMap(toCode);

//                Page<Park> fromParkList = parkService.queryParkPage(1, 1000, fromSearchMap);
//
//                Page<Park> toParkList = parkService.queryParkPage(1, 1000, toSearchMap);

                List<LogisticsInfo> fromInfoList = logisticsInfoService.queryLogisticsInfo(fromSearchMap);
//                List<LogisticsInfo> toInfoList = logisticsInfoService.queryLogisticsInfo(toSearchMap);

                if(fromInfoList!=null && fromInfoList.size()>0){

                    Page<LogisticsLine> page = new Page<>(1);
                    List<LogisticsLine> logisticsLines = logisticsLineService.queryLogisticsLine(fromInfoList,toCode);

                    page.setResult(logisticsLines);
                    view.addAttribute("page", page);

                }


            }else{

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

                    List<LogisticsInfo> list = logisticsInfoService.queryLogisticsInfo(search);
                    if(list!=null && list.size()>0){
                        Page<LogisticsLine> page= new Page<>(1,100);

                        List<LogisticsLine> logisticsLineList = logisticsLineService.queryLogisticsLine(list,null);
                        page.setResult(logisticsLineList);
                        if(logisticsLineList!=null && logisticsLineList.size()>0){
                            page.setTotalCount(logisticsLineList.size());
                        }else {

                            page.setTotalCount(0);
                        }
                        view.addAttribute("page", page);
                    }

                }
                else{

                    Page<LogisticsLine> page = logisticsLineService.queryLogisticsLinePage(currentPage, pageSize,search);
                    //放入page对象。
                    view.addAttribute("page", page);
                    view.addAttribute("id", id);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "logisticsLine-list";
    }


    @ResponseBody
    @RequestMapping(value = "/parkList", method = {RequestMethod.GET, RequestMethod.POST})
    public String parkList( String provCode,String cityCode,String areaCode) {
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            search.put("provCode",provCode);
//            search.put("cityCode",cityCode);
            search.put("areaCode", areaCode);

            Page<Park> page = parkService.queryParkPage(1, 50, search);
            if(page!=null && page.getResult()!=null && page.getResult().size()>0)
            {

                Gson gson = new Gson();
                String jsonValue =  gson.toJson(page.getResult());


                return jsonValue;
            }else{
                return "{}";
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "{}";
    }


    @ResponseBody
    @RequestMapping(value = "/logisticsInfoList", method = {RequestMethod.GET, RequestMethod.POST})
    public String logisticsInfoList( Integer parkId) {
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            search.put("parkId", parkId);

            Page<LogisticsInfo> page = logisticsInfoService.queryLogisticsInfoPage(1, 50, search);
            if(page!=null && page.getResult()!=null && page.getResult().size()>0)
            {

                Gson gson = new Gson();
                String jsonValue =  gson.toJson(page.getResult());


                return jsonValue;
            }else{
                return "{}";
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "{}";
    }

}
