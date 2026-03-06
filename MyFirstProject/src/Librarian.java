public class Librarian extends Person {
    public Librarian(String name, String ID, int age) {
        super(name, ID, age);
    }
    public void addBook(String name, String author, int publishedYear, int bookIndex, Library library) {
        library.books[bookIndex] = new Book(name, author, publishedYear, bookIndex, "No");
    }
    public void removeBook(int bookIndex, Library library) {
        library.books[bookIndex] = null;
    }
    public void issueBook(int bookIndex, Library library) {
        if (library.books[bookIndex] != null) library.books[bookIndex].isBorrowed = true;
    }
}
class Book {
    int bookIndex;
    String name;
    String author;
    int publishedYear;
    boolean isBorrowed;
    public Book(String name, String author, int publishedYear, int bookIndex, String isBorrowed) {
        this.name = name;
        this.author = author;
        this.publishedYear = publishedYear;
        this.bookIndex = bookIndex;
        if (isBorrowed.equals("Yes")) this.isBorrowed = true;
        else if (isBorrowed.equals("No")) this.isBorrowed = false;
    }
}