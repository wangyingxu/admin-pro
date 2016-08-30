package core.admin.pro.json;

import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.*;
import core.admin.service.*;
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
@RequestMapping(value = "/json/admin/delivery", method = {RequestMethod.GET, RequestMethod.POST})
public class DeliveryControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryControllerJson.class);

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private BranchNetworkService branchNetworkService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private OrderService orderService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit( Long orderId,HttpServletRequest request,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null){

                view.addAttribute("networkId",sessionAdminUser.getNetworkId());
                //是否是物流公司用户
                if(sessionAdminUser.getLogisticsId()!=null && sessionAdminUser.getLogisticsId()!=0){
                    view.addAttribute("showType",1);

                }else{
                    view.addAttribute("showType",0);
                }
                view.addAttribute("networkId",sessionAdminUser.getNetworkId());
                /**
                 * 当前用户所在地区的所有物流公司列表
                 *
                 */
                Map<String, Object> search = new HashMap<String, Object>();
                search.put("provCode",sessionAdminUser.getProvCode());
                search.put("areaCode", sessionAdminUser.getAreaCode());

                Page<Park> page = parkService.queryParkPage(1, 50, search);
                if(page!=null && page.getResult()!=null && page.getResult().size()>0)
                {

                    List<LogisticsInfo> logisticsInfoList = logisticsInfoService.queryLogisticsInfo(page.getResult());
                    view.addAttribute("logisticsList",logisticsInfoList);

                }
                Page<CarInfo> carInfoPage = carInfoService.queryCarInfoPage(1, 50, new HashMap<String, Object>());

                if(carInfoPage!=null && carInfoPage.getResult()!=null){
                    view.addAttribute("carList",carInfoPage.getResult());
                }

                if (orderId!=null ) {

                    Delivery delivery = deliveryService.queryDeliveryByOrderId(orderId,Long.valueOf(sessionAdminUser.getNetworkId()));
                    if(delivery==null){
                        delivery = new Delivery();
                        delivery.setOrderId(orderId.intValue());
                        if(sessionAdminUser.getLogisticsId()!=null && sessionAdminUser.getLogisticsId()!=0){

                            delivery.setSendType(2);
                        }else{
                            delivery.setSendType(1);
                        }
                        delivery.setReceiveType(1);

                        Order order = orderService.queryOrderById(orderId);
                        if(order!=null){

                            delivery.setCompany(order.getConsignee().getConsigneeName());
                            delivery.setAddress(order.getConsignee().getAddress());
                            delivery.setComments(order.getConsignee().getComment());
                            delivery.setMobile(order.getConsignee().getLinkmanMobile());

                        }
                    }



                    List<LogisticsInfo> logisticsInfoList = logisticsInfoService.searchLogisticsInfo(new HashMap<String, Object>());
                    view.addAttribute("receiveCompanyList",logisticsInfoList);
                    view.addAttribute("delivery", delivery);
                }
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "delivery-edit";
    }


    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = deliveryService.deleteDelivery(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(Delivery delivery, HttpServletRequest request,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if(sessionAdminUser!=null )
                delivery.setSendNetworkId(sessionAdminUser.getNetworkId());
            delivery.setSendUserId(sessionAdminUser.getId().intValue());

            /**
             * 收货类型为最终客户,则从order中取用户信息
             */
            if(2 == delivery.getReceiveType() ){

                Order order = orderService.queryOrderById(Long.valueOf(delivery.getOrderId()));
                if(order!=null){
                    delivery.setProvCode(order.getConsignee().getProvCode());
                    delivery.setCityCode(order.getConsignee().getCityCode());
                    delivery.setAreaCode(order.getConsignee().getAreaCode());
                    delivery.setReceiveLogisticsId(0);
                }
            }

            long rows = deliveryService.saveDelivery(delivery);
            view.addAttribute("delivery", delivery);

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

            Page<Delivery> page = deliveryService.queryDeliveryPage(currentPage, pageSize,search);
            //放入page对象。
            view.addAttribute("page", page);
            view.addAttribute("id", id);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "delivery-list";
    }

}
