public class Task27 {
    public static void main(String[] args) {
        int arr[] = {3, 1, 2, 4, 6};
        sum(arr);
    }

    public static void sum(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        System.out.println("Sum of array values : " + sum);
    }
}