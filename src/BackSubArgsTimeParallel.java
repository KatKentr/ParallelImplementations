import java.util.Random;

//Author: Aikaterini Kentroti
//Solution of a nxn lower triangular linear system  with backward substitution - Parallel implementation

public class BackSubArgsTimeParallel {

	public static void main(String[] args) {

		while (StdIn.hasNextLine() && !StdIn.isEmpty()) {

			int size = 0;

			int numThreads = 0;

			String inputstream = StdIn.readLine(); // reads user's input(<array_size> <number_of_threads>) as string

			int[] input = Utils.readAsInts(inputstream); // convert input string to an array of ints, first element: the
															// size of the array to be created, second element: number
															// of threads

			if (input.length != 2) {
				System.out.println("Usage: java BackSubArgs <problem size> <num_Threads>");
				System.exit(1);
			}

			try {

				size = input[0];

				numThreads = input[1];

			} catch (NumberFormatException e) {

				System.out.println("argument " + size + " must be int" + "argument " + numThreads + " must be int");

				System.exit(1);
			}

			if (size <= 0) {
				System.out.println("size should be positive integer");
				System.exit(1);
			}

			double[][] a = new double[size][size];
			double[] b = new double[size];
			double[] x = new double[size];

			// random doubles between 1.0 and 2.0
			Random r = new Random();
			for (int i = 0; i < size; i++) {
				x[i] = 0.0;
				b[i] = 1.0 + (2.0 - 1.0) * r.nextDouble();
				a[i][i] = 2.0 + (1.0 + (2.0 - 1.0) * r.nextDouble());
				for (int j = 0; j < i; j++)
					a[i][j] = 1.0 + (2.0 - 1.0) * r.nextDouble();
			}
			SharedDataM data = new SharedDataM(x); // an instance of the class SharedDataM

			long startTime = System.currentTimeMillis();

			WorkThreadM workers[] = new WorkThreadM[numThreads];

			for (int k = 0; k < numThreads; k++) {

				// System.out.println("k is:"+k);
				workers[k] = new WorkThreadM(k, data, a, numThreads, b);
				workers[k].start();
			}

			for (int k = 0; k < numThreads; k++) {
				try {
					workers[k].join();
				} catch (InterruptedException e) {
				}
			}

			long endTime = System.currentTimeMillis(); // end timing

			double totalTime = (endTime - startTime) / (double) 1000;
			
			System.out.print("size of array is: "+size+" number of threads: "+numThreads);

			System.out.printf("total time required: = %f seconds\n", totalTime);

			/* for debugging */
			//for (int h = 0; h < size; h++) {
				// System.out.println(data.x[h]);
				// System.out.println(data.flags[h]);
			//}

		}
	}

}

class SharedDataM { // holds the array with the unknowns x- shared among the threads and an array
					// with boolean values, which indicates the status of the X values. Calculated: true, otherwise false

	double x[];
	boolean flags[];

	public SharedDataM(double x[]) {

		this.x = x;

		flags = new boolean[x.length];

		for (int i = 0; i < x.length; i++) {

			flags[i] = false;

		}

	}
}

class WorkThreadM extends Thread {

	double mySum;
	SharedDataM myData;
	double a[][];

	int myStart;
	int myStop;
	int myId;
	int numThreads;
	double b[];

	public WorkThreadM(int myId, SharedDataM data, double a[][], int numThreads, double b[]) {

		myData = data;
		mySum = 0.0;
		this.a = a;
		this.b = b;

		int n = a.length;

		myStart = myId * (n / numThreads); // define the the number of rows that each thread will process
		myStop = myStart + (n / numThreads);
		if (myId == (numThreads - 1))
			myStop = n;

		this.a = a;

		this.myId = myId;

		// System.out.println("In thread: "+myId+ " my start is: "+myStart+" my stop is:
		// "+myStop);
	}

	public void run() {

		// System.out.println("In thread: "+myId+ " my start is: "+myStart+" my stop is:
		// "+myStop);

		for (int i = myStart; i < myStop; i++) {

			double sum = 0.0;

			for (int j = 0; j < myStart; j++) {

				while (myData.flags[j] == false) { // each threads waits until a x value required for their
													// calculations, is calculated

				}

				sum = sum + (myData.x[j] * a[i][j]);

			}

			myData.x[i] = (b[i] - sum) / a[i][i];

			myData.flags[i] = true;
		}

	}
}
