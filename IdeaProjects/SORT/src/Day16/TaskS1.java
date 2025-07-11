package Day16;

import java.util.Arrays;

public class TaskS1 {

    public static void selectionSort(int[] a, int n) {
        for (int i = n - 1; i >= 1; i--) {
            int maxIndex = i;

            for (int j = 0; j < i; j++) {
                if (a[j] > a[maxIndex]) {
                    maxIndex = j;
                }
            }

            // Swap a[i] and a[maxIndex]
            int temp = a[i];
            a[i] = a[maxIndex];
            a[maxIndex] = temp;
        }
    }

    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 4, 2};
        int n = arr.length;

        System.out.println("Before sorting: " + Arrays.toString(arr));

        selectionSort(arr, n);

        System.out.println("After sorting: " + Arrays.toString(arr));
    }
}
