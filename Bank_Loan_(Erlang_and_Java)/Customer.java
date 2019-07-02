

/*
 * @author : Himanshu Kohli
 * @version : 1.0
 * @date 23 June 2019
 * Customer class 
 */

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
 * Customer class 
 */
public class Customer implements Runnable {

	DataHolder holder;
	HashMap<Thread,Customer> processList;
	BlockingQueue<String> blockingQueue;
	HashMap<Thread,Banks> banksprocessMap;
	long start_timer;
	
	// Constructor for customers
	public Customer(DataHolder holder, HashMap<Thread,Customer> process,HashMap<Thread,Banks> banksprocessMap) {
		this.holder = holder;
		this.processList = process;	
		blockingQueue = new ArrayBlockingQueue<String>(10000);
		this.banksprocessMap = banksprocessMap;
		
	}
	
	// sends request to the bank for a loan amount
	public synchronized void sendRequest() throws InterruptedException {
		if(!holder.bankNames.isEmpty() && holder.balance > 0) {
			String randBankName = holder.bankNames.get(new Random().nextInt(holder.bankNames.size()));
			Thread.sleep(new Random().nextInt(100));
			
			Set<Thread> keySet = banksprocessMap.keySet();
			for(Thread t : keySet) {
				Banks temp = banksprocessMap.get(t);
				if(t.getName().equalsIgnoreCase(randBankName.trim())) {
					int loan = (new Random().nextInt(holder.balance+1));
						if(loan > 0) {
							temp.bankBlockingQueue.put( holder.name  + "::" + loan);
							Money.MoneyQueue.put(holder.name + " requests a loan of "+ loan + " from " + randBankName);
							
						}
					}
				}
			}
		}

	// listens to the reply from bank class 
	public void listenRequests() throws InterruptedException {
		this.start_timer = System.currentTimeMillis();
		while(true) {
			
				if(!this.blockingQueue.isEmpty() && this.blockingQueue.peek().contains("ACCEPT")) {
					String message = this.blockingQueue.poll();
					String messageBroken[] = message.split(" ");
					int approvedLoan = Integer.parseInt(messageBroken[2].trim());
					this.holder.balance = this.holder.balance - approvedLoan;
					this.start_timer = System.currentTimeMillis();
					sendRequest();
				}
				else if(!this.blockingQueue.isEmpty()  && this.blockingQueue.peek().contains("REJECTS")) {
					String message = this.blockingQueue.poll();
					String messageBroken[] = message.split(" ");
					Money.MoneyQueue.put("[Removing bank "+messageBroken[1].trim() + " from " + holder.name + "]");
					this.holder.bankNames.remove(messageBroken[1].trim());
					this.start_timer = System.currentTimeMillis();
					sendRequest();
				}
				
				if(this.blockingQueue.isEmpty() && (System.currentTimeMillis() - this.start_timer) >= 1500) {
					if(this.holder.balance == 0)
						System.out.println("\nWhooo Hoooo!!!, " + Thread.currentThread().getName() + " has achieved a balance of "+ holder.orignal_balance);
					else
						System.out.println("\nBoooo Hoooo!!!, " + Thread.currentThread().getName() + " has left "+ holder.balance + " to reach a goal of "+ holder.orignal_balance);
					break;
				}		
		}
	}

	
	// overriden run method to execute the threads
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			sendRequest();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			listenRequests();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
