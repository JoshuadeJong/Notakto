# Notakto
Notakto is a variant of tic-tac-toe which can be played across one or several boards. Both players use the same piece, an "X", with the goal of forcing the opponent to make a three-in-a-row of Xs. The game is traditional played on a 3x3 board, however any NxM board is possible where N,M >= 3. The game has a distinct advantage over tic-tac-toe as there will always be a winner, however the winner is often determined by who went first and the size of the board.

The game includes different choices of board size, a player verse player mode, and three different levels of AI to play against. The AI play the game as following:
  * Easy: Make a random move into an unoccupied square.
  * Medium: Make a random move into an unoccupied square which will not result in an immediate loss.
  * Hard: Make an optimal move into an unoccupied square which will not result in an immediate loss and maximizes the chance of winning next turn. Look ahead by one turn. 

