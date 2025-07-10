package Day7;
public class Task20 {
    public static void main(String[] args) {
        // Declare and initialize the array with your name
        char[] Name = { 'H', 'A', 'R', 'I', 'K', 'A' };

        // Print the array as a string
        System.out.println(Name);  // This automatically prints the full name

        // Get the length of the name
        int n = Name.length;
        System.out.println("There are " + n + " letters in my name");

        // Print each character using a loop
        System.out.print("Letters in my name: ");
        for (int i = 0; i < n; i++) {
            System.out.print(Name[i] + " ");
        }
    }
}
