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

    // Declare ArrayList to store comic books and set the file path
    private ArrayList<ComicBook> comicList = new ArrayList<>();
    private String dataFilePath = "data/ComicBooks.txt";

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
     * Loads data from file into the ArrayList. File structure: - Line 1: Total number of comic books (N) - Every subsequent 5 lines represent 1 comic book: ID, Title, Price, Author, Volume
     */
    public void loadData() {
        File file = new File("ComicBooks.txt");
        if (file.exists()) {
            dataFilePath = "ComicBooks.txt";
        } else {
            file = new File("data/ComicBooks.txt");
            if (file.exists()) {
                dataFilePath = "data/ComicBooks.txt";
            } else {
                dataFilePath = "data/ComicBooks.txt";
                System.out.println("Data file 'ComicBooks.txt' not found. Starting with empty list.");
                return;
            }
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.println("Cannot read data file '" + dataFilePath + "': " + e.getMessage() + ". Starting with an empty list.");
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
            System.out.println("The first line of '" + dataFilePath + "' is not a valid number of comic books."
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
                    System.out.println("Comic book #" + (i + 1) + " in '" + dataFilePath + "' was skipped: ID " + id
                            + " is used more than once.");
                    continue;
                }
                comicList.add(new ComicBook(id, title, price, author, volume));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Comic book #" + (i + 1) + " in '" + dataFilePath + "' (lines " + (start + 1) + " to "
                        + (start + 5) + ") is missing or invalid and was skipped.");
            }
        }
        System.out.println("Loaded " + comicList.size() + " comic book(s) from '" + dataFilePath + "'.");
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
                    "letters, digits, spaces and common punctuation (. , : ' - ! ? & / ( ) # + ; \") and must start with a letter or digit (example: One Piece)");
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
        if (query.isEmpty()) {
        System.out.println("Search query cannot be empty.");
        return; 
    }
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

    /**
     * Function 3: Search book by author.
     */
    public void searchByAuthor(Scanner sc) {
        System.out.print("Enter author name to search: ");
        String query = sc.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            return;
        }
        
        boolean isFound = false;
        int count = 0;

        for (ComicBook c : comicList) {
            // Using equalsIgnoreCase as required by the assignment rule for author search
            if (c.getAuthor().equalsIgnoreCase(query)) {
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
            System.out.println("Found " + count + " comic book(s) matching the author.");
        } else {
            System.out.println("No comic book found with the given author.");
        }
    }
    
    private boolean isDuplicate(int excludeId, String title, String author, int volume) {
        for (ComicBook c : comicList) {
            if (c.getId() != excludeId
                    && c.getTitle().equalsIgnoreCase(title)
                    && c.getAuthor().equalsIgnoreCase(author)
                    && c.getVolume() == volume) {
                return true;
            }
        }
        return false;
    }

    private boolean isDuplicate(String title, String author, int volume) {
        return isDuplicate(-1, title, author, volume);
    }

    // Column widths match the input limits (TITLE_MAX, AUTHOR_MAX, MAX_NUMBER, MAX_PRICE)
    public void displayAll() {
        String line = "+-----+------+--------------------------------+------------+---------------------------+--------+";
        System.out.println(line);
        System.out.printf("| %3s | %4s | %-30s | %10s | %-25s | %6s |%n", "No.", "ID", "Title", "Price", "Author", "Volume");
        int no = 1;
        for (ComicBook c : comicList) {
            System.out.println(line);
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
     * Function 4: Update book rental price and information.
     * Displays inventory, prompts for ID until found, allows updating Title,
     * Rental Price, Author, and Volume (or pressing Enter to keep current values),
     * checks for duplicates, and shows the updated list.
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

        System.out.println("Found comic book: \"" + target.getTitle() + "\" (ID: " + target.getId() + ")");
        System.out.println("Please enter new information (press Enter to keep current value):");

        String newTitle;
        double newPrice;
        String newAuthor;
        int newVolume;

        while (true) {
            // 1. Update Title
            while (true) {
                System.out.print("Enter new title (current: \"" + target.getTitle() + "\", press Enter to keep): ");
                String tInput = sc.nextLine().trim().replaceAll("\\s+", " ");
                if (tInput.isEmpty()) {
                    newTitle = target.getTitle();
                    break;
                }
                if (tInput.length() > TITLE_MAX) {
                    System.out.println("The title is too long (" + tInput.length() + " characters). Please enter at most "
                            + TITLE_MAX + " characters.");
                    continue;
                }
                if (!tInput.matches(TITLE_PATTERN)) {
                    System.out.println("The title contains invalid characters. Please enter a valid title.");
                    continue;
                }
                newTitle = capitalizeWords(tInput);
                break;
            }

            // 2. Update Rental Price
            while (true) {
                System.out.print("Enter new rental price (current: "
                        + String.format(Locale.US, "$%.2f", target.getBookRentalPrice()) + ", press Enter to keep): ");
                String pInput = sc.nextLine().trim();
                if (pInput.isEmpty()) {
                    newPrice = target.getBookRentalPrice();
                    break;
                }
                if (!pInput.matches("\\d{1,7}(\\.\\d{1,2})?")) {
                    System.out.println("\"" + pInput + "\" is not a valid rental price. Please enter a positive number with at most 2 decimal places, using a dot as the decimal separator (example: 7.50).");
                    continue;
                }
                try {
                    double p = Double.parseDouble(pInput);
                    if (p <= 0 || p > MAX_PRICE) {
                        System.out.println("The rental price must be from 0.01 to "
                                + String.format(Locale.US, "%.2f", MAX_PRICE) + ". You entered " + pInput + ".");
                        continue;
                    }
                    newPrice = p;
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("\"" + pInput + "\" is not a valid rental price. Please enter a valid number.");
                }
            }

            // 3. Update Author
            while (true) {
                System.out.print("Enter new author (current: \"" + target.getAuthor() + "\", press Enter to keep): ");
                String aInput = sc.nextLine().trim().replaceAll("\\s+", " ");
                if (aInput.isEmpty()) {
                    newAuthor = target.getAuthor();
                    break;
                }
                if (aInput.length() > AUTHOR_MAX) {
                    System.out.println("The author is too long (" + aInput.length() + " characters). Please enter at most "
                            + AUTHOR_MAX + " characters.");
                    continue;
                }
                if (!aInput.matches(AUTHOR_PATTERN)) {
                    System.out.println("The author contains invalid characters. Please enter a valid author name.");
                    continue;
                }
                newAuthor = capitalizeWords(aInput);
                break;
            }

            // 4. Update Volume
            while (true) {
                System.out.print("Enter new volume (current: " + target.getVolume() + ", press Enter to keep): ");
                String vInput = sc.nextLine().trim();
                if (vInput.isEmpty()) {
                    newVolume = target.getVolume();
                    break;
                }
                if (!vInput.matches("\\d{1,9}")) {
                    System.out.println("\"" + vInput + "\" is not a valid volume. Please enter digits only, from 1 to " + MAX_NUMBER + ".");
                    continue;
                }
                int v = Integer.parseInt(vInput);
                if (v < 1 || v > MAX_NUMBER) {
                    System.out.println("The volume must be from 1 to " + MAX_NUMBER + ". You entered " + v + ".");
                    continue;
                }
                newVolume = v;
                break;
            }

            // Check duplicate with another book
            if (isDuplicate(target.getId(), newTitle, newAuthor, newVolume)) {
                System.out.println("The book \"" + newTitle + "\" by " + newAuthor + ", volume " + newVolume
                        + " already exists in another record. Please re-enter the information.");
                continue;
            }
            break;
        }

        target.setTitle(newTitle);
        target.setBookRentalPrice(newPrice);
        target.setAuthor(newAuthor);
        target.setVolume(newVolume);

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

        ComicBook target = null;
        while (true) {
            System.out.print("Enter comic book ID to delete: ");
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

        System.out.println("Found comic book: \"" + target.getTitle() + "\".");
        
        while (true) {
            System.out.print("Are you sure you want to delete this comic book? (Yes/No): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            
            if (confirm.equals("yes") || confirm.equals("y")) {
                comicList.remove(target);
                System.out.println("Comic book deleted successfully.");
                displayAll();
                break;
            } else if (confirm.equals("no") || confirm.equals("n")) {
                System.out.println("Deletion cancelled. Returning to Menu.");
                break;
            } else {
                System.out.println("Unrecognized response. Please enter 'Yes' to delete or 'No' to cancel.");
            }
        }
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
     * Saves the ArrayList to dataFilePath using the same format as loadData().
     */
    public void saveData() {
        File targetFile = new File(dataFilePath);
        File parent = targetFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (PrintWriter pw = new PrintWriter(targetFile)) {
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
     * Displays comprehensive project and team member information with updated assigned tasks.
     */
    public static void displayAboutUs() {
        System.out.println("\n+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                  ABOUT US - PROJECT INFO                                                   |");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-122s |%n", " Project       : Comic Book Rental Shop Management - Group Assignment");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("|  %-122s |%n", "1. University : FPT University Campus Can Tho");
        System.out.printf("|  %-122s |%n", "2. College    : Information Technology College - Software Engineering Department");
        System.out.printf("|  %-122s |%n", "3. Subject    : PRO192 - Object-Oriented Programming (CEA201 in prompt)");
        System.out.printf("|  %-122s |%n", "4. Class      : SE2102");
        System.out.printf("|  %-122s |%n", "5. Mentor     : Vo Hong Khanh");
        System.out.printf("|  %-122s |%n", "6. Group      : Group 05");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                        TEAM MEMBERS                                                        |");
        System.out.println("+-----+-----------+------------------------------+--------------+------------------------------------------------------------+");
        System.out.printf("| %-3s | %-9s | %-28s | %-12s | %-58s |\n", "No.", "ID", "Full Name", "Role", "Assigned Tasks");
        System.out.println("+-----+-----------+------------------------------+--------------+------------------------------------------------------------+");

        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |\n",
                1, "CE210032", "Ngo Thi Thao Huynh", "Leader", "Testing & Data, Function 4: Update Book Rental Price");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |\n",
                2, "CE210356", "Ngo Ngoc Anh Thu", "Secretary", "Class ComicBookManagement, Function 5: Delete Comic Book");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |\n",
                3, "CE210006", "Nguyen Huu Loc", "Member", "Function 3: Search Book by Author, Function 6: About Us");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |\n",
                4, "CE210031", "Le Ngoc Ngan", "Member", "Menu, Function 2: Search Book by Title");
        System.out.printf("| %-3d | %-9s | %-28s | %-12s | %-58s |\n",
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
