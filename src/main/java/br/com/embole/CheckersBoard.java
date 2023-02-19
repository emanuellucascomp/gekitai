package br.com.embole;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * This canvas displays a 320-by-320 checkerboard pattern with
 * a 2-pixel dark red border.  The canvas will be exactly
 * 324-by-324 pixels. This class contains methods that are
 * called in response to a mouse click on the canvas and
 * in response to clicks on the New Game and Resign buttons.
 * Note that the "New Game" and "Resign" buttons must be
 * created before the Board constructor is called, since
 * the constructor references the buttons (in the call to doNewGame()).
 */
public class CheckersBoard extends Canvas {

    CheckersData board; // The data for the checkers board is kept here.
    //    This board is also responsible for generating
    //    lists of legal moves.

    boolean gameInProgress; // Is a game currently in progress?

    /* The next three variables are valid only when the game is in progress. */

    int currentPlayer;      // Whose turn is it now?  The possible values
    //    are CheckersData.RED and CheckersData.BLACK.

    int selectedRow, selectedCol;   // If the current player has selected a piece to
    //     move, these give the row and column
    //     containing that piece.  If no piece is
    //     yet selected, then selectedRow is -1.

    CheckersMove[] legalMoves;  // An array containing the legal moves for the
    //   current player.
    Label message;
    Button resignButton;
    Button newGameButton;

    /**
     * Constructor.  Creates a CheckersData to represent the
     * contents of the checkerboard, and calls doNewGame to
     * start the first game.
     */
    CheckersBoard(Label message, Button newGameButton, Button resignButton) {
        super(324,324);  // canvas is 324-by-324 pixels
        this.board = new CheckersData();
        this.message = message;
        this.resignButton = resignButton;
        this.newGameButton = newGameButton;
        doNewGame();
    }

    /**
     * Start a new game.  This method is called when the Board is first
     * created and when the "New Game" button is clicked.  Event handling
     * is set up in the start() method in the main class.
     */
    void doNewGame() {
        if (gameInProgress == true) {
            // This should not be possible, but it doesn't hurt to check.
            message.setText("Finish the current game first!");
            return;
        }
        board.setUpGame();   // Set up the pieces.
        currentPlayer = CheckersData.RED;   // RED moves first.
        legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
        selectedRow = -1;   // RED has not yet selected a piece to move.
        message.setText("Red:  Make your move.");
        gameInProgress = true;
        newGameButton.setDisable(true);
        resignButton.setDisable(false);
        drawBoard();
    }

    /**
     * Current player resigns.  Game ends.  Opponent wins.  This method is
     * called when the user clicks the "Resign" button.  Event handling is
     * set up in the start() method in the main class.
     */
    void doResign() {
        if (gameInProgress == false) {  // Should be impossible.
            message.setText("There is no game in progress!");
            return;
        }
        if (currentPlayer == CheckersData.RED)
            gameOver("RED resigns.  BLACK wins.");
        else
            gameOver("BLACK resigns.  RED wins.");
    }

    /**
     * The game ends.  The parameter, str, is displayed as a message
     * to the user.  The states of the buttons are adjusted so players
     * can start a new game.  This method is called when the game
     * ends at any point in this class.
     */
    void gameOver(String str) {
        message.setText(str);
        newGameButton.setDisable(false);
        resignButton.setDisable(true);
        gameInProgress = false;
    }

    /**
     * This is called by mousePressed() when a player clicks on the
     * square in the specified row and col.  It has already been checked
     * that a game is, in fact, in progress.
     */
    void doClickSquare(int row, int col) {

            /* If the player clicked on one of the pieces that the player
             can move, mark this row and col as selected and return.  (This
             might change a previous selection.)  Reset the message, in
             case it was previously displaying an error message. */

        for (int i = 0; i < legalMoves.length; i++)
            if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer == CheckersData.RED)
                    message.setText("RED:  Make your move.");
                else
                    message.setText("BLACK:  Make your move.");
                drawBoard();
                return;
            }

            /* If no piece has been selected to be moved, the user must first
             select a piece.  Show an error message and return. */

        if (selectedRow < 0) {
            message.setText("Click the piece you want to move.");
            return;
        }

            /* If the user clicked on a square where the selected piece can be
             legally moved, then make the move and return. */

        for (int i = 0; i < legalMoves.length; i++)
            if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                    && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
                doMakeMove(legalMoves[i]);
                return;
            }

            /* If we get to this point, there is a piece selected, and the square where
             the user just clicked is not one where that piece can be legally moved.
             Show an error message. */

        message.setText("Click the square you want to move to.");

    }  // end doClickSquare()

    /**
     * This is called when the current player has chosen the specified
     * move.  Make the move, and then either end or continue the game
     * appropriately.
     */
    void doMakeMove(CheckersMove move) {

        board.makeMove(move);

            /* If the move was a jump, it's possible that the player has another
             jump.  Check for legal jumps starting from the square that the player
             just moved to.  If there are any, the player must jump.  The same
             player continues moving.
             */

        if (move.isJump()) {
            legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
            if (legalMoves != null) {
                if (currentPlayer == CheckersData.RED)
                    message.setText("RED:  You must continue jumping.");
                else
                    message.setText("BLACK:  You must continue jumping.");
                selectedRow = move.toRow;  // Since only one piece can be moved, select it.
                selectedCol = move.toCol;
                drawBoard();
                return;
            }
        }

            /* The current player's turn is ended, so change to the other player.
             Get that player's legal moves.  If the player has no legal moves,
             then the game ends. */

        if (currentPlayer == CheckersData.RED) {
            currentPlayer = CheckersData.BLACK;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver("BLACK has no moves.  RED wins.");
            else if (legalMoves[0].isJump())
                message.setText("BLACK:  Make your move.  You must jump.");
            else
                message.setText("BLACK:  Make your move.");
        }
        else {
            currentPlayer = CheckersData.RED;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver("RED has no moves.  BLACK wins.");
            else if (legalMoves[0].isJump())
                message.setText("RED:  Make your move.  You must jump.");
            else
                message.setText("RED:  Make your move.");
        }

            /* Set selectedRow = -1 to record that the player has not yet selected
             a piece to move. */

        selectedRow = -1;

            /* As a courtesy to the user, if all legal moves use the same piece, then
             select that piece automatically so the user won't have to click on it
             to select it. */

        if (legalMoves != null) {
            boolean sameStartSquare = true;
            for (int i = 1; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow != legalMoves[0].fromRow
                        || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                    sameStartSquare = false;
                    break;
                }
            if (sameStartSquare) {
                selectedRow = legalMoves[0].fromRow;
                selectedCol = legalMoves[0].fromCol;
            }
        }

        /* Make sure the board is redrawn in its new state. */

        drawBoard();

    }  // end doMakeMove();

    /**
     * Draw a checkerboard pattern in gray and lightGray.  Draw the
     * checkers.  If a game is in progress, highlight the legal moves.
     */
    public void drawBoard() {

        GraphicsContext g = getGraphicsContext2D();
        g.setFont( Font.font(18) );

        /* Draw a two-pixel black border around the edges of the canvas. */

        g.setStroke(Color.DARKRED);
        g.setLineWidth(2);
        g.strokeRect(1, 1, 322, 322);

        /* Draw the squares of the checkerboard and the checkers. */

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2 )
                    g.setFill(Color.LIGHTGRAY);
                else
                    g.setFill(Color.GRAY);
                g.fillRect(2 + col*40, 2 + row*40, 40, 40);
                switch (board.pieceAt(row,col)) {
                    case CheckersData.RED:
                        g.setFill(Color.RED);
                        g.fillOval(8 + col*40, 8 + row*40, 28, 28);
                        break;
                    case CheckersData.BLACK:
                        g.setFill(Color.BLACK);
                        g.fillOval(8 + col*40, 8 + row*40, 28, 28);
                        break;
                    case CheckersData.RED_KING:
                        g.setFill(Color.RED);
                        g.fillOval(8 + col*40, 8 + row*40, 28, 28);
                        g.setFill(Color.WHITE);
                        g.fillText("K", 15 + col*40, 29 + row*40);
                        break;
                    case CheckersData.BLACK_KING:
                        g.setFill(Color.BLACK);
                        g.fillOval(8 + col*40, 8 + row*40, 28, 28);
                        g.setFill(Color.WHITE);
                        g.fillText("K", 15 + col*40, 29 + row*40);
                        break;
                }
            }
        }

            /* If a game is in progress, highlight the legal moves.   Note that legalMoves
             is never null while a game is in progress. */

        if (gameInProgress) {
            /* First, draw a 4-pixel cyan border around the pieces that can be moved. */
            g.setStroke(Color.CYAN);
            g.setLineWidth(4);
            for (int i = 0; i < legalMoves.length; i++) {
                g.strokeRect(4 + legalMoves[i].fromCol*40, 4 + legalMoves[i].fromRow*40, 36, 36);
            }
                /* If a piece is selected for moving (i.e. if selectedRow >= 0), then
                    draw a yellow border around that piece and draw green borders
                    around each square that that piece can be moved to. */
            if (selectedRow >= 0) {
                g.setStroke(Color.YELLOW);
                g.setLineWidth(4);
                g.strokeRect(4 + selectedCol*40, 4 + selectedRow*40, 36, 36);
                g.setStroke(Color.LIME);
                g.setLineWidth(4);
                for (int i = 0; i < legalMoves.length; i++) {
                    if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow) {
                        g.strokeRect(4 + legalMoves[i].toCol*40, 4 + legalMoves[i].toRow*40, 36, 36);
                    }
                }
            }
        }

    }  // end drawBoard()

    /**
     * Respond to a user click on the board.  If no game is in progress, show
     * an error message.  Otherwise, find the row and column that the user
     * clicked and call doClickSquare() to handle it.
     */
    public void mousePressed(MouseEvent evt) {
        if (gameInProgress == false)
            message.setText("Click \"New Game\" to start a new game.");
        else {
            int col = (int)((evt.getX() - 2) / 40);
            int row = (int)((evt.getY() - 2) / 40);
            if (col >= 0 && col < 8 && row >= 0 && row < 8)
                doClickSquare(row,col);
        }
    }


}
