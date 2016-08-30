package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.ChargeStandard;
import core.admin.service.ChargeStandardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/json/admin/chargeStandard", method = {RequestMethod.GET, RequestMethod.POST})
public class ChargeStandardControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(ChargeStandardControllerJson.class);

    @Autowired
    private ChargeStandardService chargeStandardService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,int lineId,
                       Model view) {
        try {
            ChargeStandard chargeStandard=null;
            if (id != null && id.longValue() > 0) {
                 chargeStandard = chargeStandardService.queryChargeStandardById(id);


            }else{

                  chargeStandard = new ChargeStandard();
                    chargeStandard.setLineId(lineId);

            }

            view.addAttribute("chargeStandard", chargeStandard);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "chargeStandard-edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = chargeStandardService.deleteChargeStandard(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(ChargeStandard chargeStandard,
                       Model view) {
        try {

            long rows = chargeStandardService.saveChargeStandard(chargeStandard);
            view.addAttribute("chargeStandard", chargeStandard);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       @RequestParam(value = "lineId", required = true) int lineId,
                       Model view) {
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if (lineId!=0) {
                search.put("lineId", lineId);
            }

            Page<ChargeStandard> page = chargeStandardService.queryChargeStandardPage(currentPage, pageSize,search);
            //放入page对象。
            view.addAttribute("page", page);
            view.addAttribute("lineId",lineId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "chargeStandard-list";
    }

}
