package Day20;
class Book {
    private String title;
    private String author;
    private double price;

    public Book(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
}

class BookFormatter {
    public String getFormattedTitle(Book book) {
        return "Title: " + book.getTitle().toUpperCase();
    }

    public String getFormattedAuthor(Book book) {
        return "Author: " + book.getAuthor().toUpperCase();
    }
}

class BookPriceCalculator {
    public double calculateDiscountedPrice(Book book, double discountPercentage) {
        return book.getPrice() * (1 - discountPercentage);
    }
}

public class BookDemo {
    public static void main(String[] args) {
        Book book = new Book("Atomic Habits", "James Clear", 499);

        BookFormatter formatter = new BookFormatter();
        BookPriceCalculator calculator = new BookPriceCalculator();

        System.out.println(formatter.getFormattedTitle(book));
        System.out.println(formatter.getFormattedAuthor(book));
        System.out.println("Original Price: ₹" + book.getPrice());
        System.out.println("Discounted Price (15% off): ₹" + calculator.calculateDiscountedPrice(book, 0.15));
    }
}
