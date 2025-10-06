package Day16;
public class TaskS2 {
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        // Outer loop for passes
        for (int i = 0; i < n - 1; i++) {
            // Inner loop for comparisons
            for (int j = 0; j < n - 1 - i; j++) {
                // Swap if current element is greater than next
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
    public static void main(String[] args) {
        int[] numbers = {5, 2, 9, 1, 5, 6};
        System.out.println("Before sorting:");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        bubbleSort(numbers); // Call sorting function
        System.out.println("\nAfter sorting:");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
    }
}
