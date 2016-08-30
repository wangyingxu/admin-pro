package core.admin.pro.json;

import com.google.gson.Gson;
import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.*;
import core.admin.service.BranchNetworkService;
import core.admin.service.CustomerService;
import core.admin.service.DeliveryService;
import core.admin.service.OrderService;
import core.admin.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.admin.common.AdminConstants.SESSION_USER_KEY;

@RestController
@RequestMapping(value = "/json/admin/order", method = {RequestMethod.GET, RequestMethod.POST})
public class OrderControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(OrderControllerJson.class);

    @Autowired
    private OrderService orderService;


    @Autowired
    private DeliveryService deliveryService;


    @Autowired
    private BranchNetworkService branchNetworkService;

    @Autowired
    private CustomerService customerService;


    private static Integer pageSize = 100;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(HttpServletRequest request, @RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view,@RequestParam(value = "mark", required = false, defaultValue = "0") int mark ) {
        try {

            if (id != null && id.longValue() > 0) {
                Order order = orderService.queryOrderById(id);
                view.addAttribute("order", order);
                view.addAttribute("goodsSize", order.getGoods().size());
            }else{
                view.addAttribute("goodsSize", 1);
            }

            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if (sessionAdminUser != null) {
                search.put("userId", sessionAdminUser.getId().intValue());
                search.put("networkId", sessionAdminUser.getNetworkId());
            }

            List<Customer> customerList = customerService.queryCustomer(search);
            view.addAttribute("customerList",customerList);

            view.addAttribute("orderTypeMap", AdminConstants.orderTypeMap);

            view.addAttribute("payTypeMap", AdminConstants.payType);
            view.addAttribute("priceTypeMap", AdminConstants.priceType);

            view.addAttribute("receiveTypeMap", AdminConstants.receiveType);

            view.addAttribute("orderCategoryMap", AdminConstants.orderCategory);

            view.addAttribute("packageTypeMap", AdminConstants.packageType);


            StringBuilder ocBuilder = new StringBuilder(512);
            StringBuilder pBuilder = new StringBuilder(512);
            int idx=0;
            for (Map.Entry<Integer, String> entry : AdminConstants.orderCategory.entrySet()) {
                 ocBuilder.append("{");
                ocBuilder.append("\'key\':").append("\'").append(entry.getKey()).append("\',");
                ocBuilder.append("\'value\':").append("\'").append(entry.getValue()).append("\'");
                ocBuilder.append("}");
                if(idx!= AdminConstants.orderCategory.entrySet().size()-1){
                    ocBuilder.append(",");
                }
                idx++;
            }
                idx=0;
            for (Map.Entry<Integer, String> entry : AdminConstants.packageType.entrySet()) {
                pBuilder.append("{");
                pBuilder.append("\'key\':").append("\'").append(entry.getKey()).append("\'").append(",");
                pBuilder.append("\'value\':").append("\'").append(entry.getValue()).append("\'");
                pBuilder.append("}");
                if(idx!= AdminConstants.packageType.entrySet().size()-1){
                    pBuilder.append(",");
                }
                idx++;
            }

            Gson gson = new Gson();
            String orderCategoryJson ="["+ocBuilder.toString()+"]";
            String packageTypeJson =  "["+pBuilder.toString()+"]";

            view.addAttribute("packageTypeJson",packageTypeJson);
            view.addAttribute("orderCategoryJson",orderCategoryJson);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if(mark==1){
            return "order-view";
        }else{
            return "order-edit";
        }

    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = orderService.deleteOrder(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(Order order, HttpServletRequest request,
                       Model view, @RequestParam("files") List<MultipartFile> goodFile) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
                order.setUserId(sessionAdminUser.getId().intValue());
                order.setNetworkId(sessionAdminUser.getNetworkId());
            }
            long rows = orderService.saveOrder(order, goodFile);
            view.addAttribute("order", order);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,HttpServletRequest request,
                       Model view) {
        try {

            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);

            AdminConstants.user = sessionAdminUser;

            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if(sessionAdminUser!=null){
                if(sessionAdminUser.getLogisticsId()!=null && sessionAdminUser.getLogisticsId()>0){

                    search.put("networkId", sessionAdminUser.getNetworkId());
                    search.put("companyId", sessionAdminUser.getLogisticsId());
                }else {
                    search.put("networkId", sessionAdminUser.getNetworkId());
                    search.put("companyId", null);
                }


                view.addAttribute("networkId",sessionAdminUser.getNetworkId());
            }
            Page<Order> page = orderService.queryOrderPage(currentPage, pageSize, search);
            //放入page对象。
            view.addAttribute("page", page);

            List<BranchNetwork> networks = branchNetworkService.queryBranchNetwork(new HashMap<String, Object>());
             Map<Integer,String> networkMap = new HashMap<>();
            for(BranchNetwork branchNetwork:networks)
            {
                networkMap.put(branchNetwork.getId(),branchNetwork.getName());
            }

            view.addAttribute("networkMap",networkMap);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "order-list";
    }


    @RequestMapping(value = "/confirmGood", method = {RequestMethod.POST})
    @ResponseBody
    public String confirmGood(HttpServletRequest request,int orderId) {
        try {

            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){

                if(orderId>0){
                    Delivery delivery = deliveryService.queryDeliveryByOrderIdAndReceiveNetworkId(Long.valueOf(orderId), Long.valueOf(sessionAdminUser.getNetworkId()));
                    if(delivery!=null){
                        delivery.setStatus(AdminConstants.DELIVERY_STATUS_SUCCESS);
                        delivery.setReceiveTime(System.currentTimeMillis());
                        delivery.setReceiveUserId(sessionAdminUser.getId().intValue());

                        deliveryService.saveDelivery(delivery);
                    }else
                    {
                        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "收货出错！");
                    }
                }else{

                    return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "订单号不能为空！");
                }


            }else{
                return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "请重新登录！");
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "收货成功！");
    }

    @RequestMapping(value = "/acceptEdit", method = {RequestMethod.PUT})
    public String acceptEdit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {

            if (id != null && id.longValue() > 0) {
                Order order = orderService.queryOrderById(id);
                view.addAttribute("order", order);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "order-accept";
    }

    @RequestMapping(value = "/acceptSave", method = {RequestMethod.POST})
    @ResponseBody
    public String acceptSave(Order order, HttpServletRequest request,
                             Model view, @RequestParam("files") List<MultipartFile> goodFile) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){
                order.setUserId(sessionAdminUser.getId().intValue());
                order.setNetworkId(sessionAdminUser.getNetworkId());
            }
            long rows = orderService.saveAccpt(order, goodFile);
            view.addAttribute("order", order);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }


}
