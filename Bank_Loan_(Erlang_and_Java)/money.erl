%% @author Himanshu Kohli
%% @doc @todo Add description to money2.


-module(money).

-export([start/0]).

start() ->
	% Refers to the process id of the money class
	MoneyPid = self(),
	TempProcessHolder = #{money_process => MoneyPid},	% Holds each and every process of the class
	
	% Read both the files bank and customer
	CustomerTuples = file:consult("customers.txt"),
	BankTuples = file:consult("banks.txt"),
	
	% Retain the data into the variables
	{_,CustomerData} = CustomerTuples,
	{_,BankData} = BankTuples,

	io:fwrite("** Banks and financial resources **\n\n"),
	Temp = makeBankProcessList(BankData,[]),
	%Create a Holder for the customer processes

	io:fwrite("\n"),
	io:fwrite("** Customers and loan objectives **\n\n"),
	CustomerProcessHolder = makeCustomerProcess(CustomerData,TempProcessHolder,Temp),
	io:fwrite("\n"),
	startProcessing(CustomerData,CustomerProcessHolder),

	% Get the feedback from the customer or bank processes
	display_messages().
	
	


display_messages() ->
	receive
		{customer,C_Name,Loan,B_Name,Customer_Balance} -> 
						io:fwrite("Customer : ~p request a loan of ~p from ~p  and leftover ~p\n",[C_Name,Loan,B_Name,Customer_Balance]),
						display_messages();
		{bank,Bank_Name,Bank_Balance,Status,Customer_Name,Loan_Amount} -> 
						io:fwrite("Bank : ~p with balance: ~p  ~p the loan of ~p from ~p \n",[Bank_Name,Bank_Balance,Status,Loan_Amount,Customer_Name]), 
						display_messages();
		
		{customer_result,C_Name,C_Balance,Orignal_Balance} ->
						if C_Balance == 0 -> io:fwrite("\n Whoo hooo, ~p has reached the goal ~p \n",[C_Name,Orignal_Balance]), io:fwrite("\n");
						true -> io:fwrite("\n Boo Hoo, ~p has ~p amount left to reach the goal \n",[C_Name,C_Balance]),  io:fwrite("\n")
						end,
						display_messages();
		{bank_result,B_Name,B_Balance} ->
						io:fwrite("~p got the balance of ~p \n",[B_Name,B_Balance]), display_messages();
		
		{bank_removal,Customer_Name,Bank_Info_Tuple,Customer_Balance} -> 
			{_,Name} = Bank_Info_Tuple,
			io:fwrite("[ Removal Bank:: ~p with balance ~p removed the bank ~p] \n",[Customer_Name,Customer_Balance,Name]),
						display_messages()
		
		after 1500 -> io:fwrite("\nGoodBye, Have a great day!!!\n\n")	end.

startProcessing([],_) -> ok;
startProcessing([H|T], Holder) -> 
				{F,_} = H,
				CustomerPid = maps:get(F, Holder),
				CustomerPid ! {self(),F},
				startProcessing(T,Holder).

makeCustomerProcess([],Holder,_) -> Holder;
makeCustomerProcess([H|T],Holder,Temp) -> 
				{F,S} = H,
				io:fwrite(" ~p : ~p\n",[F,S]),
				TempHolder = Holder,
				DoesProcessExistsAlready = maps:is_key(F, TempHolder),

				if
					DoesProcessExistsAlready == false -> Pid = spawn(customer,customer_executor,[F,S,S,Temp]), % Create a new process
														 UpdateHolder = maps:put(F, Pid, TempHolder); % Update the process in the map
					
					DoesProcessExistsAlready == true -> UpdateHolder = TempHolder
				end,
				makeCustomerProcess(T,UpdateHolder,Temp).
					
makeBankProcessList([],Holder) -> Holder;
makeBankProcessList([H|T],Holder) -> 
				{F,S} = H,
				io:fwrite(" ~p : ~p\n",[F,S]),
				Pid = spawn(bank,bank_executor,[F,S]), % Create a new process
				UpdateHolder = Holder ++ [{Pid,F}], % Update the process in the list
				makeBankProcessList(T,UpdateHolder).


