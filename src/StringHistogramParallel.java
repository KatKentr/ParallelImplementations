import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//author:Aikaterini Kentroti
//Parallel implementation of a string histogram

public class StringHistogramParallel {
	
	static class sharedHistogram {           //the global histogram array
		
		 int alphabetSize = 256;
			
		 int[] histogram = new int[alphabetSize];
			
			public sharedHistogram() {
				
				for (int i = 0; i < alphabetSize; i++) { 
		            histogram[i] = 0; 
		        }
			}
			
		 Lock SharedHistogramLock = new ReentrantLock();
			
		
	}
	
	
	 public static void main(String args[]) throws IOException {
	 
		 String fileString = new String(Files.readAllBytes(Paths.get(args[0])));//, StandardCharsets.UTF_8);
		 int numThreads = Integer.parseInt(args[1]);
		 
		  char[] text = new char[fileString.length()]; 
	      int n = fileString.length();
	      for (int i = 0; i < n; i++) { 
	            text[i] = fileString.charAt(i); 
	        } 
	        
	        System.out.println("file is: "+args[0]);
	        System.out.println("number of characters is: "+ text.length);
	        
	        sharedHistogram hist=new sharedHistogram();           //creates an instance of sharedHistogram
	        
	        
	        long startTime = System.currentTimeMillis(); //start timing
	        
	        /* allocate array of thread references*/
	    	
			Thread[] threads = new Thread[numThreads];

		        /* create and start threads */
		        for (int i = 0; i < numThreads; ++i) {
		            System.out.println("In main: create and start thread " + i);
		            threads[i] = new HistThread(i,numThreads,text,hist);
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
		        
	        
		        /* end timing and print result */
		        long endTime = System.currentTimeMillis();
		        
		        
		        for (int i = 0; i < hist.alphabetSize; i++) {           //print histogram
		        	
		            System.out.println(hist.histogram[i]);
////		            if (hist.histogram[i]!=0) {
////		            	System.out.print("array position "+ i);
////		            }
		            
		        }
		        
		        double time=(endTime - startTime) / (double)1000;
		        
		        //System.out.println("length of histogram is: "+hist.histogram.length);
		        System.out.printf("time to compute = %f seconds\n",time);
	 }
	 
	 static class HistThread extends Thread{
		 
		 int alphabetSize = 256;
		 int start;
		 int stop;
		 int[] localHist=new int[alphabetSize];
		 char[] text;
		 sharedHistogram hist;
		 int myID;
		 
		 
		 public HistThread(int myID,int numThreads,char[] text,sharedHistogram hist) {
			 
			 int n=text.length;                          //each thread will process a separate part of the text table. Results will be stored in each thread's local table localHist
			 
			 start = myID * (n / numThreads);            //define the start and end index of the text table that each thread will process
			 stop = start + (n / numThreads);
		     if (myID == (numThreads - 1)) stop= n;
		     
		     this.text=text;
		     
		     for (int i = 0; i < alphabetSize; i++) {   //initialize thread's local histogram array
				 localHist[i] = 0; 
			 }
		     
		     this.hist=hist;
		     this.myID=myID;
			 		 
	 }
		 
		 public void run() {
			 
			 for (int i=start; i<stop; i++) {
				 
				localHist[(int)text[i]] ++;
				 
			 }
			 
			 
			 //System.out.println("thread " + myID + " local hist calculated"); 
			 
			 hist.SharedHistogramLock.lock();   //to avoid race condition between threads for histogram array
			 
			 try {
				
				 
				 for (int i = 0; i < localHist.length; i++) {            //add the values to global Histogram
					 
					   
					 hist.histogram[i] =hist.histogram[i] + localHist[i];
						
			        }
				 
			 } finally {
				 
				 hist.SharedHistogramLock.unlock();
			 }
			 
		 }
		 
		 

}
}
