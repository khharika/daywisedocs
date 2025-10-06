package Day28;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class DManager {
    private static Day26.DManager instance;
    private List<String> itemList;
    private DManager() {
        itemList = new ArrayList<>();
    }
    public static synchronized Day26.DManager getInstance() {
        if (instance == null) {
            instance = new Day26.DManager();
        }
        return instance;
    }
    public static Day26.DManager createInstance() {
        if (instance != null) {
            throw new IllegalStateException("Instance already exists!");
        }
        instance = new Day26.DManager();
        return instance;
    }
    public synchronized void addItem(String item) {
        itemList.add(item);
        System.out.println("Item added: " + item);
    }
    public synchronized boolean removeItem(String item) {
        boolean removed = itemList.remove(item);
        if (removed) {
            System.out.println("Item removed: " + item);
        }
        return removed;
    }
    public synchronized List<String> retrieveList() {
        return new ArrayList<>(itemList);
    }
}
class Singleton {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Day26.DManager manager = Day26.DManager.getInstance();
        System.out.println("enter items(type 'Done' to finish):");
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("Done")) {
                break;
            }
            manager.addItem(input);
        }
        System.out.println("item to be removed: ");
        String itemToRemove = scanner.nextLine();
        manager.removeItem(itemToRemove);
        for (String item : manager.retrieveList()) {
            System.out.println(item);
        }
        scanner.close();
        System.out.println("Updated list: " + manager.retrieveList());
    }
}