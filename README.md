Djkstra-Maze
============

The objective:

Given a maze you are allowed to knock down a wall for which incurs a penalty P for each wall
knocked down that will be specified by the user. Because walls may be knocked down, you are guaranteed that a
path exists.

The penalty P is accumulated to the path's distance.

Input Format:

The first line contains the number of rows and columns. Each subsequent line represents a square and possible
walls: N for northern wall, S for southern wall, E for eastern wall, W for western wall. An eastern wall for square
(i,j) implies a western wall for square (i,j+1) (if square (i,j+1) exists), whether or not square (i,j+1) explicity says so,
and so on for other directions. Any square in row zero automatically has a northern wall; similarly for other
squares on the border, except for the starting and ending points. Each square may list several walls (or possibly
no walls); the directions can be in any order, and the squares can be in any order.