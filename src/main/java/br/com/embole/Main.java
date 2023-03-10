package br.com.embole;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;



/**
 * This panel lets two users play checkers against each other.
 * Red always starts the game.  If a player can jump an opponent's
 * piece, then the player must jump.  When a player can make no more
 * moves, the game ends.
 */
public class Main extends Application {
    private GekitaiBoard board;
    private Button newGameButton;
    private Button resignButton;
    private Button sendMessageButton;
    private Label message;
    private TextArea dialog;
    private TextArea messageInput;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The constructor creates the Board (which in turn creates and manages
     * the buttons and message label), adds all the components, and sets
     * the bounds of the components.  A null layout is used.  (This is
     * the only thing that is done in the main Checkers class.)
     */
    public void start(Stage stage) {

        /* Create the label that will show messages. */

        message = new Label("Click \"New Game\" to begin.");
        message.setTextFill(Color.BLACK);
        message.setFont(Font.font(null, FontWeight.BOLD, 18));

        /* Create the buttons and the board.  The buttons MUST be
         * created first, since they are used in the CheckerBoard
         * constructor! */

        newGameButton = new Button("New Game");
        resignButton = new Button("Resign");
        sendMessageButton = new Button("Send message");

        board = new GekitaiBoard(message, newGameButton, resignButton); // a subclass of Canvas, defined below
        board.drawBoard();  // draws the content of the checkerboard

        dialog = new TextArea();
        messageInput = new TextArea();

        /* Set up ActionEvent handlers for the buttons and a MousePressed handler
         * for the board.  The handlers call instance methods in the board object. */

        newGameButton.setOnAction( e -> board.doNewGame() );
        resignButton.setOnAction( e -> board.doResign() );
        board.setOnMousePressed( e -> board.mousePressed(e) );

        /* Set the location of each child by calling its relocate() method */

        board.relocate(20,20);
        newGameButton.relocate(370, 120);
        resignButton.relocate(370, 200);
        message.relocate(20, 370);
        dialog.relocate(20, 400);
        messageInput.relocate(20, 600);
        sendMessageButton.relocate(400, 600);

        /* Set the sizes of the buttons.  For this to have an effect, make
         * the butons "unmanaged."  If they are managed, the Pane will set
         * their sizes. */

        resignButton.setManaged(false);
        resignButton.resize(100,30);
        newGameButton.setManaged(false);
        newGameButton.resize(100,30);
        sendMessageButton.resize(100, 40);
        sendMessageButton.setManaged(false);

        /* Create the Pane and give it a preferred size.  If the
         * preferred size were not set, the unmanaged buttons would
         * not be included in the Pane's computed preferred size. */

        Pane root = new Pane();

        root.setPrefWidth(550);
        root.setPrefHeight(800);

        /* Add the child nodes to the Pane and set up the rest of the GUI */

        root.getChildren().addAll(board, newGameButton, resignButton, message, dialog, messageInput, sendMessageButton);
        root.setStyle("-fx-background-color: lightblue; -fx-border-width:3;");
        messageInput.setStyle("-fx-pref-height: 40px; -fx-pref-width: 370px;");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Gekitai");
        stage.show();

    }
}
