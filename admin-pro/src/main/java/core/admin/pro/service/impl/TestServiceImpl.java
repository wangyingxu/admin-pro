package core.admin.pro.service.impl;

import core.admin.dao.CustomerMapper;
import core.admin.domain.Customer;
import core.admin.pro.service.TestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenshaofeng1996 on 2016/8/29.
 */
@Service("testService")
public class TestServiceImpl implements TestService {
    @Resource
    private CustomerMapper customerMapper;

    public List<Customer> getTestList(){
        Map<String, Object> map = new HashMap<>();
        return customerMapper.selectList(map);
    }


}
