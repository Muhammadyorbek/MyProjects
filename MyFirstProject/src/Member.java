public class Member extends Person{
    public Member(String name, String ID, int age) {
        super(name, ID, age);
    }
    public void borrowBook(String name, String author, int publishedYear, int bookIndex, Library library) {
        library.books[bookIndex] = new Book(name, author, publishedYear, bookIndex, "Yes");
    }
    public void returnBook(String name, String author, int publishedYear, int bookIndex, Library library) {
        library.books[bookIndex] = new Book(name, author, publishedYear, bookIndex, "No");
    }
    public void viewBorrowedBooks(int bookIndex, Library library) {
        System.out.println("Name: " + library.books[bookIndex].name + "\nAuthor: " + library.books[bookIndex].author + "\nPublished year: " + library.books[bookIndex].publishedYear);
    }
}
