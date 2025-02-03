package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamColorTurn;


    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamColorTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColorTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColorTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingTeam;
        if (teamColor == TeamColor.WHITE) {
            opposingTeam = TeamColor.BLACK;
        }
        else {
            opposingTeam = TeamColor.WHITE;
        }
//        Collection<ChessPiece> attackingPieces = attackKingPieces(board, opposingTeam);
//        return !attackingPieces.isEmpty();
        return kingInDanger(opposingTeam);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
        //TODO Does this need to be a deep copy?
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private Collection<Collection<ChessMove>> compileTeamPossibleMoves(TeamColor opposingTeam) {
        Collection<Collection<ChessMove>> compiledMoves = new ArrayList<>();
        for(int i=0;i<8;i++) {
            for(int j = 0; j<8; j++) {
                ChessPosition boardspace = new ChessPosition(i+1,j+1);
                if(board.getPiece(boardspace)==null){
                    break;
                }
                else if (board.getPiece(boardspace).getTeamColor()==opposingTeam) {
                    compiledMoves.add(board.getPiece(boardspace).pieceMoves(board, boardspace));
                }
            }
        }
        return compiledMoves;
    }

    private boolean kingInDanger(TeamColor opposingTeam) {
        Collection<Collection<ChessMove>> opposingTeamMoves = compileTeamPossibleMoves(opposingTeam);
        for(Collection<ChessMove> movesList:opposingTeamMoves) {
            for(ChessMove move: movesList) {
                ChessPiece pieceToCheck = board.getPiece(move.getEndPosition());
//                if (board.getPiece(move.getEndPosition()) == null) {
//                    break;
//                }
                if (pieceToCheck != null && board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                    if(board.getPiece(move.getEndPosition()).getTeamColor()!=opposingTeam) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Collection<ChessPiece> attackKingPieces(ChessBoard board, TeamColor opposingTeam) {
        Collection<ChessPiece> attackingPieces = new ArrayList<>();
        Collection<Collection<ChessMove>> opposingTeamMoves = compileTeamPossibleMoves(opposingTeam);

        for (Collection<ChessMove> movesList : opposingTeamMoves) {
            for (ChessMove move : movesList) {
                ChessPiece attackingPiece = board.getPiece(move.getStartPosition());
                if (board.getPiece(move.getEndPosition()) == null) {
                    break;
                }
                ChessPiece targetPiece = board.getPiece(move.getEndPosition());
                if (targetPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (targetPiece.getTeamColor() != opposingTeam) {
                        attackingPieces.add(attackingPiece);
                    }
                }
            }
        }
        return attackingPieces;
    }
}
