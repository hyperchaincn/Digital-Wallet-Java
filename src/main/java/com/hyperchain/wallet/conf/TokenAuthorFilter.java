package com.hyperchain.wallet.conf;


import com.hyperchain.wallet.Dao.UserDao;
import com.hyperchain.wallet.model.User;
import com.hyperchain.wallet.util.JwtHelper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;


@Component
@WebFilter(urlPatterns={"/*"}, filterName="tokenAuthorFilter")
public class TokenAuthorFilter implements Filter {

    @Autowired
    UserDao userDao;

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse) response;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        String authHeader = req.getHeader("authorization");

        String uri = req.getRequestURI();
        String[] filterwords = new String[]{"dashboard",".html","static","bower","js","css","fonts","views","login","token","hello"};
        System.out.println("URI"+uri);
        for (String word : filterwords){
            if (uri.contains(word) ){
                chain.doFilter(request, response);
                return;
            }
        }

        if ("OPTIONS".equals(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(req, response);
        } else {
            try {
                if (authHeader == null) {
                    throw new LoginException("not auth");
                }

                String token = authHeader.substring(6);
                final Base64.Decoder decoder = Base64.getDecoder();
                try {
                    token = new String(decoder.decode(token), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                token = token.split(":")[0];
                final Map<String,Object> map = JwtHelper.parserJavaWebToken(token);
                if(map == null){
                    throw new LoginException("not auth");
                }
                String phone = (String)map.get("phone");
                req.setAttribute("phone",phone);
                req.setAttribute("password", map.get("password"));
                req.setAttribute("address", map.get("address"));
                User user = userDao.FindUser(phone);
                req.setAttribute("private", user.privateKey);

            } catch (final Exception e) {
                    e.printStackTrace();
            }

            chain.doFilter(req, res);
        }
    }


    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

}