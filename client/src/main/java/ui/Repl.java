package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preLoginClient;
    private PostLoginClient postLoginClient;
    //private final InGameClient inGameClient;
    private State state;
    private String authToken = null;
    private String serverUrl;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        //postLoginClient = new PostLoginClient(serverUrl);
        //inGameClient = new InGameClient(serverUrl);
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
                        postLoginClient = new PostLoginClient(serverUrl, preLoginClient.getAuthToken());
                    }
                }
                else if (state == State.SIGNEDIN) {
                    result = postLoginClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    if (result.contains("You joined") || result.contains("You are observing")) {
                        //state = State.INGAME;
                    }
                    if (result.contains("You logged out")) {
                        state = State.SIGNEDOUT;
                    }
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

//    public void notify(Notification notification) {
//        System.out.println(SET_TEXT_COLOR_RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        System.out.print("\n[" + state + "]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
