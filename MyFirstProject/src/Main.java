public class Main{
    public static void main(String [] args) {
        Library library = new Library();
        Person person = new Person("Xurshid", "PU2026", 20);
        person.displayInfo();
        Librarian librarian = new Librarian("Nigora", "PU2025", 21);
        librarian.displayInfo();
        librarian.addBook("O'tgan kunlar", "Abdulla Qodiriy", 1960, 0, library);
        librarian.issueBook(0, library);
        librarian.removeBook(0, library);
        librarian.addBook("O'tgan kunlar", "Abdulla Qodiriy", 1960, 0, library);
        Member member = new Member("Qanaqadir Harry", "UP0908", 100);
        member.displayInfo();
        member.borrowBook("O'tgan kunlar", "Abdulla Qodiriy", 1960, 0, library);
        member.returnBook("O'tgan kunlar", "Abdulla Qodiriy", 1960, 0, library);
        member.viewBorrowedBooks(0, library);
        Guest guest = new Guest("Bratishka", "PU2070", 12);
        guest.displayInfo();
        guest.viewCatalog(library);
    }
}