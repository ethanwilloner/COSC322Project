# Computer Science 322 - Artificial Intelligence
# Game of the Amazons Bot

This bot is designed to play the Amazons board game against other players or bot which are communicating through the server which is automatically configured in the included cosc322-2015 library.

----------


Features
-------------

> **MiniMax Algorithm**

> - Separate parallel and sequential minimax search implementations with Alpha-Beta pruning
> - Iterative deepening implemented for both sequential and parallel searches
> - Uses memory consumption and computation time as cut-off conditions

> **Evaluation Functions**

> - Complex Evaluation Function based on Martin Muller and Theodore Tegos' minimum distance evaluation function
>       - http://library.msri.org/books/Book42/files/muller.pdf
> - Minimum distances on board are efficiently calculated and colored with Iterative Deepening
> - Simple Evaluation Function based on which side has the most available moves

> **Object Oriented Design**

> - Leveraged abstract classes to create a framework for performing the searches
> - Custom XML parser using JAXB
>       - Replaces nanoXML implementation used in the example code provided
>       - Object Oriented
>       - Transformation layer between our internal board layout and the standard layout
>       - Simple unmarshaling and marshaling of XML strings

> **Utilities**

> - Illegal move detection
> - End-of-game state checks
> - Remote play via the Amazon’s game server
> - Method for getting all possible moves for each queen
> - Record time-to-make-move and display after each turn with other statistics
>       - Used in cut-off conditions for iterative deepening minimax search