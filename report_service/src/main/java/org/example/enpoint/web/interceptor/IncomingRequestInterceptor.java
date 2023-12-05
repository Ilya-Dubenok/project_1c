package org.example.enpoint.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.exception.RequestNotFromGatewayException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IncomingRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String gateWayHeader = request.getHeader("gateway");
        if (!"true".equals(gateWayHeader)) {
            throw new RequestNotFromGatewayException();
        }
        return true;
    }
}
