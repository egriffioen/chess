package ui;

import exception.ResponseException;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLoginClient preLoginClient;
    private PostLoginClient postLoginClient;
    private InGameClient inGameClient;
    private State state;
    private String authToken = null;
    private String serverUrl;
    private String colorPerspective = null;
    private WebSocketFacade ws;

    public Repl(String serverUrl) throws ResponseException {
        preLoginClient = new PreLoginClient(serverUrl);
        state = State.SIGNEDOUT;
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.println("Welcome to 240 chess");
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state == State.SIGNEDOUT) {
                    result = preLoginClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    if (result.contains("You signed in as")) {
                        state = State.SIGNEDIN;
                        ws = new WebSocketFacade(serverUrl, this);
                        postLoginClient = new PostLoginClient(serverUrl, preLoginClient.getAuthToken(), this, ws);
                    }
                }
                else if (state == State.SIGNEDIN) {
                    result = postLoginClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    if ((result.contains("You joined") && result.contains("white")) || result.contains("You are observing")) {
                        state = State.INGAME;
                        colorPerspective = "WHITE";
                        inGameClient = new InGameClient(serverUrl, postLoginClient.getGameID(), preLoginClient.getAuthToken(),
                                colorPerspective, postLoginClient.getObserver(), this, ws);
                        System.out.println();
                    }
                    else if ((result.contains("You joined") && result.contains("black"))) {
                        state = State.INGAME;
                        colorPerspective = "BLACK";
                        inGameClient = new InGameClient(serverUrl, postLoginClient.getGameID(), preLoginClient.getAuthToken(),
                                colorPerspective, postLoginClient.getObserver(), this, ws);
                        System.out.println();
                    }
                    if (result.contains("You logged out")) {
                        state = State.SIGNEDOUT;
                    }
                    if (result.contains("quit --> Returning to Login Screen")) {
                        state = State.SIGNEDOUT;
                    }
                }
                else if (state==State.INGAME) {
                    result = inGameClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    if (result.contains("quit --> Returning to Lobby")||result.contains("left")) {
                        state = State.SIGNEDIN;
                        ws = new WebSocketFacade(serverUrl, this);
                        postLoginClient = new PostLoginClient(serverUrl, preLoginClient.getAuthToken(), this, ws);
                    }
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    @Override
    public void notify(NotificationMessage notificationMessage) {
        System.out.println(SET_TEXT_COLOR_RED + notificationMessage.getMessage());
        printPrompt();
    }

    @Override
    public void notify(ErrorMessage errorMessage) {
        System.out.println(SET_TEXT_COLOR_RED + errorMessage.getMessage());
        printPrompt();
    }

    @Override
    public void notify(LoadGameMessage loadGameMessage) throws ResponseException {
        //System.out.println();
        System.out.println(RESET_TEXT_COLOR);
        PrintChess printChess = new PrintChess(colorPerspective, loadGameMessage.getGame());
        printChess.print();
        //printPrompt();
    }

    private void printPrompt() {
        System.out.print(RESET_TEXT_COLOR + "\n[" + state + "]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
