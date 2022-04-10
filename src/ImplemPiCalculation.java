
//author:Aikaterini Kentroti
//Parallelization of the calculation of Pi through numerical integration

public class ImplemPiCalculation {
	
	public static double sum=0.0;
	public static long numSteps;
	public static int numThreads;
	
	
public static void main(String[] args) {
		
        long numSteps = 0;
        
        int numThreads=0;

        /* parse command line */
        if (args.length != 2) {                       //Input: number of steps and number of threads as main argument
        	
		System.out.println("arguments:  number_of_steps, number_of_threads");
                System.exit(1);
        }
        
        try {
        	
		numSteps = Long.parseLong(args[0]);
		numThreads=Integer.parseInt(args[1]);
		
        } catch (NumberFormatException e) {
        	
		System.out.println("argument "+ args[0] +" must be long int"+"argument " +args[1] +" must be int");
		System.exit(1);
        }
        
        System.out.printf("Parallel program results with %d steps\n", numSteps);
        System.out.println("Number of threds: "+numThreads);
        
        ImplemPiCalculation.numIntParallel(numThreads,numSteps);       //invoke method to calculate PI
        
	}
	
	
	
	public static void numIntParallel(int numThreads,long numSteps) {
		
		
        /* allocate array of thread references*/
	
		Thread[] threads = new Thread[numThreads];
		
		
		    /* start timing */
            long startTime = System.currentTimeMillis();

	        /* create and start threads */
	        for (int i = 0; i < numThreads; ++i) {
	            //System.out.println("In main: create and start thread " + i);
	            threads[i] = new ThreadSum(i, numSteps,numThreads);
	            threads[i].start();
	            
	        }

	        /* wait for threads to finish */
	        for (int i = 0; i < numThreads; ++i) {
	        		        	                                                                              
	            try                                          
	            {                                                                            
	            	threads[i].join();
	            }
	            catch (InterruptedException e) 
	            {
	                System.err.println("this should not happen"+e);
	            }
	        }
	        
	        //System.out.println("In main: threads all done");
	        
	        
	  
            //Calculation of pi
	        
	        double step = 1.0 / (double)numSteps;
	        double pi = sum * step;                                      //calculate PI
        
	        
	        /* end timing*/
	        long endTime = System.currentTimeMillis();
	        
	        
	        double totalTime=(endTime - startTime) / (double)1000;          //time elapsed
	        
	        //Output
	        System.out.println("Calculation of pi: "+pi);                
	        System.out.printf("total time required: = %f seconds\n",totalTime," with ",numThreads);
        
        
	}

	
private static class ThreadSum extends Thread{
	
	private int myID;
	private double sum_thread=0.0;
	private long numSteps;
	private int numThreads;
	
	//constructor
	public ThreadSum(int myID,long numSteps,int numThreads) {
		
		this.myID=myID;
		this.numSteps=numSteps;
		this.numThreads=numThreads;
					
	}
	
	/* thread code */
    public void run() {
    	    	 
        //System.out.println("hello from thread " + myID + " out of " + numThreads);  debugging purposes
        
      
        double step = 1.0 / (double)numSteps;
        
       
        
        
        /* do computation */
        for (int i=myID; i<numSteps; i=i+numThreads ) {       //calculates integral(sum) for each thread
        	
        	double x = ((double)i+0.5)*step;
        		
            sum_thread += 4.0/(1.0+x*x);
        	
        }
        
   
       synchronized(this){                  //one thread each time able to access global sum
    	    
    	   sum = sum + sum_thread;
    	  
       }
       
       
       
    }
    
    
	
	
}


}
