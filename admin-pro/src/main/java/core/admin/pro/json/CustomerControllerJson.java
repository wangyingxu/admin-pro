package core.admin.pro.json;

import com.google.gson.Gson;
import core.admin.common.AdminConstants;
import core.admin.common.page.Page;
import core.admin.domain.AdminUser;
import core.admin.domain.Customer;
import core.admin.service.CustomerService;
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

/**
 * Created by pop on 16/1/23.
 */
@RestController
@RequestMapping(value = "/json/admin/customer", method = {RequestMethod.GET, RequestMethod.POST})
public class CustomerControllerJson {
    private static final Logger logger = LoggerFactory.getLogger(CustomerControllerJson.class);

    @Autowired
    private CustomerService customerService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(HttpServletRequest request, @RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if (sessionAdminUser != null) {
                search.put("userId", sessionAdminUser.getId().intValue());
                search.put("networkId", sessionAdminUser.getNetworkId().intValue());
            }
            String keyword = request.getParameter("keyword");
            if(!StringUtils.isEmpty(keyword)){
                search.put("company", keyword);
                search.put("address", keyword);
                search.put("contacts", keyword);
                search.put("mobile", keyword);
                view.addAttribute("keyword", keyword);
            }
            Page<Customer> page = customerService.queryCustomerPage(currentPage, pageSize, search);
            //放入page对象。
            view.addAttribute("page", page);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "customer-list";
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT})
    public String edit(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                       Model view) {
        try {
            if (id != null && id.longValue() > 0) {
                Customer customer = customerService.queryCustomerById(id);
                view.addAttribute("customer", customer);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "customer-edit";
    }

    @RequestMapping(value = "/editJson", method = {RequestMethod.PUT})
    @ResponseBody
    public String editJson(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        try {

            if (id != null && id.longValue() > 0) {
                Customer customer= customerService.queryCustomerById(id);

                Gson gson = new Gson();
                String jsonValue =  gson.toJson(customer);
                return jsonValue;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "{}";
        }
        return "{}";
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ResponseBody
    public String save(HttpServletRequest request, Customer customer,
                       Model view) {
        try {
            AdminUser sessionAdminUser = SessionUtils.getSessionValue(request, SESSION_USER_KEY);
            if (sessionAdminUser != null) {
                customer.setUserId(sessionAdminUser.getId().intValue());
                customer.setNetworkId(sessionAdminUser.getNetworkId().intValue());
            }
            long rows = customerService.saveCustomer(customer);
            view.addAttribute("customer", customer);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "保存成功！");
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public String delete(@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
                         Model view) {
        try {

            long rows = customerService.deleteCustomer(id);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return String.format(AdminConstants.WEB_IFRAME_SCRIPT, "删除成功！");
    }
}
