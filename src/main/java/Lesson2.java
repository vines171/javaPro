import lesson_2.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class Lesson2 {
    public static void main(String[] args) {
        List<String> testData = Arrays.asList(null, "ABC", "ABC", "QQ", "");
        List<String> testData2 = Arrays.asList("ABC", "CDE", "EE");
        List<String> testData3 = Arrays.asList("ABC", "CDEF", "EE");
        List<Integer> testData4 = Arrays.asList(5, 2, 10, 9, 4, 3, 10, 1, 13);
        String line = "У лукоморья дуб зелёный;\n" +
                "Златая цепь на дубе том:\n" +
                "И днём и ночью кот учёный\n" +
                "Всё ходит по цепи кругом;";

        List<Employee> employees = Arrays.asList(
                new Employee("Иван", 66, "Инженер"),
                new Employee("Мария", 35, "Инженер"),
                new Employee("Петр", 45, "Менеджер"),
                new Employee("Анна", 72, "Инженер"),
                new Employee("Сергей", 80, "Инженер"),
                new Employee("Ольга", 23, "Инженер"),
                new Employee("Дмитрий", 50, "Вахтер"),
                new Employee("Екатерина", 78, "Аналитик")
        );



//        System.out.println("Результат задания 1" + getListNotNullNotDuplicate(testData));
//        System.out.println("Результат задания 2 " + countUniqueLetters(testData2));
//        System.out.println("Результат задания 3 " + getLongestWord(testData3));
//        System.out.println("Результат задания 4 " + getThirdLargest(testData4));
//        System.out.println("Результат задания 5 " + get3OldEngineers(employees));
//        System.out.println("Результат задания 6 " + getAverageEngineerAge(employees));
        System.out.println("Результат задания 7 " + getMapWordsByLength(line));


    }
    public static List<String> getListNotNullNotDuplicate(List<String> inputList){
        return inputList.stream()
                .filter(str -> str != null && !str.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public static long countUniqueLetters(List<String> strings) {
//        return strings.stream()
//                .filter(str -> str != null && !str.isEmpty())
//                .map(String::toUpperCase)
//                .flatMapToInt(String::chars)
//                .filter(Character::isLetter)
//                .mapToObj(ch -> (char) ch)
//                .distinct()
//                .count();
        return strings.stream()
                .filter(str -> str != null) // Фильтруем null строки
                .flatMap(str -> str.chars() // Преобразуем каждую строку в поток символов
                        .filter(Character::isLetter) // Фильтруем только буквы
                        .mapToObj(ch -> (char) ch)) // Преобразуем int в char
                .map(Character::toUpperCase) // Приводим к верхнему регистру
                .distinct() // Оставляем только уникальные буквы
                .count(); // Считаем количество
    }

    public static String getLongestWord(List<String> words) {
        return words.stream()
                .filter(word -> word != null && !word.isEmpty())
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }

    public static Integer getThirdLargest(List<Integer> numbers) {
        return numbers.stream()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .orElse(null);
    }

    public static List<String> get3OldEngineers(List<Employee> employees) {
        return employees.stream()
                .filter(Objects::nonNull)
                .filter(emp -> "Инженер".equals(emp.getPosition()))
                .sorted(Comparator.comparingInt(Employee::getAge).reversed())
                .limit(3)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public static double getAverageEngineerAge(List<Employee> employees) {
        return employees.stream()
                .filter(emp -> emp != null && "Инженер".equals(emp.getPosition()))
                .mapToInt(Employee::getAge)
                .average()
                .orElse(0.0);
    }

    public static Map<Integer, List<String>> getMapWordsByLength(String line) {
        if (line == null) {
            return Map.of();
        }
        return Arrays.stream(line.split("[\\s.,!?]+"))
                .map(word -> word.replaceAll("[^\\p{L}]", "")) // Убираем все не-буквы
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(String::length));
    }
    public static List<String> getFindLongestWords(List<String> line) {
        if (line == null) {
            return List.of();
        }

        List<String> allWords = line.stream()
                .filter(sentence -> sentence != null && !sentence.isEmpty())
                .map(word -> word.replaceAll("[^\\p{L}]", ""))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());

        if (allWords.isEmpty()) {
            return List.of();
        }

        int maxLength = allWords.stream()
                .mapToInt(String::length)
                .max()
                .getAsInt();

        return allWords.stream()
                .filter(word -> word.length() == maxLength)
                .collect(Collectors.toList());
    }

}
