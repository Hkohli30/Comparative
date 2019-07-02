
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

public class Banks implements Runnable {

	DataHolder holder;
	BlockingQueue<String> bankBlockingQueue;
	HashMap<Thread,Customer> customerProcess;
	long start_timer;
	
	// banks class constructor
	public Banks(DataHolder holder,HashMap<Thread,Customer> cP) {
		this.holder = holder;
		this.customerProcess = cP;
		bankBlockingQueue = new ArrayBlockingQueue<String>(10000);
	}
	
	// sends the reply back to customer
	public synchronized void sendRequest(Customer customer,String reply) throws InterruptedException {
		customer.blockingQueue.put(reply);
	}
	
	// listens to the request from customer to the bank
	public void listenRequest() throws InterruptedException {
		this.start_timer = System.currentTimeMillis();
		while(true) {
			if(!this.bankBlockingQueue.isEmpty()) {
				Thread.sleep(new Random().nextInt(100));
				String message = this.bankBlockingQueue.poll().trim();
				String messageElements[] = message.split("::");
				String name = messageElements[0].trim();
				Set<Thread> keySet = customerProcess.keySet();
				Customer customer = null;
				for(Thread t : keySet) {
					if(t.getName().equalsIgnoreCase(name)) {
						customer = customerProcess.get(t);
					}
				}
				int loan = Integer.parseInt(messageElements[1].trim());
				
				if((holder.balance - loan) > 0) {
					holder.balance = holder.balance - loan;
					Money.MoneyQueue.put(holder.name + " accepts a loan of " + loan + " from " + name);
					String reply = "ACCEPT " + holder.name +" " +loan;
					sendRequest(customer,reply);
				}
				else {
					Money.MoneyQueue.put(holder.name + " rejects a loan of " + loan + " from " + name);
					String reply = "REJECTS " + holder.name +" " +loan;
					sendRequest(customer,reply);
				}
				this.start_timer = System.currentTimeMillis();
			}
			
			if(this.bankBlockingQueue.isEmpty() && (System.currentTimeMillis() - this.start_timer) >= 1500) {
				System.out.println("\nBank : " + Thread.currentThread().getName() + " has left balance of "+ holder.balance);
				break;
			}	
		}
	}
	
	
	
	// Overriden run method to execute threads
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			listenRequest();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
