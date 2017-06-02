AStar.java is my implementation of the A* algorithm for this assignment.

MidEarthVertex.java are vertex objects used to contain middle earth node info.

MidEarthEdge.java are edge objects used to contain node to node info.

Atlas.txt is my file for parsing which contains all of the node/edge info
from the assignment description.

Usage: 
for compilation, issue either of the following commands
javac AStar.java MidEarthVertex.java MidEarthEdge.java

-or-

javac *.java

run the program in the following format:

java AStar.java <NameOfTextFile> <Number 1, 2, or 3>

Issueing the command: java AStar.java Atlas.txt 1 will run the program on
a heuristic using only distances. Using the number 2 will run AStar with
a weighted heuristic that slightly favours road quality. Running AStar with
a 3 at the end of the command will run AStar with a heuristic that favours
slightly lower risk.

for clarification, intended commands:
java AStar.java Atlas.txt 1
java AStar.java Atlas.txt 2
java AStar.java Atlas.txt 3
