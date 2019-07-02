#stringDatabase.py

import random
class stringDatabase:
	def readFile(self):
		global list
		list = []
		f  = open("four_letters.txt","r")
		for line in f:
			list.append(line.rstrip("\n").split(" "))

	def getWord(self):
		list_length = len(list)
		randX = random.randint(0,list_length-1)
		sublist_length = len(list[randX])
		randY = random.randint(0,sublist_length-1)
		return list[randX][randY]
		
