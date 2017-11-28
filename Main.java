/*
 * Mark Brown
 * CSCI 460
 * Programming Assignment 3: Priority Inversion
 */

/*
TO RUN:  If not using netbeans, leave the comment out the "package assignment_3;" line 
below as well as in the job class.  Simply run the program, and follow the on screen prompts.  
Select 1 to run the preset list of jobs provided in the assignment.  Press 2 to generate a random 
job list.  You will then be asked to input the number of jobs you want to generate.
Enter an integer between 1 and 10.  When finished, enter 3 to exit the program.
*/
package assignment_3;

//Imports required by program.
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static Job[] jobs;//Array to hold the job list.
    private static int numJobs;//Variable to hold number of jobs in the list
    private static int jobsLeft;//Keep track of how many jobs are left.
    private static int currentTime;//Timer to simulate system time
    private static boolean bufferFree = true;//Indicates whether or not the buffer shared by T1 and T3 is locked.
    private static int jobWithBuffer = 0;//If the buffer is locked, this indicates which job currently has it.
    private static boolean done = false;//Indicates if we are done running all jobs.

    public static void main(String[] args) {
        int selection = 0;
        //Take user input.  Run until user enters 3 to exit.
        while (selection != 3) {
            Scanner in = new Scanner(System.in);
            System.out.println("Choose from the following options:");
            System.out.println("1: Run preset list of jobs.");
            System.out.println("2: Random sequence of jobs.");
            System.out.println("3: EXIT.");
            selection = in.nextInt();
            System.out.println();

            switch (selection) {
                //Run preset jobs given in the assignment.
                case 1:
                    currentTime = 0;
                    numJobs = 7;
                    jobs = new Job[numJobs];
                    for (int i = 0; i < numJobs; i++) {
                        jobs[i] = null;
                    }
                    System.out.println("Running preset joblist.");
                    System.out.println();
                    presetJobList();
                    printJobList();
                    System.out.println();
                    runJobs();
                    break;
                //Run a randomly generated list of up to 10 jobs.
                case 2:
                    currentTime = 0;
                    boolean valid = false;
                    while (!valid) {
                        System.out.println("How many jobs would you like to generate? (No more than 10)");
                        numJobs = in.nextInt();
                        if (numJobs < 0 || numJobs > 10) {
                            System.out.println("Invalid Entry.  Try Again.");
                        } else {
                            jobs = new Job[numJobs];
                            for (int i = 0; i < numJobs; i++) {
                                jobs[i] = null;
                            }
                            System.out.println("Generating and running " + numJobs + " jobs.");
                            System.out.println();
                            randomJobs(numJobs);
                            printJobList();
                            runJobs();
                            valid = true;
                        }
                    }
                    break;
                //Exit politely.
                case 3:
                    System.out.println("Goodbye.");
                    break;
                //Somethin other than 1, 2, or 3 was entered.  Give an error message.
                default:
                    System.out.println("Invalid Choice.");

            }

        }

    }
    //This method is called to run the list of jobs.
    public static void runJobs() {
        jobsLeft = numJobs;//Set the number of jobs that need to be completed to the total number of jobs.
        int currentJob = 0;//Set currentJob to the first job in the array.
        //While there are still jobs to run, keep running.
        while (jobsLeft > 0) {
            //If this is the first job to be run
            if (currentTime == 0) {
                //set the first jobs start time to 1, and updated the timer.
                jobs[currentJob].startTime = 1;
                currentTime++;
                //If the first job is of type T3, lock the buffer and save the 
                //spot in the job array of the job that has the buffer.
                if (jobs[currentJob].type == 3) {
                    bufferFree = false;
                    jobWithBuffer = currentJob;
                }
            }
            //If there are jobs left to complete, and the previous job was 
            //completed, find the next highest priority job that can run.
            if (jobsLeft > 0 && jobs[currentJob] == null) {
                //Set current job to equal the next job to be run.
                currentJob = findJob();
                //Set the new jobs start time to the current system time.
                jobs[currentJob].startTime = currentTime;
              //If the previous job hasn't completed yet, check to see if there
              //is another job in the job list that should take priority.
            } else {
                currentJob = checkPriority(currentJob);
            }
            
            //Update current time based on what type of job is going to run.
            if (jobs[currentJob].type == 1) {
                currentTime += 3;
            } else {
                currentTime++;
            }
            //Run that job.
            jobs[currentJob].run();
            
            //Check to see if the job was completed.  If so, print results.
            if (jobs[currentJob].finished) {
                System.out.print("Time " + jobs[currentJob].startTime + ", ");
                jobs[currentJob].print();
                //If the completed job was of type 3, free the shared buffer.
                if (jobs[currentJob].type == 3) {
                    bufferFree = true;
                }
                //Remove completed job from job list, and decrement # of jobs left.
                jobs[currentJob] = null;
                jobsLeft--;
            }
        }
    }

    //find the next job after a job completes
    public static int findJob() {
        //Initialize which variable for which job we will run to -1 since that is 
        //not a spot in our list.
        int jobChoice = -1;
        //While we have not found a job to run.
        while (jobChoice == -1) {
            //Search through job list to find next job.
            for (int i = 0; i < numJobs; i++) {
                //Update jobchoice for the first time to the first available job.
                if (jobChoice == -1) {
                    if (jobs[i] != null && jobs[i].arrivalTime <= currentTime) {
                        if (bufferFree == true) {
                            jobChoice = i;
                        } else {
                            if (jobs[i].type == 2) {
                                jobChoice = i;
                            } else if (i == jobWithBuffer) {
                                jobChoice = i;
                            }
                        }
                    }
                } else {
                    if (jobs[jobChoice].type == 2) {
                        if (jobs[i] != null && bufferFree == true && jobs[i].type == 1 && jobs[i].arrivalTime <= currentTime) {
                            jobChoice = i;
                        }
                    } else if (jobs[jobChoice].type == 3) {
                        if (jobs[i] != null && jobs[i].arrivalTime <= currentTime && jobs[i].type < jobs[jobChoice].type) {
                            if (bufferFree == false) {
                                if (jobs[i].type == 2) {
                                    jobChoice = i;
                                }
                            } else {
                                jobChoice = i;
                            }
                        }
                    }
                }
            }
            //If no job was currently available to run, update the system time and 
            //look again.
            if (jobChoice == -1) {
                currentTime++;
            }
            //Lock buffer if we are running a job of type T3.
            else if (jobs[jobChoice].type == 3) {
                bufferFree = false;
                jobWithBuffer = jobChoice;
            }
        }
        //Return next highest priority job that can run.
        return jobChoice;
    }

    //Method to check the priority of available jobs, and determine if they should run.
    public static int checkPriority(int current) {
        int replacement = current; //Variable to hold replacement job if there is one
        boolean replaced = false;//Variable set to true if we find a replacement job
        /*
        If our current job type is two, then the only job that can take priority
        is a type one job.  We loop through the joblist looking for type one jobs,
        and replace our current job if we find a type 1 job, and the buffer for
        type 1/3 jobs is not locked.
        */
        if (jobs[current].type == 2) {
            for (int i = 0; i < numJobs; i++) {
                if (bufferFree == true && jobs[i] != null && jobs[i].arrivalTime <= currentTime && jobs[i].type == 1) {
                    replacement = i;
                    replaced = true;
                }
            }
          /*
            If our current job type is 3, then the only job that can preempt it is
            a job of type 2.  So we look through our list for an elligible type 2
            job and set replaced to true if we find one.
            */
        } else if (jobs[current].type == 3) {
            for (int i = 0; i < numJobs; i++) {
                if (jobs[i] != null && jobs[i].arrivalTime <= currentTime && jobs[i].type == 2) {
                    replacement = i;
                    replaced = true;
                }
            }
        }
        /*
        If we found a suitable replacement job, then we print out the progress of 
        the current job, reset the current running time variable for that job so 
        that when it resumes it will print out correctly, and set the start time 
        of our replacement job.  We then return the replacement job and exit.
        */
        if (replaced == true) {
            System.out.print("Time " + jobs[current].startTime + ", ");
            jobs[current].print();
            jobs[current].resetTimeRunning();
            jobs[replacement].startTime = currentTime;
        }
        return replacement;
    }
    /*
    Method to generate a randomly sized list of random jobs whith the size passed 
    in as the input parameter.
    */
    public static void randomJobs(int num) {
        Random random = new Random();
        int arrival = 1;
        int jobType;

        for (int i = 0; i < num; i++) {
            //Generate Job type 1-3
            jobType = 1 + random.nextInt(3);
            if (i != 0) {
                /*
                For all jobs except the first job (which always arrives at time
                1) generate a random int between 0 and 7 and add it to the 
                previous job's arrival time.
                */
                arrival += (random.nextInt(7)); 
            }
            jobs[i] = new Job(arrival, jobType);

        }
    }
    /*
    Method to generate the joblist that is provided in the assignment sheet.
    */
    public static void presetJobList() {
        jobs[0] = new Job(1, 3);
        jobs[1] = new Job(3, 2);
        jobs[2] = new Job(6, 3);
        jobs[3] = new Job(8, 1);
        jobs[4] = new Job(10, 2);
        jobs[5] = new Job(12, 3);
        jobs[6] = new Job(26, 1);

    }
    /*
    Method to print out our current joblist.
    */
    public static void printJobList() {
        for (int i = 0; i < numJobs; i++) {
            System.out.println("Job " + (i + 1) + ": Arrival Time: " + jobs[i].arrivalTime + " Type: " + jobs[i].type);
        }
        System.out.println();
    }

}
