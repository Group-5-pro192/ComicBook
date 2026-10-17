package comicbookmanagement;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ComicBookManagement {

    // Declare ArrayList to store comic books and set the constant file name
    private ArrayList<ComicBook> comicList = new ArrayList<>();
    private final String FILE_NAME = "data/ComicBooks.txt";

    // Input limits, chosen so every value fits its column in displayAll()
    private static final int MAX_NUMBER = 9999;
    private static final double MAX_PRICE = 1000.00;
    private static final int TITLE_MAX = 30;
    private static final int AUTHOR_MAX = 25;
    private static final String TITLE_PATTERN = "[A-Za-z0-9][A-Za-z0-9 .,:'!?&-]*";
    private static final String AUTHOR_PATTERN = "[A-Za-z][A-Za-z .'-]*";

    public ComicBookManagement() {
    }

    // Getter for team members to access the comic book list
    public ArrayList<ComicBook> getComicList() {
        return comicList;
    }

    /**
     * Loads data from FILE_NAME into the ArrayList. File structure: - Line 1: Total number of comic books (N) - Every subsequent 5 lines represent 1 comic book: ID, Title, Price, Author, Volume
     */
    public void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("Data file '" + FILE_NAME + "' not found. Starting with empty list.");
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.println("Cannot read data file '" + FILE_NAME + "': " + e.getMessage() + ". Starting with an empty list.");
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
        int id;
        while (true) {
            id = readInt(sc, "ID", MAX_NUMBER);
            if (findById(id) == null) {
                break;
            }
            System.out.println("ID " + id + " is already used by another comic book. Please enter a different ID.");
        }
        String title, author;
        int volume;
        while (true) {
            title = readText(sc, "title", TITLE_MAX, TITLE_PATTERN,
                    "letters, digits, spaces and . , : ' - ! ? & and must start with a letter or digit (example: One Piece)");
            author = readText(sc, "author", AUTHOR_MAX, AUTHOR_PATTERN,
                    "English letters, spaces and . ' - and must start with a letter (example: Eiichiro Oda)");
            volume = readInt(sc, "volume", MAX_NUMBER);
            if (!isDuplicate(title, author, volume)) {
                break;
            }
            System.out.println("The book \"" + title + "\" by " + author + ", volume " + volume
                    + " already exists. Please enter the title, author and volume again.");
        }
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
        String query = sc.nextLine().toLowerCase().trim();
        boolean isFound = false;
        int count = 0;

        for (ComicBook c : comicList) {
            if (c.getTitle().toLowerCase().contains(query)) {
                if (!isFound) {
                    String line = "+------+--------------------------------+------------+---------------------------+--------+";
                    System.out.println(line);
                    System.out.printf("| %4s | %-30s | %10s | %-25s | %6s |%n", "ID", "Title", "Price", "Author", "Volume");
                    System.out.println(line);
                    isFound = true;
                }
                System.out.printf(Locale.US, "| %4d | %-30s | %10s | %-25s | %6d |%n",
                        c.getId(), c.getTitle(), String.format(Locale.US, "$%.2f", c.getBookRentalPrice()), c.getAuthor(), c.getVolume());
                count++;
            }
        }

        if (isFound) {
            System.out.println("+------+--------------------------------+------------+---------------------------+--------+");
            System.out.println("Found " + count + " comic book(s) matching your search.");
        } else {
            System.out.println("No comic book found with the given title.");
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

    // Column widths match the input limits (TITLE_MAX, AUTHOR_MAX, MAX_NUMBER, MAX_PRICE)
    public void displayAll() {
        String line = "+-----+------+--------------------------------+------------+---------------------------+--------+";
        System.out.println(line);
        System.out.printf("| %3s | %4s | %-30s | %10s | %-25s | %6s |%n", "No.", "ID", "Title", "Price", "Author", "Volume");
        System.out.println(line);
        int no = 1;
        for (ComicBook c : comicList) {
            System.out.printf(Locale.US, "| %3d | %4d | %-30s | %10s | %-25s | %6d |%n",
                    no++, c.getId(), c.getTitle(), String.format(Locale.US, "$%.2f", c.getBookRentalPrice()), c.getAuthor(), c.getVolume());
        }
        System.out.println(line);
        System.out.println("Total: " + comicList.size() + " comic book(s).");
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
     * Function 4: Update book rental price. Displays inventory, prompts for ID until found, allows price update or Enter to keep, and shows updated list.
     */
    public void updateBookRentalPrice(Scanner sc) {
        if (comicList.isEmpty()) {
            System.out.println("The comic book list is empty. There is nothing to update.");
            return;
        }

        displayAll();

        ComicBook target = null;
        while (true) {
            System.out.print("Enter comic book ID to update: ");
            String idInput = sc.nextLine().trim();
            if (idInput.isEmpty()) {
                System.out.println("The ID cannot be empty. Please enter an ID.");
                continue;
            }
            target = findById(idInput);
            if (target != null) {
                break;
            }
            System.out.println("Comic book with ID \"" + idInput + "\" was not found. Please enter again.");
        }

        System.out.println("Found comic book: \"" + target.getTitle() + "\" (Current rental price: "
                + String.format(Locale.US, "$%.2f", target.getBookRentalPrice()) + ")");

        while (true) {
            System.out.print("Enter new rental price (press Enter to keep current price): ");
            String priceInput = sc.nextLine().trim();
            if (priceInput.isEmpty()) {
                System.out.println("No new price entered. Keeping current rental price.");
                break;
            }
            if (!priceInput.matches("\\d{1,7}(\\.\\d{1,2})?")) {
                System.out.println("\"" + priceInput + "\" is not a valid rental price. Please enter a positive number with at most 2 decimal places, using a dot as the decimal separator (example: 7.50).");
                continue;
            }
            try {
                double newPrice = Double.parseDouble(priceInput);
                if (newPrice <= 0 || newPrice > MAX_PRICE) {
                    System.out.println("The rental price must be from 0.01 to "
                            + String.format(Locale.US, "%.2f", MAX_PRICE) + ". You entered " + priceInput + ".");
                    continue;
                }
                target.setBookRentalPrice(newPrice);
                break;
            } catch (NumberFormatException e) {
                System.out.println("\"" + priceInput + "\" is not a valid rental price. Please enter a valid number.");
            }
        }

        System.out.println("Comic book rental price updated successfully.");
        displayAll();
    }

    private int readInt(Scanner sc, String field, int max) {
        while (true) {
            System.out.print("Enter " + field + ": ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println("The " + field + " cannot be empty. Please enter a whole number from 1 to " + max + ".");
            } else if (!s.matches("\\d{1,9}")) {
                System.out.println("\"" + s + "\" is not a valid " + field
                        + ". Please enter digits only (no letters, signs, spaces or decimal point), from 1 to " + max + ".");
            } else {
                int n = Integer.parseInt(s);
                if (n >= 1 && n <= max) {
                    return n;
                }
                System.out.println("The " + field + " must be from 1 to " + max + ". You entered " + n + ".");
            }
        }
    }

    private String readText(Scanner sc, String field, int maxLength, String pattern, String rule) {
        while (true) {
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
                return capitalizeWords(s);
            }
        }
    }

    // "eiichiro oda" -> "Eiichiro Oda", "jean-luc" -> "Jean-Luc"
    private static String capitalizeWords(String s) {
        StringBuilder sb = new StringBuilder(s.toLowerCase());
        for (int i = 0; i < sb.length(); i++) {
            if (i == 0 || sb.charAt(i - 1) == ' ' || sb.charAt(i - 1) == '-') {
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
            }
        }
        return sb.toString();
    }

    private double readPrice(Scanner sc) {
        while (true) {
            System.out.print("Enter rental price: ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println("The rental price cannot be empty. Please enter a price from 0.01 to "
                        + String.format(Locale.US, "%.2f", MAX_PRICE) + ".");
            } else if (!s.matches("\\d{1,7}(\\.\\d{1,2})?")) {
                System.out.println("\"" + s + "\" is not a valid rental price. Please enter a positive number with at most"
                        + " 2 decimal places, using a dot as the decimal separator (example: 7.50).");
            } else {
                double price = Double.parseDouble(s);
                if (price > 0 && price <= MAX_PRICE) {
                    return price;
                }
                System.out.println("The rental price must be from 0.01 to "
                        + String.format(Locale.US, "%.2f", MAX_PRICE) + ". You entered " + s + ".");
            }
        }
    }

    /**
     * Saves the ArrayList to FILE_NAME using the same format as loadData().
     */
    public void saveData() {
        File parent = new File(FILE_NAME).getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
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
     * Function 6: About Us Displays comprehensive project and team member information.
     */
    public static void displayAboutUs() {
        System.out.println("\n+---------------------------------------------------------------------------------------------------+");
        System.out.println("|                                        ABOUT US - PROJECT INFO                                    |");
        System.out.println("+---------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-97s |%n", " Project       : Comic Book Rental Shop Management - Group Assignment");
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
                        break;
                    case 4:
                        manager.updateBookRentalPrice(sc);
                        break;
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
        } catch (NoSuchElementException e) {
            manager.saveData();
            System.out.println("\nNo more input. Data saved. Exiting the program. Goodbye!");
        }

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
