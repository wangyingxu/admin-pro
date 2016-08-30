package core.admin.pro.json;

import core.admin.common.page.Page;
import core.admin.domain.BranchNetwork;
import core.admin.service.BranchNetworkService;
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

@RestController
@RequestMapping(value = "/json/admin/branchNetwork")
public class BranchNetworkControllerJson {

    private static final Logger logger = LoggerFactory.getLogger(BranchNetworkControllerJson.class);

    @Autowired
    private BranchNetworkService branchNetworkService;

    private static Integer pageSize = 15;

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.GET} )
    @ResponseBody
    public Object edit(@PathVariable(value = "id") Long id, HttpServletRequest request, @RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage) {
        try {
            Map<String, Object> search = new HashMap<String, Object>();
            if (id != null && id.longValue() > 0) {
                search.put("id", id);
                BranchNetwork branchNetwork = branchNetworkService.queryBranchNetworkById(id);
                return branchNetwork;
            } else {
                String keyword = request.getParameter("keyword");
                if(!StringUtils.isEmpty(keyword)){
                    search.put("name", keyword);
                }
                Page<BranchNetwork> page = branchNetworkService.queryBranchNetworkPage(currentPage, pageSize,search);
                //List<BranchNetwork> branchNetworks = branchNetworkService.queryBranchNetwork(search);
                //放入page对象。
                List<Object> data = new ArrayList<Object>();
                data.add(page);
                data.add(keyword);
                return data;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }


    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.DELETE} )
    @ResponseBody
    public Object delete(@PathVariable(value = "id") Long id) {
        BranchNetwork branchNetwork = branchNetworkService.queryBranchNetworkById(id);
        try {
            long rows = branchNetworkService.deleteBranchNetwork(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return branchNetwork;
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    @ResponseBody
    public BranchNetwork save(@RequestBody BranchNetwork branchNetwork) {
        BranchNetwork b = new BranchNetwork();
        try {
            long  l = branchNetworkService.saveBranchNetwork(branchNetwork);
            b = branchNetworkService.queryBranchNetworkById(l);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return b;
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<BranchNetwork> list(HttpServletRequest request, @RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                       @RequestParam(value = "id", required = false) Long id,
                       Model view) {
        Page<BranchNetwork> page = null;
        try {
            //查询
            Map<String, Object> search = new HashMap<String, Object>();
            if (id != null) {
                search.put("id", id);
            }
            String keyword = request.getParameter("keyword");
            if(!StringUtils.isEmpty(keyword)){
                search.put("name", keyword);
                view.addAttribute("keyword", keyword);
            }
            page   = branchNetworkService.queryBranchNetworkPage(currentPage, pageSize,search);


            //放入page对象。
          /*  view.addAttribute("page", page);
            view.addAttribute("id", id);*/


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return page.getResult();
    }

}
