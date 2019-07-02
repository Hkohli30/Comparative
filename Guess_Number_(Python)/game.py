#game.py

class game:
	'''
		Game class which stores the information of a particular game
	'''

	def __init__(self,gameNo,word,status,badGuess,missLetter,score):
		'''
			Constructor for game class 
			@args : self : used to refer to the variables of class
			@args : stores game number
			@args : stores status
			@args : stores word
			@args : badGuess
			@args : stores missed letter
			@args : sores scores
		'''
		self.gameNo = gameNo
		self.word = word
		self.status = status
		self.badGuess = badGuess
		self.missLetter = missLetter
		self.score = score
		
	def getResult(self):
		'''
			Function that prepares and returns the string formation of the object
			@args : self : used to refer to the variables of class 
		'''
		x = "{: >15}"
		return x.format(self.gameNo) + x.format(self.word)+ x.format(self.status) + x.format(self.badGuess) + x.format(self.missLetter) + x.format(self.score)
		
