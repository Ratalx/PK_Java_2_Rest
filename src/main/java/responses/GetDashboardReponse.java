package responses;

import beans.Book;

import java.util.List;

public class GetDashboardReponse {
    private List<Book> books;
    private int status;

    public GetDashboardReponse() {
    }

    public GetDashboardReponse(List<Book> books, int status) {
        this.books = books;
        this.status = status;
    }
}
