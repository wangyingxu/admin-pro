package core.admin.pro.service;

/**
 * Created by chenshaofeng1996 on 2016/8/29.
 */

import core.admin.domain.Customer;

import java.util.List;

/**
 * 这是自定义的service  如果任务不能完成 可以
 * 自定义service 接口 实现
 */
public interface TestService {
    List<Customer> getTestList();
}
