package filters;

import beans.User;
import com.google.gson.Gson;
import responses.ExceptionResponse;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@WebFilter(filterName = "LoginFilter", urlPatterns = {"/dashboard","/dashboard/*"})
public class CookieFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        try {
            var user = (User) req.getSession().getAttribute("User");
            if (user == null) {
                throw new Exception("Unauthorized user");
            }
            System.out.println(user);
            if(!checkForUserIdCookie(req.getCookies(), user)) {
                throw new Exception("No proper cookie");
            }
            chain.doFilter(request, resp);
        }
        catch (Exception ex) {
            Gson gson = new Gson();
            resp.setContentType("application/json;charset=UTF-8");
            ExceptionResponse exResponse = new ExceptionResponse();
            exResponse.setMessage(ex.getLocalizedMessage());
            exResponse.setStatus(401);
            ((HttpServletResponse) resp).setStatus(401);
            gson.toJson(exResponse, resp.getWriter());
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

    private boolean checkForUserIdCookie(Cookie[] cookies, User user) {
        for (var cookie : cookies) {
            if("userID".equals(cookie.getName())) {
                return new String(Base64.getDecoder().decode(cookie.getValue().getBytes())).equals(user.getLogin());
            }
        }
        return false;
    }
}
