package core.admin.pro.json;

import core.admin.domain.Customer;
import core.admin.pro.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by chenshaofeng1996 on 2016/8/29.
 */
@RestController
@RequestMapping(value = "/json/test")
public class TestController {
    @Resource
    private TestService testService;
    @RequestMapping(value="list",method = RequestMethod.GET)
    @ResponseBody
    public  List<Customer>   getTestList(){
        List<Customer> testList = testService.getTestList();
        return testList;
    }
}
