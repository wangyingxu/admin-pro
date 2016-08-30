package core.admin.pro.interceptor;

import core.admin.common.AdminConstants;
import core.admin.common.ErrorCode;
import core.admin.exception.ServiceDataException;
import core.admin.util.SessionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * DATE:8/15/15 10:53
 * AUTHOR:wangzhen
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession httpSession = SessionUtils.getSession(request);

        Object object = httpSession.getAttribute(AdminConstants.SESSION_USER_KEY);
        if (object == null) {
            throw new ServiceDataException(ErrorCode.ERR_USER_NOT_SIGN_ERROR);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        super.afterCompletion(request, response, handler, ex);
    }

}
