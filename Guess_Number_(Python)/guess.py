#guess.py
# Author Himanshu Kohli
# Version 1.0
# Date 21 April 2019
import stringDatabase as sd
import game as modelGame

class guess:
	'''
		Guess class which executes and runs the whole guessing game
	'''
	
	global wordFrequency
	wordFrequency = {
		"a" : 8.17, "b" : 1.49, "c" : 2.78, "d" : 4.25, "e" : 12.70, "f" : 2.23, "g" : 2.02,
		"h" : 6.09, "i" : 6.97, "j" : 0.15, "k" : 0.77, "l" : 4.03, "m" : 2.41, "n" : 6.75,
		"o" : 7.51, "p" : 1.93, "q" : 0.10, "r" : 5.99, "s" : 6.33,"t" : 9.06,"u" : 2.76, "v" : 0.98,
		"w" : 2.36, "x" : 0.15, "y" : 1.97,"z" : 0.07
		}
	global tableValues
	tableValues = []
	
	def __init__(self,database):
		'''
			Constructor of the guess class
			@args: database : refers to the object of database class 
		'''
		self.database = database
		database.readFile()

	def showMenu(self):
		'''
			Method used to print the selection menu to screen
			@args : self : used to refer to the variables of class 
		'''
		print("g = guess, t = tell me, l for a letter, and q to quit \n")
		
	def executor(self):
		'''
			Method which has all the driving logic and executes the games
			@args : self : used to refer to the variables of class 
		'''
		self.scoreList = []
		self.word = self.database.getWord()
		self.maskedWord = "----"
		self.startFlag = False
		self.scoreHolder = 0
		self.coveredValue = self.getWordsValue()
		self.counter = 1
		self.wrongCounter = 0
		self.wrongGuessCounter = 0
		self.requestLetterCounter = 0
		self.negativeGuessReduction = 0
		self.loopCounter = 1
		while True and self.loopCounter != 100:
			print("Current Word: " + self.maskedWord )
			self.showMenu()
			print("Enter Respose : ", end =' ')
			userInput = input().lower()
			if self.validiateInput(userInput.lower()) == False:
				print("Please Enter Valid value")
				continue
			else:
				if userInput.lower() == 'q':
					self.printScores()
					temp = 0
					for item in self.scoreList:
						temp += item
					val = self.counter - 1
					if val == 0:
						print("\nNo Game played \n")
					else:
						print("The Final Score is : " + str(round(temp/(val),2)))
					break
				elif userInput.lower() == 'g':
					print("Please Enter Your Guess: ", end = ' ')
					guessWord = input()
					if guessWord.lower() == self.word:
						print("Nice work Champ : " + self.word + " guessed")
						self.gaveUpFlag = False
						self.getGuessScore()
						self.reset()
					else:
						self.wrongGuessCounter += 1
						if self.negativeGuessReduction != 0:
							self.negativeGuessReduction = self.scoreHolder +(0.1 * abs(self.scoreHolder))
						else:
							self.negativeGuessReduction = 0.1
						print("Wrong Guess, Try Again : ")
					
				elif userInput.lower() == 't':
					print("The Guessing word is : " + self.word)
					self.gaveUpFlag = True
					self.scoreHolder = self.giveUpScore()
					self.reset()
					
				elif userInput.lower() == 'l':
					print("Please Enter The character : ", end=' ')
					guessChar = input()
					loc = self.word.find(guessChar.lower())
					if len(str(guessChar)) == 1 and loc != -1:
						while loc != -1:
							lastloc = loc
							self.maskedWord = self.custom_replace(self.maskedWord,loc,self.word[loc])
							loc = self.word.find(guessChar.lower(),loc+1)
							if lastloc == loc:
								break
					else:
						print("Letter not in the word, Try Again!")
						self.requestLetterCounter += 1
						self.wrongCounter += 1
						
					if self.maskedWord.find("-") < 0:
						print("Congratulations You guessed " + self.maskedWord)
						self.getGuessScore()
						self.gaveUpFlag = False
						self.reset()
					
	
	def reset(self):
		'''
			This Method Resets all the flags and variables and starts a new game
			@args : self : used to refer to the variables of class 
		'''
		print("\t\t:::::::::New Character Generated ::::::::::")
		self.updateTable()
		self.word = self.database.getWord()
		self.maskedWord = "----"
		self.scoreHolder = 0
		self.wrongCounter = 0
		self.wrongGuessCounter = 0
		self.requestLetterCounter = 0
		self.loopCounter += 1
		
	def updateTable(self):
		'''
			This Method updates the table
			@args : self : used to refer to the variables of class 
		'''
		status = "Success"
		if self.gaveUpFlag:
			status = "Gave Up"
		self.scoreList.append(self.scoreHolder)
		results = modelGame.game(str(self.counter),self.word,status,str(self.wrongGuessCounter),
									str(self.wrongCounter),str(round(self.scoreHolder,3)))
		tableValues.append(results.getResult())
		self.counter += 1
		
	
	def giveUpScore(self):
		'''
			This method calculates the scores for the option give up / tell me
			@args : self : used to refer to the variables of class 
		'''
		i = 0
		sum = 0
		while i != 4:
			if self.maskedWord[i] != self.word[i]:
				sum = sum - wordFrequency[self.word[i]]
			i = i + 1
		return sum
			
	def getWordsValue(self):
		'''
			This method calculates the sum of all the characters in the word
			@args : self : used to refer to the variables of class 
		'''
		i = 0
		sum = 0
		while i != 4:
			sum = sum + wordFrequency[self.word[i]]
			i = i + 1
		return sum
			
	def printScores(self):
		'''
			The method which prints the table 
			@args : self : used to refer to the variables of class 
		'''
		x = "{: >15}"
		print(x.format("Game No.") + x.format("Word")+ x.format("Status") + x.format("Bad Guess") +
			x.format("Missed Letter")+ x.format("Scores"))
		for x in tableValues:
			print(x)
		
	def getGuessScore(self):
		'''
			This method generates the score of the game
			@args : self : used to refer to the variables of class 
		'''
		if self.requestLetterCounter == 0:
			self.requestLetterCounter = 1
		self.scoreHolder = self.coveredValue / self.requestLetterCounter
		self.scoreHolder = self.scoreHolder - self.negativeGuessReduction
		
	def validiateInput(self,inp):
		'''
			This method validiates the selection of the menu
			@args : self : used to refer to the variables of class 
		'''
		if len(inp) == 1:
				if inp == 'g' or inp == 't' or inp == 'l' or inp == 'q':
					return True
		return False
	
	def custom_replace(self,string,index=0,replace=''):
		'''
			Method used to replace all the occurence of a particular character
			@args : self : used to refer to the variables of class 
			@args : string : the string which is to be replaced
			@args : index : the index value
			@args : replace : the replacing character
		'''
		return '%s%s%s'%(string[:index],replace,string[index+1:])
	

def main():
	print("The Great Guessing Game")
	database = sd.stringDatabase()
	guesser = guess(database)
	guesser.executor();
	
	
if __name__ == "__main__":
    main()