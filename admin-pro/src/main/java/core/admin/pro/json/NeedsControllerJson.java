package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.Needs;
import core.admin.service.NeedsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RequestMapping(value = "/json/admin/needs", method = {RequestMethod.GET, RequestMethod.POST})
@RestController
public class NeedsControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(NeedsControllerJson.class);

    @Autowired
    private NeedsService needsService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {

            if (id != null && id.longValue() > 0) {
                Needs needs = needsService.queryNeedsById(id);
                view.addAttribute("needs", needs);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "admin/needs/edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = needsService.deleteNeeds(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(Needs needs,
                       Model view) {
        try {

            long rows = needsService.saveNeeds(needs);
            view.addAttribute("needs", needs);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       @RequestParam(value = "id", required = false) Long id,
                       Model view) {
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if (id != null) {
                search.put("id", id);
            }

            Page<Needs> page = needsService.queryNeedsPage(currentPage, pageSize, search);
            //放入page对象。
            view.addAttribute("page", page);
            view.addAttribute("id", id);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "/admin/needs/list";
    }

}
