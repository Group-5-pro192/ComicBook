package comicbookmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ComicBookManagement {

    // Declare ArrayList to store comic books and set the constant file name
    private ArrayList<ComicBook> comicList = new ArrayList<>();
    private final String FILE_NAME = "comicbook.txt";

    public ComicBookManagement() {
    }

    // Getter for team members to access the comic book list
    public ArrayList<ComicBook> getComicList() {
        return comicList;
    }

    /**
     * Loads data from 'comicbook.txt' into the ArrayList.
     * File structure:
     * - Line 1: Total number of comic books (N)
     * - Every subsequent 5 lines represent 1 comic book: ID, Title, Price, Author, Volume
     */
    public void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("Data file '" + FILE_NAME + "' not found. Starting with empty list.");
            return;
        }

        try (Scanner sc = new Scanner(file)) {
            if (sc.hasNextInt()) {
                int total = Integer.parseInt(sc.nextLine().trim());
                for (int i = 0; i < total; i++) {
                    if (!sc.hasNextLine()) break;
                    
                    int id = Integer.parseInt(sc.nextLine().trim());
                    String title = sc.nextLine().trim();
                    double price = Double.parseDouble(sc.nextLine().trim());
                    String author = sc.nextLine().trim();
                    String volume = sc.nextLine().trim();

                    comicList.add(new ComicBook(id, title, price, author, volume));
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }
}
