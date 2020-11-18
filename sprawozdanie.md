# Sprawozdanie z ćwiczenia "Rest"
## Strona początkowa
Na serwerze po uruchumieniu na servwerze tomcat endpointem początkowym jest:
```http://localhost:8080/Cw_1_Servlets_war/login```
## Ralizacja zadania
Większość klas użytych w programie jest zgodna z opisem w instrukcji, do Dashboard dodano kod odpowiedzialny za realizację żądań *POST* oraz *DELETE* tylko dla użytkownika, którego rola jest równa "*Role.ADMIN*", klasa przedstawia się w następujący sposób:
```java
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

```  
### Działanie
Niestety z braku czasu i umiejętności nie udało mi się stworzyć odpowiedniego frontendu jednak jako, że założeniem architektóry *REST* jest współpraca z dowolnym frontendem który spełnia zasady tworzenia API zdecydowałem się testować wszystkie funkcjonalności za pomocą Postmana.

API przedstawia się następująco:
##### Logowanie
end point = "http://localhost:8080/Cw_1_Servlets_war/login"
Metoda = *POST*
```json
{
"login": "<login>"
"password": "<password>"
}
```
Wynik: Użytkownik zostanie zalogowany oraz przekierowany na dashboard.
Odpowiedź: Ok w przypadku powodzenia.
##### Wyświetlanie dashboardu 
end point = "http://localhost:8080/Cw_1_Servlets_war/dashboard*"
Metoda = *GET*
Wynik: Odpowiedź będzie zawierać w sobię listę książek.
Odpowiedź: lista książek.
##### Dodawanie Książki
end point = "http://localhost:8080/Cw_1_Servlets_war/dashboard*"
Metoda = *POST*
```json
{
	"Title":"<Title>",
	"Author":"<Author>",
	"Year": "<Year>"
}
```
Wynik: Do zbioru książek zostanie dodana nowa zgodna z jsonem książka.
Odpowiedź: Nowo dodana książka.
##### Dodawanie Książki
end point = "http://localhost:8080/Cw_1_Servlets_war/dashboard/{ID}"
Metoda = *DELETE*

Wynik: Usunięta zostanie książka od Id == {ID}.
Odpowiedź: Usunięta książka.

### Obrazki Prezentująca działanie
##### Próba dostania się do dashboarda bez logowania
![Próba dostania się do dashboarda bez logowania](NotLogedDash.png)
##### Logowanie użytkownika
![Logowanie Użytkownika](UserLoginSucc.png)
##### Próba usunięcia książki przez użytkownika
![](UserDelete.png)
##### Logowanie admina
![](adminLog.png)
##### Usuwanie książki przez admina
![](DeleteAdmin.png)
##### Dashboard po usunięciu książki
![](DashAfterDel.png)
##### Dodanie książki przez admina
![](AddBook.png)
##### Dashboard po dodaniu książki
![](DashAfterAdd.png)

### Dane Logowania
Dane Logowania w programie to 
* dla Admina: 
```java
login: admin
hasło: admin
```
* dla usera:
```java
login: user
hasło: user
```

Dane do logowania dla usera są też domyślnymi w panelu logowania.


Imię Nazwisko: *Kacper Szczygieł*

Numer Albumu: 140453 