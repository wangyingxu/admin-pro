package core.admin.pro.json;

import com.google.gson.Gson;
import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.AdminUser;
import core.admin.domain.CarInfo;
import core.admin.service.CarInfoService;
import core.admin.util.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static core.admin.common.AdminConstants.SESSION_USER_KEY;

@RestController
@RequestMapping(value = "/json/admin/carInfo", method = {RequestMethod.GET, RequestMethod.POST})
public class CarInfoControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(CarInfoControllerJson.class);

    @Autowired
    private CarInfoService carInfoService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {

            if (id != null && id.longValue() > 0) {
                CarInfo carInfo = carInfoService.queryCarInfoById(id);
                view.addAttribute("carInfo", carInfo);
            }

            view.addAttribute("carType", AdminConstants.carType);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "carInfo-edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = carInfoService.deleteCarInfo(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(CarInfo carInfo, HttpServletRequest request) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){

                carInfo.setUserId(sessionAdminUser.getId());
                if(StringUtils.isNotEmpty(carInfo.getOftenProvCode()) && StringUtils.isNotEmpty(carInfo.getOftenCityCode()))
                {
                    if(!"北京".equals(carInfo.getOftenProvCode()) && !"市辖区".equals(carInfo.getOftenCityCode())){
                        StringBuffer sb = new StringBuffer(64);
                        sb.append(carInfo.getOftenProvCode()).append(",");
                        sb.append(carInfo.getOftenCityCode()).append(",");
                        sb.append(carInfo.getOftenAreaCode());
                        carInfo.setOftenArea(sb.toString());
                    }
                }


                long rows = carInfoService.saveCarInfo(carInfo);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(HttpServletRequest request,@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       Model view) {
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            String keyword = request.getParameter("keyword");
            if(!StringUtils.isEmpty(keyword)){
                search.put("carNo", keyword);
                search.put("owner", keyword);
                search.put("contact", keyword);
                view.addAttribute("keyword", keyword);
            }
            Page<CarInfo> page = carInfoService.queryCarInfoPage(currentPage, pageSize,search);
            //放入page对象。
            view.addAttribute("page", page);
            view.addAttribute("carType", AdminConstants.carType);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "carInfo-list";
    }


    @RequestMapping(value = "/editJson", method = {RequestMethod.PUT})
    @ResponseBody
    public String editJson(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        try {

            if (id != null && id.longValue() > 0) {
                CarInfo carInfo = carInfoService.queryCarInfoById(id);

                  if(carInfo!=null){
                    String typeStr =  AdminConstants.carType.get(carInfo.getCarType());
                      carInfo.setCarTypeStr(typeStr);
                  }
                Gson gson = new Gson();
                String jsonValue =  gson.toJson(carInfo);
                return jsonValue;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "{}";
        }
        return "{}";
    }

}
