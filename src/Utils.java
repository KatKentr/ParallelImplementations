import java.util.Random;

public class Utils {
	
	
	//creates an array of size n with random numbers
	public static int[] RandomArray(int size) {
		Random random=new Random();
		
		int[] arr=new int[size];
		
        for (int i = 0; i <size; i++) {
        	
           arr[i]=random.nextInt(100);
        }
        return arr;
		
	}
	
	//prints an array
   public static void printArray(int arr[]) {
	   
	   int n = arr.length;
	   
		for (int i = 0; i < n; ++i)
			System.out.print(arr[i] + " ");
		System.out.println();
		
   }
   
   //check if the array is sorted in ascending order, returns true if the array is sorted, false otherwise
   
   public static boolean isSorted(int[] array) {
       for (int i = 1; i < array.length; i++) {
           if (array[i] < array[i - 1]) {
               return false;
           }
       }
       return true;
   }
   
   //converts a string to an array of ints
   public static int[] readAsInts(String str) {
       String[] fields = str.trim().split("\\s+");
       int[] vals = new int[fields.length];
       for (int i = 0; i < fields.length; i++)
           vals[i] = Integer.parseInt(fields[i]);
       return vals;
   }
   

}
