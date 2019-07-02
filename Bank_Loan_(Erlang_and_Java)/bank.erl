%% @author Himanshu Kohli
%% @doc @todo Add description to bank2.


-module(bank).

-export([bank_executor/2]).


bank_executor(Bank_Name,Bank_Balance) -> 
	receive 
			{Money_PID,Reply_PID,Customer_Name,Loan_Amount} ->
								AfterAssign = Bank_Balance - Loan_Amount,
									if AfterAssign >= 0 ->
										   	Status = approves,
											Money_PID ! {bank,Bank_Name,Bank_Balance,Status,Customer_Name,Loan_Amount},
											Reply_PID ! {bank_reply,approves,Money_PID,Loan_Amount},
											bank_executor(Bank_Name,AfterAssign);
									true -> 
											Status = denies,
											Money_PID ! {bank,Bank_Name,Bank_Balance,Status,Customer_Name,Loan_Amount},
											Reply_PID ! {bank_reply,rejects,Money_PID,{self(),Bank_Name}},
											bank_executor(Bank_Name,Bank_Balance)
									end
		after 1500 -> io:fwrite("Bank ~p has a remaining balance of ~p \n",[Bank_Name,Bank_Balance]) end. 

