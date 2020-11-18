package helpers;


import beans.Book;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;

@WebListener()
public class LibraryContextServletListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var Books = new ArrayList<Book>();
        for(int i =0; i< 10; ++i)
        {
            var NewBook = new Book();
            NewBook.setAuthor("Author"+ i);
            NewBook.setTitle("Title"+ i);
            NewBook.setYear("200"+ i);
            Books.add(NewBook);

        }
        sce.getServletContext().setAttribute("Books", Books);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
