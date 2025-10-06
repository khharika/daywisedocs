package Day27;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
interface SortingStrategy {
    void sort(List<String> items);
}

class AlphabeticalSorting implements Day26.SortingStrategy {
    @Override
    public void sort(List<String> items) {
        Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
    }
}
class LengthwiseSorting implements Day26.SortingStrategy {
    @Override
    public void sort(List<String> items) {
        Collections.sort(items, (a, b) -> Integer.compare(a.length(), b.length()));
    }
}
class SortingContext {
    private Day26.SortingStrategy strategy;
    private List<String> items = new ArrayList<>();

    public void setStrategy(Day26.SortingStrategy strategy) {
        this.strategy = strategy;
    }

    public void addItem(String item) {
        items.add(item);
    }

    public void removeItem(String item) {
        items.remove(item);
    }

    public void performSort() {
        if (strategy != null) {
            strategy.sort(items);
        } else {
            System.out.println("No sorting strategy set!");
        }
    }

    public List<String> getList() {
        return items;
    }
}

public class StrategyPatternDemo {
    public static void main(String[] args) {
        Day26.SortingContext context = new Day26.SortingContext();

        // Adding items
        context.addItem("Stanford");
        context.addItem("Ankit");
        context.addItem("Watson");

        // Alphabetical Sorting
        context.setStrategy(new Day26.AlphabeticalSorting());
        context.performSort();
        System.out.println("Alpha sorting:");
        for (String s : context.getList()) {
            System.out.println(s);
        }

        System.out.println();

        // Lengthwise Sorting
        context.setStrategy(new Day26.LengthwiseSorting());
        context.performSort();
        System.out.println("Lengthwise sorting:");
        for (String s : context.getList()) {
            System.out.println(s);
        }
    }
}
