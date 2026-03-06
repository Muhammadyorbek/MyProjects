public class Guest extends Person {
    public Guest(String name, String ID, int age) {
        super(name, ID, age);
    }
    public void viewCatalog(Library library) {
        for (int i = 0; i < 100; i++) {
            if (library.books[i] != null && !library.books[i].isBorrowed) System.out.println("Name: " + library.books[i].name + " Author: " + library.books[i].author + " Published Year: " + library.books[i].publishedYear);
        }
    }
}
