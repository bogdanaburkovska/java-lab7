import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

abstract class ExhibitionRecord {
    public abstract void displayAdditionalInfo();
}

class Exhibition extends ExhibitionRecord {
    private String name;
    private String artistLastName;
    private String day;
    private int visitorsCount;
    private String comments;

    public Exhibition(String name, String artistLastName, String day, int visitorsCount, String comments) {
        this.name = name;
        this.artistLastName = artistLastName;
        this.day = day;
        this.visitorsCount = visitorsCount;
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public String getArtistLastName() {
        return artistLastName;
    }

    public String getDay() {
        return day;
    }

    public int getVisitorsCount() {
        return visitorsCount;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-15s %-10s %-10d %-20s", name, artistLastName, day, visitorsCount, comments);
    }

    public static String getTableHeader() {
        return String.format("%-20s %-15s %-10s %-10s %-20s", "Назва", "Прізвище художника", "День", "Кількість відвідувачів", "Коментарі");
    }

    @Override
    public void displayAdditionalInfo() {
        System.out.println("Сумарна кількість відвідувачів: " + getVisitorsCount());
        System.out.println("День з найменшою кількістю відвідувачів: " + getDay());
    }
}

class ExhibitionDatabase {
    private List<Exhibition> exhibitions;

    public ExhibitionDatabase() {
        exhibitions = new ArrayList<>();
    }

    public void addExhibition(Exhibition exhibition) {
        exhibitions.add(exhibition);
        saveToFile();
    }

    public void editExhibition(String name, Exhibition newExhibition) {
        for (int i = 0; i < exhibitions.size(); i++) {
            if (exhibitions.get(i).getName().equalsIgnoreCase(name)) {
                exhibitions.set(i, newExhibition);
                saveToFile();
                return;
            }
        }
        System.out.println("Виставка з такою назвою не знайдена.");
    }

    public void deleteExhibition(String name) {
        exhibitions.removeIf(exhibition -> exhibition.getName().equalsIgnoreCase(name));
        saveToFile();
    }

    public void displayExhibitions() {
        System.out.println(Exhibition.getTableHeader());
        System.out.println("-------------------- --------------- ---------- ---------- --------------------");
        for (Exhibition exhibition : exhibitions) {
            System.out.println(exhibition);
        }
    }

    public Exhibition searchExhibitionByName(String name) {
        for (Exhibition exhibition : exhibitions) {
            if (exhibition.getName().equalsIgnoreCase(name)) {
                return exhibition;
            }
        }
        return null;
    }

    public void searchCommentsByWord(String word) {
        System.out.println("Список коментарів, що містять слово \"" + word + "\":");
        exhibitions.stream()
                .filter(e -> e.getComments().contains(word))
                .forEach(e -> System.out.println(e.getComments()));
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("exhibitions.txt"), StandardCharsets.UTF_8))) {
            for (Exhibition exhibition : exhibitions) {
                writer.println(exhibition.getName() + "," + exhibition.getArtistLastName() + "," +
                        exhibition.getDay() + "," + exhibition.getVisitorsCount() + "," + exhibition.getComments());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        File file = new File("exhibitions.txt");
        if (!file.exists()) {
            System.out.println("Файл exhibitions.txt не знайдено. Створюється новий файл.");
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Помилка при створенні файлу: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    System.err.println("Невірний формат рядка: " + line);
                    continue;
                }
                String name = parts[0];
                String artistLastName = parts[1];
                String day = parts[2];
                int visitorsCount = Integer.parseInt(parts[3]);
                String comments = parts[4];
                Exhibition exhibition = new Exhibition(name, artistLastName, day, visitorsCount, comments);
                exhibitions.add(exhibition);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in, "UTF-8");
        ExhibitionDatabase exhibitionDatabase = new ExhibitionDatabase();
        exhibitionDatabase.loadFromFile();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("a. Додати виставку");
            System.out.println("e. Редагувати виставку");
            System.out.println("d. Видалити виставку");
            System.out.println("s. Показати всі виставки");
            System.out.println("f. Пошук виставки за назвою");
            System.out.println("c. Пошук коментарів за словом");
            System.out.println("q. Вихід");
            System.out.print("Оберіть опцію: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "a":
                    addExhibition(scanner, exhibitionDatabase);
                    break;
                case "e":
                    editExhibition(scanner, exhibitionDatabase);
                    break;
                case "d":
                    deleteExhibition(scanner, exhibitionDatabase);
                    break;
                case "s":
                    exhibitionDatabase.displayExhibitions();
                    break;
                case "f":
                    searchExhibition(scanner, exhibitionDatabase);
                    break;
                case "c":
                    searchComments(scanner, exhibitionDatabase);
                    break;
                case "q":
                    System.out.println("Завершення програми.");
                    return;
                default:
                    System.out.println("Невірний вибір.");
            }
        }
    }

    private static void addExhibition(Scanner scanner, ExhibitionDatabase exhibitionDatabase) {
        System.out.print("Назва: ");
        String name = scanner.nextLine();

        System.out.print("Прізвище художника: ");
        String artistLastName = scanner.nextLine();

        System.out.print("День: ");
        String day = scanner.nextLine();

        System.out.print("Кількість відвідувачів: ");
        int visitorsCount = Integer.parseInt(scanner.nextLine());

        System.out.print("Коментарі: ");
        String comments = scanner.nextLine();

        Exhibition exhibition = new Exhibition(name, artistLastName, day, visitorsCount, comments);
        exhibitionDatabase.addExhibition(exhibition);
        System.out.println("Виставку успішно додано.");
    }

    private static void editExhibition(Scanner scanner, ExhibitionDatabase exhibitionDatabase) {
        System.out.print("Введіть назву виставки, яку потрібно редагувати: ");
        String name = scanner.nextLine();

        Exhibition existingExhibition = exhibitionDatabase.searchExhibitionByName(name);
        if (existingExhibition != null) {
            System.out.print("Нова назва: ");
            String newName = scanner.nextLine();

            System.out.print("Нове прізвище художника: ");
            String newArtistLastName = scanner.nextLine();

            System.out.print("Новий день: ");
            String newDay = scanner.nextLine();

            System.out.print("Нова кількість відвідувачів: ");
            int newVisitorsCount = Integer.parseInt(scanner.nextLine());

            System.out.print("Нові коментарі: ");
            String newComments = scanner.nextLine();

            Exhibition newExhibition = new Exhibition(newName, newArtistLastName, newDay, newVisitorsCount, newComments);
            exhibitionDatabase.editExhibition(name, newExhibition);
            System.out.println("Виставку успішно відредаговано.");
        } else {
            System.out.println("Виставка з такою назвою не знайдена.");
        }
    }

    private static void deleteExhibition(Scanner scanner, ExhibitionDatabase exhibitionDatabase) {
        System.out.print("Введіть назву виставки, яку потрібно видалити: ");
        String name = scanner.nextLine();
        exhibitionDatabase.deleteExhibition(name);
        System.out.println("Виставку успішно видалено.");
    }

    private static void searchExhibition(Scanner scanner, ExhibitionDatabase exhibitionDatabase) {
        System.out.print("Введіть назву виставки для пошуку: ");
        String name = scanner.nextLine();
        Exhibition exhibition = exhibitionDatabase.searchExhibitionByName(name);
        if (exhibition != null) {
            System.out.println("Знайдено виставку:");
            System.out.println(Exhibition.getTableHeader());
            System.out.println("-------------------- --------------- ---------- ---------- --------------------");
            System.out.println(exhibition);
            exhibition.displayAdditionalInfo();
        } else {
            System.out.println("Виставка з такою назвою не знайдена.");
        }
    }

    private static void searchComments(Scanner scanner, ExhibitionDatabase exhibitionDatabase) {
        System.out.print("Введіть слово для пошуку в коментарях: ");
        String word = scanner.nextLine();
        exhibitionDatabase.searchCommentsByWord(word);
    }
}
