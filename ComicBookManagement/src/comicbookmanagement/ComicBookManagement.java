package comicbookmanagement;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ComicBookManagement {

    // Declare ArrayList to store comic books and set the constant file name
    private ArrayList<ComicBook> comicList = new ArrayList<>();
    private final String FILE_NAME = "data/ComicBooks.txt";
    // Set when the data file exists but cannot be read, so quitting does not overwrite it with an empty list
    private boolean loadFailed = false;

    // Input limits, chosen so every value fits its column in displayAll()
    private static final int MAX_NUMBER = 9999;
    private static final double MAX_PRICE = 1000.00;
    private static final int TITLE_MAX = 30;
    private static final int AUTHOR_MAX = 25;
    private static final String TITLE_PATTERN = "[A-Za-z0-9][A-Za-z0-9 .,:'!?&/()#+;\"-]*";
    private static final String AUTHOR_PATTERN = "[A-Za-z][A-Za-z .'-]*";

    public ComicBookManagement() {
    }

    // Getter for team members to access the comic book list
    public ArrayList<ComicBook> getComicList() {
        return comicList;
    }

    /**
     * Loads data from FILE_NAME into the ArrayList. File structure:
     * - Line 1: Total number of comic books (N)
     * - Every subsequent 5 lines represent 1 comic book: ID, Title, Price, Author, Volume
     */
    public void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("Data file '" + FILE_NAME + "' not found. Starting with an empty list.");
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.println("Cannot read data file '" + FILE_NAME + "' (it must be saved as UTF-8): "
                    + e.getMessage()
                    + ". Starting with an empty list. Changes will not be saved, so the file is kept as it is.");
            loadFailed = true;
            return;
        }
        if (lines.isEmpty()) {
            return;
        }
        int total;
        try {
            total = Integer.parseInt(lines.get(0).trim());
        } catch (NumberFormatException e) {
            // Count the records from the file instead, so quitting does not overwrite them with an empty list
            total = (lines.size() - 1) / 5;
            System.out.println("The first line of '" + FILE_NAME + "' is not a valid number of comic books."
                    + " Reading " + total + " comic book(s) found in the file instead.");
        }

        // One bad record is skipped instead of stopping the load, so the remaining books are not lost on save
        for (int i = 0; i < total; i++) {
            int start = 1 + i * 5;
            try {
                int id = Integer.parseInt(lines.get(start).trim());
                String title = lines.get(start + 1).trim();
                double price = Double.parseDouble(lines.get(start + 2).trim());
                String author = lines.get(start + 3).trim();
                int volume = Integer.parseInt(lines.get(start + 4).trim());
                if (title.isEmpty() || author.isEmpty()) {
                    throw new NumberFormatException();
                }
                if (findById(id) != null) {
                    System.out.println("Comic book #" + (i + 1) + " in '" + FILE_NAME + "' was skipped: ID " + id
                            + " is used more than once.");
                    continue;
                }
                comicList.add(new ComicBook(id, title, price, author, volume));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Comic book #" + (i + 1) + " in '" + FILE_NAME + "' (lines " + (start + 1) + " to "
                        + (start + 5) + ") is missing or invalid and was skipped.");
            }
        }
        System.out.println("Loaded " + comicList.size() + " comic book(s) from '" + FILE_NAME + "'.");
    }

    /**
     * Function 1: Add new comic book. Reads and validates each field; the ID must be unique.
     */
    public void addComicBook(Scanner sc) {
        int id = readInt(sc, "ID", MAX_NUMBER);
        while (findById(id) != null) {
            System.out.println("ID " + id + " is already used by another comic book. Please enter a different ID.");
            id = readInt(sc, "ID", MAX_NUMBER);
        }
        String title, author;
        int volume;
        boolean duplicate;
        do {
            title = readText(sc, "title", TITLE_MAX, TITLE_PATTERN,
                    "letters, digits, spaces and common punctuation (. , : ' - ! ? & / ( ) # + ; \")"
                    + " and must start with a letter or digit (example: One Piece)");
            author = readText(sc, "author", AUTHOR_MAX, AUTHOR_PATTERN,
                    "English letters, spaces and . ' - and must start with a letter (example: Eiichiro Oda)");
            volume = readInt(sc, "volume", MAX_NUMBER);
            duplicate = isDuplicate(title, author, volume);
            if (duplicate) {
                System.out.println("The book \"" + title + "\" by " + author + ", volume " + volume
                        + " already exists. Please enter the title, author and volume again.");
            }
        } while (duplicate);
        double price = readPrice(sc);

        comicList.add(new ComicBook(id, title, price, author, volume));
        System.out.println("Comic book added successfully.");
        displayAll();
    }

    /**
     * Function 2: Search book by title.
     */
    public void searchByTitle(Scanner sc) {
        System.out.print("Enter comic book title to search: ");
        String query = sc.nextLine().trim().replaceAll("\\s+", " ").toLowerCase();
        if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            return;
        }

        ArrayList<ComicBook> found = new ArrayList<>();
        for (ComicBook c : comicList) {
            if (c.getTitle().toLowerCase().contains(query)) {
                found.add(c);
            }
        }

        if (found.isEmpty()) {
            System.out.println("No comic book found with the given title.");
        } else {
            printTable(found, false);
            System.out.println("Found " + found.size() + " comic book(s) matching your search.");
        }
    }

    /**
     * Function 3: Search book by author.
     */
    public void searchByAuthor(Scanner sc) {
        System.out.print("Enter author name to search: ");
        String query = sc.nextLine().trim().replaceAll("\\s+", " ");
        if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            return;
        }

        ArrayList<ComicBook> found = new ArrayList<>();
        for (ComicBook c : comicList) {
            // Using equalsIgnoreCase as required by the assignment rule for author search
            if (c.getAuthor().equalsIgnoreCase(query)) {
                found.add(c);
            }
        }

        if (found.isEmpty()) {
            System.out.println("No comic book found with the given author.");
        } else {
            printTable(found, false);
            System.out.println("Found " + found.size() + " comic book(s) by this author.");
        }
    }

    private boolean isDuplicate(String title, String author, int volume) {
        for (ComicBook c : comicList) {
            if (c.getTitle().equalsIgnoreCase(title) && c.getAuthor().equalsIgnoreCase(author)
                    && c.getVolume() == volume) {
                return true;
            }
        }
        return false;
    }

    public void displayAll() {
        printTable(comicList, true);
        System.out.println("Total: " + comicList.size() + " comic book(s).");
    }

    // Columns widen to fit the longest value, so long data loaded from the file cannot break the table
    private void printTable(List<ComicBook> books, boolean numbered) {
        int noW = Math.max(3, String.valueOf(books.size()).length());
        int idW = 4, titleW = 30, priceW = 10, authorW = 25, volumeW = 6;
        for (ComicBook c : books) {
            idW = Math.max(idW, String.valueOf(c.getId()).length());
            titleW = Math.max(titleW, c.getTitle().length());
            priceW = Math.max(priceW, formatPrice(c.getBookRentalPrice()).length());
            authorW = Math.max(authorW, c.getAuthor().length());
            volumeW = Math.max(volumeW, String.valueOf(c.getVolume()).length());
        }
        String line = (numbered ? "+" + dashes(noW + 2) : "") + "+" + dashes(idW + 2) + "+" + dashes(titleW + 2)
                + "+" + dashes(priceW + 2) + "+" + dashes(authorW + 2) + "+" + dashes(volumeW + 2) + "+";
        String noFormat = numbered ? "| %" + noW + "s " : "";
        String format = "| %" + idW + "s | %-" + titleW + "s | %" + priceW + "s | %-" + authorW + "s | %" + volumeW + "s |%n";

        System.out.println(line);
        System.out.print(String.format(noFormat, "No."));
        System.out.printf(format, "ID", "Title", "Price", "Author", "Volume");
        System.out.println(line);
        int no = 1;
        for (ComicBook c : books) {
            System.out.print(String.format(noFormat, no++));
            System.out.printf(format, c.getId(), c.getTitle(), formatPrice(c.getBookRentalPrice()), c.getAuthor(), c.getVolume());
            if (numbered) {
                System.out.println(line);
            }
        }
        if (!numbered) {
            System.out.println(line);
        }
    }

    private static String formatPrice(double price) {
        return String.format(Locale.US, "$%.2f", price);
    }

    private static String dashes(int n) {
        return new String(new char[n]).replace('\0', '-');
    }

    private ComicBook findById(int id) {
        for (ComicBook c : comicList) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private ComicBook findById(String idStr) {
        if (idStr == null) {
            return null;
        }
        String trimmed = idStr.trim();
        for (ComicBook c : comicList) {
            if (String.valueOf(c.getId()).equalsIgnoreCase(trimmed)) {
                return c;
            }
        }
        try {
            int parsedId = Integer.parseInt(trimmed);
            return findById(parsedId);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Function 4: Update book rental price.
     * Displays inventory, prompts for ID until found, and updates only the rental
     * price (pressing Enter keeps the current price), then shows the updated list.
     */
    public void updateBookRentalPrice(Scanner sc) {
        if (comicList.isEmpty()) {
            System.out.println("The comic book list is empty. There is nothing to update.");
            return;
        }

        displayAll();

        ComicBook target = readExistingBook(sc, "update");

        System.out.println("Found comic book: \"" + target.getTitle() + "\" (ID: " + target.getId() + ").");

        double newPrice = 0; // stays 0 until Enter or a price from 0.01 to MAX_PRICE is entered
        do {
            System.out.print("Enter new rental price (current: "
                    + formatPrice(target.getBookRentalPrice()) + ", press Enter to keep): ");
            String pInput = sc.nextLine().trim();
            if (pInput.isEmpty()) {
                newPrice = target.getBookRentalPrice();
                System.out.println("The rental price remains unchanged.");
            } else if (!pInput.matches("\\d{1,7}(\\.\\d{1,2})?")) {
                System.out.println("\"" + pInput + "\" is not a valid rental price. Please enter a positive number with at most"
                        + " 2 decimal places, using a dot as the decimal separator (example: 7.50).");
            } else {
                double p = Double.parseDouble(pInput);
                if (p > 0 && p <= MAX_PRICE) {
                    newPrice = p;
                } else {
                    System.out.println("The rental price must be from 0.01 to "
                            + String.format(Locale.US, "%.2f", MAX_PRICE) + ". You entered " + pInput + ".");
                }
            }
        } while (newPrice == 0);

        target.setBookRentalPrice(newPrice);

        System.out.println("Comic book updated successfully.");
        displayAll();
    }

    /**
     * Function 5: Delete comic book. Displays inventory, prompts for ID,
     * asks for confirmation (Yes/No), and deletes if confirmed.
     */
    public void deleteComicBook(Scanner sc) {
        if (comicList.isEmpty()) {
            System.out.println("The comic book list is empty. There is nothing to delete.");
            return;
        }

        displayAll();

        ComicBook target = readExistingBook(sc, "delete");

        System.out.println("Found comic book: \"" + target.getTitle() + "\".");

        String confirm;
        do {
            System.out.print("Are you sure you want to delete this comic book? (Yes/No): ");
            confirm = sc.nextLine().trim().toLowerCase();
            if (!confirm.matches("yes|y|no|n")) {
                System.out.println("Unrecognized response. Please enter 'Yes' to delete or 'No' to cancel.");
            }
        } while (!confirm.matches("yes|y|no|n"));

        if (confirm.startsWith("y")) {
            comicList.remove(target);
            System.out.println("Comic book deleted successfully.");
            displayAll();
        } else {
            System.out.println("Deletion cancelled. Returning to the main menu.");
        }
    }

    // Asks for an ID until it matches a comic book; shared by functions 4 and 5
    private ComicBook readExistingBook(Scanner sc, String action) {
        ComicBook target = null;
        do {
            System.out.print("Enter comic book ID to " + action + ": ");
            String idInput = sc.nextLine().trim();
            if (idInput.isEmpty()) {
                System.out.println("The ID cannot be empty. Please enter an ID.");
            } else {
                target = findById(idInput);
                if (target == null) {
                    System.out.println("Comic book with ID \"" + idInput + "\" was not found. Please enter another ID.");
                }
            }
        } while (target == null);
        return target;
    }

    private int readInt(Scanner sc, String field, int max) {
        int n = 0; // stays 0 until a value from 1 to max is entered
        while (n == 0) {
            System.out.print("Enter " + field + ": ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println("The " + field + " cannot be empty. Please enter a whole number from 1 to " + max + ".");
            } else if (!s.matches("\\d{1,9}")) {
                System.out.println("\"" + s + "\" is not a valid " + field
                        + ". Please enter digits only (no letters, signs, spaces or decimal point), from 1 to " + max + ".");
            } else {
                int value = Integer.parseInt(s);
                if (value >= 1 && value <= max) {
                    n = value;
                } else {
                    System.out.println("The " + field + " must be from 1 to " + max + ". You entered " + value + ".");
                }
            }
        }
        return n;
    }

    private String readText(Scanner sc, String field, int maxLength, String pattern, String rule) {
        String result = null;
        while (result == null) {
            System.out.print("Enter " + field + ": ");
            String s = sc.nextLine().trim().replaceAll("\\s+", " ");
            if (s.isEmpty()) {
                System.out.println("The " + field + " cannot be empty. Please enter the " + field + " of the comic book.");
            } else if (s.length() > maxLength) {
                System.out.println("The " + field + " is too long (" + s.length() + " characters). Please enter at most "
                        + maxLength + " characters.");
            } else if (!s.matches(pattern)) {
                System.out.println("The " + field + " contains invalid characters. It may only contain " + rule + ".");
            } else {
                result = capitalizeWords(s);
            }
        }
        return result;
    }

    // "eiichiro oda" -> "Eiichiro Oda", "jean-luc" -> "Jean-Luc"; the other letters keep their case ("part II", "DC")
    private static String capitalizeWords(String s) {
        StringBuilder sb = new StringBuilder(s.trim());
        boolean newWord = true;
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if (ch == ' ' || ch == '-') {
                newWord = true;
            } else if (newWord) {
                sb.setCharAt(i, Character.toUpperCase(ch));
                newWord = false;
            }
        }
        return sb.toString();
    }

    private double readPrice(Scanner sc) {
        double price = 0; // stays 0 until a price from 0.01 to MAX_PRICE is entered
        while (price == 0) {
            System.out.print("Enter rental price: ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println("The rental price cannot be empty. Please enter a price from 0.01 to "
                        + String.format(Locale.US, "%.2f", MAX_PRICE) + ".");
            } else if (!s.matches("\\d{1,7}(\\.\\d{1,2})?")) {
                System.out.println("\"" + s + "\" is not a valid rental price. Please enter a positive number with at most"
                        + " 2 decimal places, using a dot as the decimal separator (example: 7.50).");
            } else {
                double value = Double.parseDouble(s);
                if (value > 0 && value <= MAX_PRICE) {
                    price = value;
                } else {
                    System.out.println("The rental price must be from 0.01 to "
                            + String.format(Locale.US, "%.2f", MAX_PRICE) + ". You entered " + s + ".");
                }
            }
        }
        return price;
    }

    /**
     * Saves the ArrayList to FILE_NAME using the same format as loadData().
     */
    public boolean saveData() {
        if (loadFailed) {
            System.out.println("The data file '" + FILE_NAME + "' was not overwritten because it could not be read at start.");
            return false;
        }
        File parent = new File(FILE_NAME).getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        // UTF-8, the same encoding Files.readAllLines() uses in loadData()
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(FILE_NAME)))) {
            pw.println(comicList.size());
            for (ComicBook c : comicList) {
                pw.println(c.getId());
                pw.println(c.getTitle());
                pw.printf(Locale.US, "%.2f%n", c.getBookRentalPrice());
                pw.println(c.getAuthor());
                pw.println(c.getVolume());
            }
            return !pw.checkError();
        } catch (Exception e) {
            System.out.println("Error writing data file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Function 6: About Us
     * Displays comprehensive project and team member information with updated assigned tasks.
     */
    public static void displayAboutUs() {
        System.out.println("\n+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                  ABOUT US - PROJECT INFO                                                   |");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-122s |%n", " Project       : Comic Book Rental Shop Management - Group Assignment");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|  1. University : FPT University Campus Can Tho                                                                             |");
        System.out.println("|  2. College    : Information Technology College - Software Engineering Department                                          |");
        System.out.println("|  3. Subject    : PRO192 - Object-Oriented Programming                                                                      |");
        System.out.println("|  4. Class      : SE2102                                                                                                    |");
        System.out.println("|  5. Mentor     : Vo Hong Khanh                                                                                             |");
        System.out.println("|  6. Group      : Group 05                                                                                                  |");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-122s |%n", " 7. Team members (Student ID - Full name - Role):");
        System.out.println("+-----+-----------+------------------------------+--------------+------------------------------------------------------------+");
        System.out.printf("| %-3s | %-9s | %-28s | %-12s | %-58s |%n", "No.", "ID", "Full Name", "Role", "Assigned Tasks");
        System.out.println("+-----+-----------+------------------------------+--------------+------------------------------------------------------------+");

        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |%n",
                1, "CE210032", "Ngo Thi Thao Huynh", "Leader", "Testing & Data, Function 4: Update Book Rental Price");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |%n",
                2, "CE210356", "Ngo Ngoc Anh Thu", "Secretary", "Class ComicBookManagement, Function 5: Delete Comic Book");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |%n",
                3, "CE210006", "Nguyen Huu Loc", "Member", "Function 3: Search Book by Author, Function 6: About Us");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |%n",
                4, "CE210031", "Le Ngoc Ngan", "Member", "Menu, Function 2: Search Book by Title");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |%n",
                5, "CE190701", "Dinh Minh Tien", "Member", "Class ComicBook, Function 1: Add New Comic Book");

        System.out.println("+-----+-----------+------------------------------+--------------+------------------------------------------------------------+");
        System.out.println();
    }

    public static void main(String[] args) {
        ComicBookManagement manager = new ComicBookManagement();
        manager.loadData();
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        // Input stream closed (Ctrl+D / Ctrl+Z): save and exit instead of crashing
        try {
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
                        manager.searchByTitle(sc);
                        break;
                    case 3:
                        manager.searchByAuthor(sc);
                        break;
                    case 4:
                        manager.updateBookRentalPrice(sc);
                        break;
                    case 5:
                        manager.deleteComicBook(sc);
                        break;
                    case 6:
                        displayAboutUs();
                        break;
                    case 7:
                        if (manager.saveData()) {
                            System.out.println("Data saved successfully. Exiting the program. Goodbye!");
                        } else {
                            System.out.println("Data could not be saved. Exiting the program. Goodbye!");
                        }
                        break;
                    default:
                        System.out.println("The number you entered is out of range. Please choose a number between 1 and 7!");
                        break;
                }
            } while (choice != 7);
        } catch (NoSuchElementException e) {
            System.out.println("\nNo more input. " + (manager.saveData() ? "Data saved." : "Data could not be saved.")
                    + " Exiting the program. Goodbye!");
        }

        sc.close();
    }

    private static int getValidChoice(Scanner sc) {
        Integer choice = null; // null until the input is a valid integer
        while (choice == null) {
            System.out.print("     Please select a function: ");
            String input = sc.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Input format error! You must enter a whole number from 1 to 7, not letters or special characters.");
            }
        }
        return choice;
    }
}
