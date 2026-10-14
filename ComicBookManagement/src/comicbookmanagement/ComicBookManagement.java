package comicbookmanagement;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class ComicBookManagement {

    // Declare ArrayList to store comic books and set the constant file name
    private ArrayList<ComicBook> comicList = new ArrayList<>();
    private final String FILE_NAME = "data/ComicBooks.txt";

    public ComicBookManagement() {
    }

    // Getter for team members to access the comic book list
    public ArrayList<ComicBook> getComicList() {
        return comicList;
    }

    /**
     * Loads data from FILE_NAME into the ArrayList.
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
                    int volume = Integer.parseInt(sc.nextLine().trim());

                    comicList.add(new ComicBook(id, title, price, author, volume));
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    /**
     * Function 1: Add new comic book.
     * Reads and validates each field; the ID must be unique.
     */
    public void addComicBook(Scanner sc) {
        int id;
        while (true) {
            id = readPositiveInt(sc, "Enter ID: ");
            if (findById(id) == null) break;
            System.out.println("ID " + id + " already exists. Please enter another ID.");
        }
        String title = readNonEmpty(sc, "Enter title: ");
        double price;
        while (true) {
            System.out.print("Enter rental price: ");
            try {
                price = Double.parseDouble(sc.nextLine().trim());
                if (price > 0 && !Double.isInfinite(price)) break;
            } catch (NumberFormatException e) {
            }
            System.out.println("Price must be a number greater than 0.");
        }
        String author = readNonEmpty(sc, "Enter author: ");
        int volume = readPositiveInt(sc, "Enter volume: ");

        comicList.add(new ComicBook(id, title, price, author, volume));
        System.out.println("Comic book added successfully.");
    }

    private ComicBook findById(int id) {
        for (ComicBook c : comicList) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    private int readPositiveInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n > 0) return n;
            } catch (NumberFormatException e) {
            }
            System.out.println("Please enter an integer greater than 0.");
        }
    }

    private String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("This field cannot be empty.");
        }
    }

    /**
     * Saves the ArrayList to FILE_NAME using the same format as loadData().
     */
    public void saveData() {
        try (PrintWriter pw = new PrintWriter(FILE_NAME)) {
            pw.println(comicList.size());
            for (ComicBook c : comicList) {
                pw.println(c.getId());
                pw.println(c.getTitle());
                pw.printf(Locale.US, "%.2f%n", c.getBookRentalPrice());
                pw.println(c.getAuthor());
                pw.println(c.getVolume());
            }
        } catch (Exception e) {
            System.out.println("Error writing data file: " + e.getMessage());
        }
    }

    /**
     * Function 6: About Us
     * Displays comprehensive project and team member information.
     */
    public static void displayAboutUs() {
        System.out.println("\n+---------------------------------------------------------------------------------------------------+");
        System.out.println("|                                        ABOUT US - PROJECT INFO                                    |");
        System.out.println("+---------------------------------------------------------------------------------------------------+");
        System.out.println("|  1. University : FPT University Campus Can Tho                                                    |");
        System.out.println("|  2. College    : Information Technology College - Software Engineering Department                 |");
        System.out.println("|  3. Subject    : PRO192 - Object-Oriented Programming                                             |");
        System.out.println("|  4. Class      : SE2102                                                                           |");
        System.out.println("|  5. Mentor     : Vo Hong Khanh                                                                    |");
        System.out.println("|  6. Group      : Group 05                                                                         |");
        System.out.println("+---------------------------------------------------------------------------------------------------+");
        System.out.println("|                                           TEAM MEMBERS                                            |");
        System.out.println("+-----+-----------+------------------------------+--------------------+-----------------------------+");
        System.out.printf("| %-3s | %-9s | %-28s | %-18s | %-27s |\n", "No.", "ID", "Full Name", "Role", "Assigned Task");
        System.out.println("+-----+-----------+------------------------------+--------------------+-----------------------------+");
        
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                1, "CE210032", "Ngo Thi Thao Huynh", "Leader", "Testing & Data");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                2, "CE210356", "Ngo Ngoc Anh Thu", "Secretary", "Class ComicBookManagement");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                3, "CE210006", "Nguyen Huu Loc", "Member", "About Us");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                4, "CE210031", "Le Ngoc Ngan", "Member", "Menu");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                5, "CE190701", "Dinh Minh Tien", "Member", "Class ComicBook");
                
        System.out.println("+-----+-----------+------------------------------+--------------------+-----------------------------+");
        System.out.println();
    }

    public static void main(String[] args) {

        ComicBookManagement manager = new ComicBookManagement();
        manager.loadData();
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        do {

            System.out.println("\n          COMIC BOOK RENTAL SHOP # SE2102-Group05");
            System.out.println("1. Add new comic book.");
            System.out.println("2. Search book by title.");
            System.out.println("3. Search book of an author.");
            System.out.println("4. Update book rental price.");
            System.out.println("5. Delete comic book.");
            System.out.println("6. About us.");
            System.out.println("7. Quit.");

            choice = getValidChoice(sc);

            switch (choice) {
                case 1:
                    manager.addComicBook(sc);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    System.out.println("This function is not implemented yet.");
                    break;
                case 6:
                    displayAboutUs();
                    break;
                case 7:
                    manager.saveData();
                    System.out.println("Data saved successfully. Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("The number you entered is out of range. Please choose a number between 1 and 7!");
                    break;
            }
        } while (choice != 7);

        sc.close();
    }

    private static int getValidChoice(Scanner sc) {
        while (true) {
            System.out.print("     Please select a function: ");
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {

                System.out.println("Input format error! You must enter an integer number (1-7), not letters or special characters.");
            }
        }
    }
}
