%% @author Himanshu Kohli
%% @doc @todo Add description to customer2.


-module(customer).


-export([customer_executor/4]).


customer_executor(Customer_Name,Customer_Balance,Orignal_Balance,List_Of_Bank_Pid) -> 
	receive 
		{Money_PID,C_Name} ->	
							Length = length(List_Of_Bank_Pid),
							
								if Length /= 0 -> RandomBankInfo = lists:nth( rand:uniform(Length) , List_Of_Bank_Pid);
								true -> RandomBankInfo = 0
								end,
							
							Check = checkRandomAndBalance(RandomBankInfo,Customer_Balance),
							if Check ->
									    {RandomBank,B_Name} = RandomBankInfo,
										Loan = generateLoanAmt(Customer_Balance),
										Money_PID ! {customer,C_Name,Loan,B_Name,Customer_Balance},
										timer:sleep(rand:uniform(100)),
										RandomBank ! {Money_PID,self(),Customer_Name,Loan};
							true -> Money_PID ! {customer_result,C_Name,Customer_Balance,Orignal_Balance}
							end,
							customer_executor(Customer_Name, Customer_Balance,Orignal_Balance, List_Of_Bank_Pid);
		
		{bank_reply,approves,Money_PID,Loan_Amount} ->
								self() ! {Money_PID,Customer_Name},
								customer_executor(Customer_Name,Customer_Balance - Loan_Amount,Orignal_Balance,List_Of_Bank_Pid);
		
		{bank_reply,rejects,Money_PID,Bank_Info_Tuple} ->
								UpdateBankList = List_Of_Bank_Pid -- [Bank_Info_Tuple],
								Money_PID ! {bank_removal,Customer_Name,Bank_Info_Tuple,Customer_Balance},
								self() ! {Money_PID,Customer_Name},
								customer_executor(Customer_Name,Customer_Balance,Orignal_Balance,UpdateBankList)
		
		after 1500 -> 
					if Customer_Balance == 0 -> io:fwrite("Whoo hooo, ~p has reached the goal ~p \n",[Customer_Name,Orignal_Balance]);
						true -> io:fwrite("Boo Hoo, ~p has ~p amount left to reach the goal of ~p\n",[Customer_Name,Customer_Balance,Orignal_Balance])
						end
			end. 

generateLoanAmt(X) -> 
				if X >= 50 -> rand:uniform(50);
					true -> rand:uniform(X) 
				end.

checkRandomAndBalance(Rand,Balance) ->
		if Rand /= 0 ->
			   if Balance /= 0 ->	true;
			   true -> false end;
		true -> false end.