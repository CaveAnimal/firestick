public class Calculator {
    /**
     * Adds two integers.
     */
    public static int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        int result = add(19, 23);
        System.out.println("Sum is " + result);
    }
}
