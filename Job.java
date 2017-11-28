/*
 * Mark Brown
 * CSCI 460
 * Programming Assignment 3: Priority Inversion
 */

/*
Job class:  Represents each individual job within our job list.
*/
package assignment_3;

public class Job {

    int arrivalTime;//Holds the arrival time of this job.
    int type;//Holds the type (1,2,3) or this job.
    int runTime;//Holds the total time required to run this job.
    //int counter = 0;
    int totalTimeRunning;//Holds the total time this job has been running.
    int currentTimeRunning;//Holds the current time this job has been running.
    int startTime = 0;//Hold the start time of this job.
    boolean finished = false;//Set to true if the job has finished running.

    /*
    Constructor for the job.  Takes in an arrival time and job type and sets the 
    instance variables (arrivalTime, type) to the values passed in.  Once type is
    set, runtime is set based on what type of job it is. (Type 1 and 3 have a runtime
    of 3ms and type 2 jobs have a runtime of 10ms.)
    */
    public Job(int aT, int jobType) {
        arrivalTime = aT;
        type = jobType;
        if (type == 1 || type == 3) {
            runTime = 3;
        } else {
            runTime = 10;
        }
    }

    /*
    Method to set both the total time a job has been running as well as the current
    time a job has been running.  If the job type is 1 then the job will always run
    completely since it has the highest priority.  Otherwise both total and current
    time running are incremented by just 1 ms.  We then check to see if the totaltimerunning
    is equal to this jobs runtime.  If so, the job is finished.
    */
    public void setTimeRunning() {
        if (type == 1) {
            totalTimeRunning += 3;
            currentTimeRunning += 3;
        } else {
            totalTimeRunning++;
            currentTimeRunning++;
        }
        if (totalTimeRunning == runTime) {
            finished = true;
        }
    }
    
    /*
    If a job is interrupted by a higher priority job, then this method is called
    to reset the interrupted jobs current time running.
    */
    public void resetTimeRunning() {
        currentTimeRunning = 0;
    }
    
    /*
    This method is called by main to run each job.  If the type is 1 then the job 
    runs to completion.  Otherwise, we increment the time running of this job and
    check to see if it is finished.
    */
    public void run(){
        if (type == 1){
            totalTimeRunning = 3;
            currentTimeRunning = 3;
            finished = true;
        }else{
            totalTimeRunning++;
            currentTimeRunning++;
            if(totalTimeRunning >= runTime){
                finished = true;
            }
        }
    }

    /*Print out a jobs progress.  If the job type is 1, then the job will run in
    its entirety, and print out.  Otherwise, only the currenttimeRunning will be 
    printed out for the progress of the job.
    */
    public void print() {
        if (type == 1) {
            String t1 = "T\u2081";
            System.out.println(t1 + "111" + t1);
        } else if (type == 2) {
            String t2 = "T\u2082";
            System.out.print(t2);
            for (int i = 0; i <= currentTimeRunning; i++) {
                System.out.print("N");
            }
            System.out.println(t2);

        } else {
            String t3 = "T\u2083";
            System.out.print(t3);
            for (int i = 0; i < currentTimeRunning; i++) {
                System.out.print("3");
            }
            System.out.println(t3);
        }
    }
}
