package PracticeSet.atlaslearnings.day11;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task12 {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Harika", "ramya", "shaik", "Ayaz");
        int count = (int)(names.stream().filter(x->x.startsWith("A")).count());
        System.out.println(count);

        List<String> fullName = new ArrayList<>();
        fullName.add("Harika, kantipudi");
        fullName.add("ramya, kasu");
        fullName.add("shaik ayaz");
        fullName.add("raaga, surendra");
        fullName.add("bhanu, sree");
        fullName.add("kavya, somayaji");


        System.out.println(fullName);
        fullName.stream().filter(x->x.startsWith("H")).sorted().map(String::toUpperCase).forEach(System.out::println);

    }
}
