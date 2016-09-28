
This is a little puzzle game in JavaFX that we created in order to practice with Model View 
Controller design and backtracking. The hypothetical situation is one where you have a bank safe
with pillars, tiles, and lasers. The point of the game is to place lasers so that all of the 
tiles of the safe are covered by a laser or a laser beam.

The way the game works once you start the application is you have to fill all the empty
tiles with laser beams. The tiles are given by an input file as a command argument, which you 
can find the format for in the given test files. The constraints of the game are that the lasers 
cannot shoot beams at other lasers, any pillar with a number on it has to have that exact number of 
lasers adjacent to it, a pillar with no number can have as many lasers adjacent to it, and again all
the tiles must be full for safe to be valid and the puzzle complete. 

There are two user interfaces you can use for this game, a graphical and a command line. If you 
choose to use the graphical user interface you have the option to recieve hints for the next laser
placement, or have the system solve the puzzle for you if the safe configuration can be solved.

-Danny
