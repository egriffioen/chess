import chess.*;
import dataaccess.*;
import exception.ResponseException;
import server.Server;
import service.GameService;
import service.UserService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();
        server.run(8080);
    }
}