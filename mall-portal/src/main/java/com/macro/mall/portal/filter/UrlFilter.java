package com.macro.mall.portal.filter;

import com.macro.mall.mapper.DmsDomainSettingMapper;
import com.macro.mall.model.DmsDomainSetting;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//该过滤器废弃
@WebFilter(filterName = "urlFilter", urlPatterns = "/XonePage/*")
public class UrlFilter implements Filter {

    @Autowired
    private DmsDomainSettingMapper domainSettingMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        System.out.println("----------------------->过滤器被创建");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // 获取域名
        String serverName = request.getServerName();
        // 获取请求路径
        String path = httpServletRequest.getRequestURI();
        String queryString = (httpServletRequest.getQueryString() == null ? "" : "?"+httpServletRequest.getQueryString());   // 获取路径中的参数

        DmsDomainSetting domainSetting = domainSettingMapper.selectByPrimaryKey(1l);
        if(domainSetting != null){
            String mainDomain = domainSetting.getMainDomain();
            String landingDomain = domainSetting.getLandingDomain();

            int end = serverName.indexOf(mainDomain);
            if (end == 0) {  // 判断是否是入口域名
                httpServletResponse.setStatus(301);
                httpServletResponse.setHeader( "Location", "http://"+landingDomain+path+queryString);
                httpServletResponse.setHeader( "Connection", "close" );
                return;
            }
        }

        chain.doFilter(request, response);
    }


    @Override
    public void destroy() {

        System.out.println("----------------------->过滤器被销毁");
    }
}