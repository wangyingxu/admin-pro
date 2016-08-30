package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.OrderGoods;
import core.admin.service.OrderGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/json/admin/orderGoods", method = {RequestMethod.GET, RequestMethod.POST})
public class OrderGoodsControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(OrderGoodsControllerJson.class);

    @Autowired
    private OrderGoodsService orderGoodsService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {

            if (id != null && id.longValue() > 0) {
                OrderGoods orderGoods = orderGoodsService.queryOrderGoodsById(id);
                view.addAttribute("orderGoods", orderGoods);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "admin/orderGoods/edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = orderGoodsService.deleteOrderGoods(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(OrderGoods orderGoods,
                       Model view) {
        try {

            long rows = orderGoodsService.saveOrderGoods(orderGoods);
            view.addAttribute("orderGoods", orderGoods);

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

            Page<OrderGoods> page = orderGoodsService.queryOrderGoodsPage(currentPage, pageSize,search);
            //放入page对象。
            view.addAttribute("page", page);
            view.addAttribute("id", id);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "/admin/orderGoods/list";
    }

}
