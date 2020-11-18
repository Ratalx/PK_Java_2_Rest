package servlets;

import org.mortbay.log.Log;
import requests.LoginRequest;
import beans.Role;
import beans.User;
import com.google.gson.Gson;
import responses.ExceptionResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        System.out.println("LoginServlet Post");

        try {
          LoginRequest loginRequest = gson.fromJson(request.getReader(), LoginRequest.class);
          response.setContentType("application/json;charset=UTF-8");
            System.out.println("LoginServlet Post Login= " + loginRequest.login + " Password= " + loginRequest.password);

          if (loginRequest.login.equals("admin")) {
              System.out.println("Login is admin ");
              if (adminLogin(request, response, loginRequest)) {
                  System.out.println("Adming SignedIn");
                  User user = new User(loginRequest.login, loginRequest.password, Role.ADMIN);
                  request.getSession().setAttribute("User", user);
                  var userIdBase64 = getBase64FromString(user.getLogin());
                  response.addCookie(new Cookie("userID", userIdBase64));
                  response.sendRedirect("dashboard");
              }
          }
          else {
              if (userLogin(request, response, loginRequest)) {
                  User user = new User(loginRequest.login, loginRequest.password, Role.USER);
                  request.getSession().setAttribute("User", user);
                  var userIdBase64 = getBase64FromString(user.getLogin());
                  response.addCookie(new Cookie("userID", userIdBase64));
                  response.sendRedirect("dashboard");
              }
              else {
                  response.sendRedirect("loginFailed.html");

              }
          }

      }
      catch (Exception ex) {
          ExceptionResponse exceptionResponse = new ExceptionResponse();
          exceptionResponse.setMessage(ex.getLocalizedMessage());
          exceptionResponse.setStatus(500);

          response.setStatus(500);
          gson.toJson(exceptionResponse, response.getWriter());
          response.sendRedirect("loginFailed.html");

      }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("LoginServlet Get");

    }

    private boolean adminLogin(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest) {
        return loginRequest.password.equals("admin");
    }

    private boolean userLogin(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest) {
        return (loginRequest.login.equals("user") && loginRequest.password.equals("user"));
    }

    private String getBase64FromString(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
}
