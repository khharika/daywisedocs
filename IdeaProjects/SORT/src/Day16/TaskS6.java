package Day16;
public class TaskS6 {
    public static void quickSort(int[] arr, int low, int high) {
        if (low >= high) return;
        int pivot = arr[high], i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        int pi = i + 1;

        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
    static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
    public static void main(String[] args) {
        int[] arr = {6, 3, 8, 5, 2};
        quickSort(arr, 0, arr.length - 1);
        for (int n : arr) System.out.print(n + " ");
    }
}

