

/*
 * @author : Himanshu Kohli
 * @version : 1.0
 * @date 23 June 2019
 * Model class 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
 * Listens to the queued messages of from the other class
 */
class MyListener{
	
	private long start_time;
	
		public void listen() throws InterruptedException {
			this.start_time = System.currentTimeMillis();
			while(true) {
				if(!Money.MoneyQueue.isEmpty()) {
					String p = Money.MoneyQueue.poll();
					if(p != null) {
						System.out.println(p);
						start_time = System.currentTimeMillis();
					}
				}
				
				if(Money.MoneyQueue.isEmpty() && (System.currentTimeMillis() - start_time) >= 2000)
					break;
			}
		}
	
}

/*
 * Main Class of the project handles the execution
 */
public class Money {
	
	static ArrayList<DataHolder> customerList;
	static ArrayList<DataHolder> bankList;
	static HashMap<Thread,Banks> banksprocessMap;
	static HashMap<Thread,Customer> processMap;
	static BlockingQueue<String> MoneyQueue;
	
	public static void main(String args[]) throws Exception{
		
		Money money = new Money();
		// Reading the bank and customer file and storing data
		money.read();	
		 // Parent thread
		Thread.currentThread().setName("Money_Thread");
	
		System.out.println("\n** Banks and Financial Resources **\n");
		
		// Creating threads for banks
		for(DataHolder holder : bankList) {
			System.out.println(holder.name + ":  " + holder.balance);
			Banks banks = new Banks(holder,processMap);
			Thread thread = new Thread(banks);
			thread.setName(holder.name);
			banksprocessMap.put(thread, banks);
		}
		
		
		System.out.println("\n** Customer and Loan Objectives **\n");
		// Creating threads for customers
		for(DataHolder holder : customerList) {
			System.out.println(holder.name + ":  " + holder.balance);
			Customer processExecutor = new Customer(holder, processMap,banksprocessMap);
			Thread thread = new Thread(processExecutor);
			thread.setName(holder.name);
			processMap.put(thread, processExecutor);
		}
		System.out.println("");
		// Starting the banks threads
		Set<Thread> executeBank = banksprocessMap.keySet();
		for(Thread process : executeBank) {
			process.start();
		}
		
		// Starting the customer threads
		Set<Thread> execute = processMap.keySet();
		for(Thread process : execute) {
			process.start();
		}
		
		// Listening to the messages in the blocking queue of main class
		MyListener listener = new MyListener();
		listener.listen();
		System.out.println("\n\nGoodBye have a Nice Day..\n\n");
	}
	
	
	/*
	 * Reads the elements and converts into the list
	 */
	public void read() {
		
		processMap = new HashMap<Thread, Customer>();
		banksprocessMap = new HashMap<Thread, Banks>();

		MoneyQueue = new ArrayBlockingQueue<String>(10000);
		customerList = new ArrayList<DataHolder>();
		bankList = new ArrayList<DataHolder>();
		FileReaderUtil reader = new FileReaderUtil();
		bankList = reader.readFile("banks.txt","BANK");
		customerList = reader.readFile("customers.txt","CUSTOMER");
		
		for(DataHolder d : customerList) {
			for(DataHolder b : bankList) {
				d.bankNames.add(b.name);
			}
		}		
	}
	
	
}
