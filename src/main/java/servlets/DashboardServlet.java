package servlets;

import beans.Book;
import beans.Role;
import beans.User;
import com.google.gson.Gson;
import requests.NewBook;
import responses.ExceptionResponse;
import responses.GetDashboardReponse;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/dashboard/*"})
public class DashboardServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var user = (User) request.getSession().getAttribute("User");
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        try {
            if (user != null && user.getRole().equals(Role.ADMIN)) {
                System.out.println("In DashboardServlet POST");

                NewBook newBookRequest = gson.fromJson(request.getReader(), NewBook.class);
                ArrayList<Book> Books = (ArrayList<Book>) request.getServletContext().getAttribute("Books");
                var newBook = new Book(newBookRequest.Title, newBookRequest.Author, newBookRequest.Year);
                Books.add(newBook);
                request.getServletContext().setAttribute("Books", Books);
                response.setStatus(201);
                gson.toJson(newBook, response.getWriter());
            }
            else {
                throw new Exception("Unauthorized user");
            }
        } catch (Exception ex) {
            ExceptionResponse exResponse = new ExceptionResponse();
            exResponse.setMessage(ex.getLocalizedMessage());
            exResponse.setStatus(401);
            ((HttpServletResponse) response).setStatus(401);
            gson.toJson(exResponse, response.getWriter());

        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("In DashboardServlet GET");
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        try {
            List<Book> books = getBooksFromContext(request.getServletContext());
            GetDashboardReponse res = new GetDashboardReponse(books, 200);
            gson.toJson(res, response.getWriter());
        } catch (Exception ex) {
            ExceptionResponse exResponse = new ExceptionResponse();
            exResponse.setMessage(ex.getLocalizedMessage());
            exResponse.setStatus(401);
            ((HttpServletResponse) response).setStatus(401);
            gson.toJson(exResponse, response.getWriter());
        }
        System.out.println("Out DashboardServlet GET");
    }

    private List<Book> getBooksFromContext(ServletContext context) {
        return (ArrayList<Book>) context.getAttribute("Books");
    }


    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        var user = (User) req.getSession().getAttribute("User");
        try {
            if (user != null && user.getRole().equals(Role.ADMIN)) {
                var UrlParts = req.getRequestURL().toString().split("/");
                var Id = Integer.parseInt(UrlParts[UrlParts.length - 1]);
                var books = getBooksFromContext(req.getServletContext());
                var bookToRemove = books.stream().filter(book -> (book.getId() == Id)).findFirst();
                if (bookToRemove.isPresent()) {
                    books.removeIf(book -> (book.getId() == Id));
                    req.getServletContext().setAttribute("Books", books);
                    gson.toJson(bookToRemove, resp.getWriter());
                } else {
                    throw new Exception("No book with id = " + Id);
                }
            } else {
                throw new Exception("Unauthorized user");
            }
        } catch (Exception ex) {
            ExceptionResponse exResponse = new ExceptionResponse();
            exResponse.setMessage(ex.getLocalizedMessage());
            exResponse.setStatus(401);
            ((HttpServletResponse) resp).setStatus(401);
            gson.toJson(exResponse, resp.getWriter());
        }
    }
}
