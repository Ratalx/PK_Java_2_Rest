package beans;

public class Book {
    public static int IdCounter;

    public int getId() {
        return Id;
    }

    private int Id;
    private String Title;
    private String Author;
    private String Year;

    public String getTitle() {
        return Title;
    }

    public String getAuthor() {
        return Author;
    }

    public String getYear() {
        return Year;
    }


    public void setTitle(String title) {
        Title = title;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public void setYear(String year) {
        Year = year;
    }

    public Book() {
        this.Id = ++IdCounter;
        this.Year = "0000";
        this.Title = "PlaceHolder";
        this.Author ="PlaceHolder";
    }
    public Book(String title, String author, String year) {
        this.Id = ++IdCounter;
        this.Year = year;
        this.Title = title;
        this.Author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "Title='" + Title + '\'' +
                ", Author='" + Author + '\'' +
                ", Year=" + Year +
                '}';
    }
}
