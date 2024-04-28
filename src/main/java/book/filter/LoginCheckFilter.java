package book.filter;

import book.common.BaseContext;
import book.common.R;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;


@Slf4j
@CrossOrigin
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 设置CORS响应头，允许所有源进行跨域请求
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "authorization, Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 如果需要发送cookie，则设置为true

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String requestURL = request.getRequestURI();
        log.info("拦截到请求：{}", requestURL);

        if (Objects.equals(requestURL, "/user/login")) {
            log.info("本次请求不需要处理");
            filterChain.doFilter(request,response);
            return;
        }

        String token = request.getHeader("Authorization");

        if (token!= null && VToken(token)) {
            filterChain.doFilter(request,response);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSON.toJSONString(R.error("NOT_LOGIN")));
    }

    /**
     * 验证token
     * @param token token
     * @return boolean true/false
     */
    public boolean VToken(String token) {
        /*
        提取token
         */
        if (token != null) {
            token =token.substring("Bearer ".length());
            int last = token.lastIndexOf(".");
            if (last != -1) {
                token = token.substring(0, last);
            }
            try {
                /*
                验证token
                 */
                Algorithm algorithm = Algorithm.HMAC256("BaiSheHuaSheng");
                DecodedJWT jwt = JWT
                        .require(algorithm)
                        .withIssuer("http://loclhost:8080") // 设置发行人
                        .build()
                        .verify(token);
                Date exp = jwt.getExpiresAt();
                /*
                  计算token过期时间并输出
                 */
                Instant oldInstant = exp.toInstant();
                Instant nowInstant = Instant.now();
                Duration duration = Duration.between(nowInstant, oldInstant);
                long days = duration.toDays();

                log.info("当前用户token距离过期还需要：{}天{}时{}分{}秒", duration.toDays(), duration.toHoursPart(),duration.toMinutesPart(), duration.getSeconds() % 60);
                /*
                将用户信息放到公共字段自动填充的数据中并输出
                 */
                Long user = Long.parseLong(jwt.getSubject());
                BaseContext.setCurrentId(user);
                log.info("当前登录用户ID：{}", user);
                return true;
            }catch(TokenExpiredException e) {
                log.info("token已过期");
                return false;
            }
        }
        return false;
    }
}
