import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

//author:Aikaterini Kentroti
//Parallel implementation of the Pi estimation using Monte Carlo Method

public class piMonteCarloParallel {

	public static void main(String[] args) {
		
		    long numSteps = 0;   //number of dart tosses(steps)
	        
	        int numThreads=0;    //number of threads

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
		
	        SharedData data = new SharedData();           //instance of the class SharedData
	        
	        long startTime = System.currentTimeMillis();     //start timing
	        
	        /* allocate array of thread references*/
	    	
			Thread[] threads = new Thread[numThreads];

		        /* create and start threads */
		        for (int i = 0; i < numThreads; ++i) {
		            //System.out.println("In main: create and start thread " + i);
		            threads[i] = new CountThread(i, numThreads,numSteps, data);
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
		        
		        //System.out.println("Total count is: "+data.count);
		        
		        double pi = (double)(data.count*4)/numSteps;       //calculate pi
		        
		        long endTime = System.currentTimeMillis();  //end timing
		        
		        double totalTime=(endTime - startTime) / (double)1000;
		        
		        System.out.printf("Parallel program results with %d steps\n", numSteps);
		        System.out.printf("computed pi = %22.20f\n" , pi);              
		        System.out.printf("total time required: = %f seconds\n",totalTime," with ",numThreads); 
		        
	        
	        
	        
	}
	
}	

class SharedData{
		
		long count=0;
		
		public synchronized void add(long threadCount) {     //to avoid race condition
			
			count+=threadCount;
		}
	}


class CountThread extends Thread{
	
	long myStart;
	long myStop;
	int myCount;
	int myID;
	SharedData data;
	
	public CountThread(int myID,int numThreads,long numSteps,SharedData data){        //each thread will process a slice of the input steps. The private count calculated from each thread (myCount) will be added in the global count
		myStart = myID * (numSteps / numThreads);
		myStop = myStart + (numSteps / numThreads);
        if (myID== (numThreads - 1)) myStop = numSteps;
        this.data=data;
        myCount=0;
        this.myID=myID;
		
	}
	
	public void run() {
		
		//computation for each thread
		
		Random random=new Random();
				
//Remark: Math.random(), which was used in the sequential implementation was replaced. Since Math.random() method is internally synchronized to ensure the correct value is returned when used by multiple threads, 
//it is also a bottleneck when multiple threads use it at the same time. It resulted in poor performance.
			
		
		 for (long i=myStart; i < myStop; i++) {                    
	            
	            double x = random.nextDouble();
	            double y = random.nextDouble();
	            double z = Math.pow(x,2) + Math.pow(y,2);
	            if (z <= 1.0) myCount++;  
	        }
		 
		 //System.out.println("thread: "+myID+" has count: "+myCount);
		 
		 data.add(myCount);
	}
	
	
	
}