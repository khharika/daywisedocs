import Day7.Task16_1;

public class Task19 {
    public static void main(String[] args) {
        System.out.println("Displaying all elements from Element enum:");

        for (Task16_1.Element element : Task16_1.Element.values()) {
            System.out.println("Symbol: " + element.name()
                    + ", Label: " + element.label
                    + ", Atomic Number: " + element.atomicNumber
                    + ", Atomic Weight: " + element.atomicWeight);
        }

        System.out.println("\nSearch by Label: " + Task16_1.Element.valueOfLabel("Helium"));
        System.out.println("Search by Atomic Number (2): " + Task16_1.Element.valueOfAtomicNumber(2));
        System.out.println("Search by Atomic Weight (4.0026f): " + Task16_1.Element.valueOfAtomicWeight(4.0026f));
    }
}
