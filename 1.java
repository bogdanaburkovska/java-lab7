import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {

    static class Exhibition {
        private String name;
        private String artistLastName;
        private String day;
        private int visitorsCount;
        private String comments;

        public Exhibition() {}

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

        public void setName(String name) {
            this.name = name;
        }

        public String getArtistLastName() {
            return artistLastName;
        }

        public void setArtistLastName(String artistLastName) {
            this.artistLastName = artistLastName;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getVisitorsCount() {
            return visitorsCount;
        }

        public void setVisitorsCount(int visitorsCount) {
            this.visitorsCount = visitorsCount;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        @Override
        public String toString() {
            return String.format("%-20s %-15s %-10s %-10d %-20s", name, artistLastName, day, visitorsCount, comments);
        }

        public static String getTableHeader() {
            return String.format("%-20s %-15s %-10s %-10s %-20s",
                    "Назва", "Прізвище художника", "День", "Кількість відвідувачів", "Коментарі");
        }
    }

    static class ExhibitionDatabase {
        private List<Exhibition> exhibitions;

        public ExhibitionDatabase() {
            exhibitions = new ArrayList<>();
        }

        public void addExhibition(Exhibition exhibition) {
            exhibitions.add(exhibition);
            saveToFile();
        }

        public List<Exhibition> getExhibitions() {
            return exhibitions;
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
            for (int i = 0; i < exhibitions.size(); i++) {
                if (exhibitions.get(i).getName().equalsIgnoreCase(name)) {
                    exhibitions.remove(i);
                    saveToFile();
                    return;
                }
            }
            System.out.println("Виставка з такою назвою не знайдена.");
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

        public void sortExhibitionsByParameter(String parameter) {
            switch (parameter.toLowerCase()) {
                case "назва":
                    Collections.sort(exhibitions, Comparator.comparing(Exhibition::getName));
                    break;
                case "прізвище художника":
                    Collections.sort(exhibitions, Comparator.comparing(Exhibition::getArtistLastName));
                    break;
                case "день":
                    Collections.sort(exhibitions, Comparator.comparing(Exhibition::getDay));
                    break;
                default:
                    System.out.println("Невідомий параметр для сортування.");
            }
        }

        public void totalVisitors() {
            int total = exhibitions.stream().mapToInt(Exhibition::getVisitorsCount).sum();
            System.out.println("Сумарна кількість відвідувачів: " + total);
        }

        public void dayWithLeastVisitors() {
            Exhibition min = Collections.min(exhibitions, Comparator.comparingInt(Exhibition::getVisitorsCount));
            System.out.println("День з найменшою кількістю відвідувачів: " + min.getDay() + " (" + min.getVisitorsCount() + ")");
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

    public static void main(String[] args) {
        try {
            // Налаштування System.out для використання UTF-8
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
            System.out.println("o. Сортування виставок");
            System.out.println("t. Загальна кількість відвідувачів");
            System.out.println("m. День з найменшою кількістю відвідувачів");
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
                case "o":
                    sortExhibitions(scanner, exhibitionDatabase);
                    break;
                case "t":
                    exhibitionDatabase.totalVisitors();
                    break;
                case "m":
                    exhibitionDatabase.dayWithLeastVisitors();
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
        System.out.print("Введіть назву для пошуку: ");
        String name = scanner.nextLine();

        Exhibition foundExhibition = exhibitionDatabase.searchExhibitionByName(name);
        if (foundExhibition != null) {
            System.out.println("Знайдено виставку: " + foundExhibition);
        } else {
            System.out.println("Виставка з такою назвою не знайдена.");
        }
    }

    private static void sortExhibitions(Scanner scanner, ExhibitionDatabase exhibitionDatabase) {
        System.out.print("Сортувати за полем (назва/прізвище художника/день): ");
        String parameter = scanner.nextLine();
        exhibitionDatabase.sortExhibitionsByParameter(parameter);
        exhibitionDatabase.displayExhibitions();
    }

    private static void searchComments(Scanner scanner, ExhibitionDatabase exhibitionDatabase) {
        System.out.print("Введіть слово для пошуку у коментарях: ");
        String word = scanner.nextLine();
        exhibitionDatabase.searchCommentsByWord(word);
    }
}
