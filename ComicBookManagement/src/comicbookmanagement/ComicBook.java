/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comicbookmanagement;

/**
 *
 * @author LENOVO X1CARBON
 */
public class ComicBook {

    private int id;
    private String title;
    private double bookRentalPrice;
    private String author;
    private int volume;

    public ComicBook() {
    }

    public ComicBook(int id, String title, double bookRentalPrice, String author, int volume) {
        this.id = id;
        this.title = title;
        this.bookRentalPrice = bookRentalPrice;
        this.author = author;
        this.volume = volume;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBookRentalPrice() {
        return bookRentalPrice;
    }

    public void setBookRentalPrice(double bookRentalPrice) {
        this.bookRentalPrice = bookRentalPrice;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "ComicBook{id=%d, title=%s, bookRentalPrice=%.2f, author=%s, volume=%d}",
                id, title, bookRentalPrice, author, volume);
    }
}
