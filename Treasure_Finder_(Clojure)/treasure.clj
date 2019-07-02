
;; Version 7.0
;; @Author Himanshu Kohli
;; @Date 26 May 2019

;; Algorithm Adaptation from:
;; https://www.geeksforgeeks.org/rat-in-a-maze-backtracking-2/

(require '[clojure.string :as str])

(declare findTreasureLocation)
(declare findTreasure)
(declare printSolution)
(declare withinBounds)
(declare replaceChar)
(declare getValSol)
(declare checkSizes)

(defn ReadFile []
;; Reading file map.txt
   (def elements (slurp "map.txt"))
   ;; Printing the challenge Part
   (println "This is my Challenge: \n")
   (println elements)
   ;; Splitting the elements of the read string to get Vector
   (def myList(clojure.string/split-lines (str/trim elements)))
  
  ;;Defining the vertical and horizontal lengths
  (def hLength (count myList))
  (def vLength (count (nth myList 0)))

  ;; Defining the Solution
  (def sol (atom myList))
  ;; Variables to find the location of treasure
  (def hLoc (atom -1))
  (def vLoc (atom -1))

  ;; Finds the location of the treasure and updates the variables
  (findTreasureLocation)

  (if (true? (checkSizes))
    (do 
        (if (and (= (findTreasure 0 0) false))
          (println "\nUh oh, I could not find the treasure :-( \n")
          (println "\nWoo hoo, I found the treasure :-) \n")
        )
    )
    (println "\nThe elements size of each line is not the same. \n Please check the file for additional spaces or elements")
  )
  
  
  ;; Priniting the solution if same sizes
  (if (true? (checkSizes))
      (printSolution)
  ) 
    
  
)

;; This method recursively finds the path
;; @param : xCord : The vertical Coordinate of the matrix
;; @param : yCord : The horizontal Coordinate of the matrix
(defn findTreasure
  [xCord yCord]
  ;; Reached the last point and found Solution
  ;;(println "The Values:::" xCord yCord)
  (cond 
      (and (= xCord @vLoc) (= yCord @hLoc))   true
    :else
          (do
            (cond
              ;; Check if it is within the matrix bounds
              (= (withinBounds xCord yCord) true)
                (do
                  (replaceChar xCord yCord "+")
                  (cond 
                    ;; Recursive check all the directions
                    (= (findTreasure (+ xCord 1) yCord) true)  true
                    (= (findTreasure  xCord (+ yCord 1)) true) true
                    (= (findTreasure (- xCord 1) yCord) true)  true
                    (= (findTreasure  xCord (- yCord 1)) true) true
                    
                    ;; None of it worked, backtrack
                    :else   (do (replaceChar xCord yCord "!")  false)
                  )
                )
              :else false
            ) 
          )    
  )
)

;; Checks the xCord and yCord is in the bounds of the matrix
(defn withinBounds
  [xCord yCord]
 (if (and (>= xCord 0) (< xCord hLength) (>= yCord 0) (< yCord vLength) (= (str/trim (getValSol xCord yCord)) "-")) 
    true
    false
 )
)

;; This method replaces the character from the list with row and col ;;;; elements and the char by with it is to be replace
(defn replaceChar
  [row col char]
  (def colInc (+ 1 col))
  (def s (nth @sol row))
  (def fhalf (subs s 0 col))
  (def shalf (subs s colInc (count s)))
  (swap! sol assoc row (str fhalf char shalf))
)

;; This method prints the solution
(defn printSolution
  []
  (def in (atom 0))
  (while (< @in (count myList))
    (do
      (println (nth @sol @in))
      (swap! in inc)
    )
  )
)

;; This method checks if the output has same number of elements
(defn checkSizes
  []
  (def in (atom 1))
  (def sizeFlag(atom true))
  (def size (count (nth myList 0)))
  (while (< @in (count myList))
    (do
      (if (not= (count(nth myList @in)) size)
        (reset! sizeFlag false)
      )
      (swap! in inc)
    )
  )
  @sizeFlag
)

;; This method finds the location of the treasure
(defn findTreasureLocation
  []
  (def in (atom 0))
  (while (< @in (count myList))
    (do
      (if (true? (number? (str/index-of (nth @sol @in) "@")))
        (do
          (def t @in)
          (reset! hLoc (str/index-of (nth @sol @in) "@"))
          (reset! vLoc t)
        )
      )
      (swap! in inc)
    )
  )
)

;; This method returns the value of the specific position
(defn getValSol
 [row col]
  (def colInc (+ 1 col))
  (def s (str/trim (nth @sol row)))
  (def v (str/trim (subs s col colInc)))
  v
)

;; This is the main method which excecutes the program
(defn main[]  
(ReadFile)
)

(main)
