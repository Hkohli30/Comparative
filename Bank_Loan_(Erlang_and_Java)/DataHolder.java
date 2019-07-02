
/*
 * @author : Himanshu Kohli
 * @version : 1.0
 * @date 23 June 2019
 * Model class 
 */
import java.util.ArrayList;

public class DataHolder {
	
	String name;
	int balance;
	int orignal_balance;
	ArrayList<String> bankNames = null;
	
	// Constructor for customers
	public DataHolder(String name,int balance) {
		this.name = name;
		this.balance = balance;
		this.orignal_balance = balance;
		bankNames = new ArrayList<String>();
	}
	
	// Constructor for banks
	public DataHolder(String name,int balance,int balance_left) {
		this.name = name;
		this.balance = balance;
		this.orignal_balance = -1;
	}

	// returns the list of names
	public ArrayList<String> getBNames() {
		return bankNames;
	}

}
