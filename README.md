# Java GIS-like System , Game & GUI with AI algorithms and mTSP/VRP Solutions.



[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://github.com/Liadc/OOP-Ex4/)
© Written by Liad Cohen and Timor Sharabi.

OOP-Ex2, Ex3, Ex4 are 3 assignments we (Liad and Timor) were doing as part of homework tasks in the Java Object Oriented Course in Ariel University.
<br>
<br>
Websites:  <br>
<a href="https://github.com/Liadc/OOP-Ex4/wiki">Wiki Website </a> <br>

<b><a href="https://liad.cloud/">Database Website </a> <br></b>

<a href="https://liadc.github.io/OOP-Ex4/">Javadoc Website </a> <br>


## The whole project represents a Geographic Information System (GIS) with an amazing GUI.

## Table of Contents:
- Project overview
- Showcase
- EX2 - Release 2.0 (Basic Infrastructure)
- EX3 - Release 3.0 (mTSP problem)
- EX4 - Release 4.0 - (Full Game - with AI Algorithms).
- How-To
- Algorithms Explanation
- Project Structure, Packages, Classes and Class Diagram



## Project Overview 
Named "Pacman and Fruits" as assignment asked, but this can be used as a real-life GIS including a GUI with real-time Elements/Objects moving according to real-life speeds, 3D distances, Lat-Lon-Alt GPS coordinates converters and more.
Saving, Loading CSV files to work inside the application, as well as Exporting to KML files which are compatible with Google Earth, as well as showing the full animation of objects moving in the Java GUI (using threads), AND in Google Earth itself!
Algorithms to solve mTSP/VRP problems as all "Pacmen" will have to eat all "Fruits" as fast as possible (different speeds).

## Showcase
More Pictures and GIFs on the WIKI pages.

![](https://media.giphy.com/media/RKtB8DTjz6rNrr4YYB/giphy.gif)

![alt text](https://camo.githubusercontent.com/6720886e5d4570689d94e5adc5162f3c489d8cb3/68747470733a2f2f692e6962622e636f2f5a4b714c6b64622f53637265656e73686f742d312e706e67)

## EX2 - Release 2.0 (Basic Infrastructure)
We developed a GIS (Geographic Information System)-like system including GIS infrastructure, calculating real-life 3D distances between objects using our Lat-Lon-Alt coordinates converters, time changes between Long Epoch time to UTC, 
Reading CSV files & reading whole folders with CSV files, constructing objects from CSV and showing on Google Earth after exporting to KML files. 
(No GUI in this Release).


## EX3 - Release 3.0 (mTSP problem)
Java GUI including image map for a "game", real-life speeds of objects in a map, as well as real-life distances between pixels in a map, movement (with real-life speed) of elements and some algorithms to solve the mTSP "Pac-men on a map to eat all fruits as fast as possible". (with different speeds variant of the problem!).
Includes a "Painter" thread which will show an animation of the solved solution of the algorithm, moving the pac-men in real-time according to their speed (With respect to real-life distances). This thread will paint according to the user preferred FPS, and for how long he wants the animation to show.  Look here for more information.

This release includes "Path", "Solution" and "ShortestPathAlgo", "Game", "Map" "Fruits", "Packman" (Yes, with a typo because of the assignment), "GUI" classes and more.

## EX4 - Release 4.0 - (Full Game with AI algorithms)

Java GUI representing a Game with Keyboard and Mouse play supported.  <br>
Players sets a location to start, and has to eat all fruits before the time (100 seconds) runs out.  <br>
In different maps there are also "Ghosts" which are enemies who will run after the player and if they manage to get to him, it will reduce the score by 15. <br>
There are "Obstacles" which the player cannot move through, if he tries it will drain 1 point and also stays at the same spot (no noclipping) <br>
There are other "Pacmen" inside the game, which also eat the player's fruits. <br>
The Player can eat other Pacmen or Fruits to get his scores up. (by 1). <br>
If the player finishes the map (all fruits are gone) the reimaining time will be added to his score. <br>
I.E: If the player finishes the game after 30 seconds, and collected 25 points by eating fruits and pacmen, his total score will be 70+25 = 95. <br>
 <br>
Every move of the player will drain 0.1 seconds of the time remaining for the map. <br>
Ghosts and Packman can noclip through obstacles, and their speed is half the speed of the player. <br>
When the game ends, it will be sent to an online MySQL database with IDs of the players, map played and the score. <br>
 <br>
Our mission was to be able to construct the game with the GUI with Java programming, all while using OOP design patterns, support keyboard and mouse listeners.  <br>
Later, to develop an AI algorithm: The player will move automatically and play against the map to score the highest. <br>
 <br>

##  How-To
### Load/Save file Menu
![alt text](https://i.imgur.com/BiGHdNc.jpg)
### Load a file
![alt text](https://i.imgur.com/4jeLKvr.jpg)
![alt text](https://i.imgur.com/FAror2g.jpg)

![alt text]()
### Run an Algorithm:
![alt text](https://i.imgur.com/9g8kT3v.jpg)
### Compare results against other players:
![alt text](https://i.imgur.com/6Upwcjm.jpg)
![alt text](https://i.imgur.com/0f9yiNg.jpg)


## AI Algorithms Explanation
We have developed two algorithms: Randomized and Greedy algorithm for the player AI.
Both algorithms will use shared methods, such as Obstacles avoidance methods, which will use Graph theory and Dijkstra's Algorithm
to find a shortest path from specific source node to other nodes in our Graph.
More information and details inside the WIKI of this repo.

### Greedy / Heuristics algorithm
This algorithm is not guaranteed to provide the best/optimal solution. <br>
We use heuristics algorithms to solve the map as efficiently as possible. <br>
More information and details inside the WIKI of this repo. <br>
<a href="https://github.com/Liadc/OOP-Ex4/wiki/Heuristics---Greedy-AI-Algorithm">Wiki Website </a> <br>

### Randomized algorithm
This algorithm is not guaranteed to provide the best/optimal solution. <br>
We shuffle an ArrayList of targets for the player.  <br>
This is a base infrastructure for later Genetic-Algorithm which will be developed during semester break. <br>
More information and details inside the WIKI of this repo. <br>
<a href="https://github.com/Liadc/OOP-Ex4/wiki/Heuristics---Greedy-AI-Algorithm">Wiki Website </a> <br>

![alt text](https://github.com/Liadc/OOP-Ex4/blob/master/manual-vs-algorithm.jpg?raw=true)

## Project Structure, Packages, Classes and Class Diagram

### Overview:
![alt text](https://i.imgur.com/4lc4Z3r.jpg)

 <a href src = https://liadc.github.io/OOP-Ex4/>Java Doc </a>



   [sameREADME]: <https://github.com/xposionn/OOP-Ex2/edit/master/README.md>

### Class Diagram: (Enlarge to view whole image)
![alt text](https://github.com/xposionn/OOP-Ex2/blob/master/ClassDiagram/Ex3-NoDependenciesClassDiagram.png?raw=true)
### Class Diagram with dependencies: (Enlarge to view whole image)
![alt text](https://github.com/xposionn/OOP-Ex2/blob/master/ClassDiagram/Ex3-WithDependenciesClassDiagram.png)
Will edit more for Ex4.
