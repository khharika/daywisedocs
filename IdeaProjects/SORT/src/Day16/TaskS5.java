package Day16;
public class TaskS5 {
    public static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }
    public static void merge(int[] arr, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) {
            temp[k++] = (arr[i] < arr[j]) ? arr[i++] : arr[j++];
        }
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];
        for (int p = 0; p < temp.length; p++) {
            arr[left + p] = temp[p];
        }
    }
    public static void main(String[] args) {
        int[] arr = {5, 2, 9, 1, 6};
        System.out.println("Before sorting:");
        for (int num : arr) System.out.print(num + " ");
        mergeSort(arr, 0, arr.length - 1);
        System.out.println("\nAfter sorting:");
        for (int num : arr) System.out.print(num + " ");
    }
}
