package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.AdminUser;
import core.admin.domain.OrderLine;
import core.admin.domain.Park;
import core.admin.service.OrderLineService;
import core.admin.service.ParkService;
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

@RestController
@RequestMapping(value = "/json/admin/orderLine", method = {RequestMethod.GET, RequestMethod.POST})
public class OrderLineControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(OrderLineControllerJson.class);

    @Autowired
    private OrderLineService orderLineService;

    @Autowired
    private ParkService parkService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       int orderId,
                       Model view) {
        try {
            List<Park> parkList = parkService.queryPark(new HashMap<String, Object>());
            if(parkList!=null && parkList.size()>0){
                Map<Integer,String> parkMap = new HashMap<>();
                for(Park info:parkList)
                {
                    parkMap.put(info.getId(),info.getName());
                }
                view.addAttribute("parkMap", parkMap);

            }
            OrderLine orderLine =null;
            if (id != null && id.longValue() > 0) {
                 orderLine = orderLineService.queryOrderLineById(id);
            }else{
                orderLine = new OrderLine();
                orderLine.setOrderId(orderId);
            }
            view.addAttribute("orderLine", orderLine);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "orderLine-edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = orderLineService.deleteOrderLine(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(OrderLine orderLine, HttpServletRequest request,
                       Model view) {
        try {

            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
                orderLine.setUserId(sessionAdminUser.getId().intValue());
            }
            long rows = orderLineService.saveOrderLine(orderLine);
            view.addAttribute("orderLine", orderLine);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       @RequestParam(value = "orderId", required = false) Long orderId,
                       Model view) {
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if (orderId != null) {
                search.put("orderId", orderId);
            }

            Page<OrderLine> page = orderLineService.queryOrderLinePage(currentPage, pageSize,search);
            //放入page对象。
            view.addAttribute("page", page);
            view.addAttribute("orderId", orderId);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "orderLine-list";
    }

}
