package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.sql.SQLException;
import java.util.HashMap;

public class SQLAuthDAO extends DatabaseConfigurations implements AuthDAO{

    public SQLAuthDAO() throws ResponseException, SQLException, DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public void addAuthToken(AuthData authData) throws ResponseException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?,?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public void removeAuthToken(AuthData authData) throws ResponseException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authData.authToken());
    }

    @Override
    public AuthData getAuthToken(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username")
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clearAllAuthData() throws ResponseException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public HashMap<String, AuthData> getAuthTokens() throws ResponseException {
        HashMap<String, AuthData> allAuthTokens = new HashMap<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        AuthData authToken = new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username")
                        );
                        allAuthTokens.put(authToken.authToken(), authToken);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return allAuthTokens;
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param instanceof AuthData p) {
                        ps.setString(i + 1, p.toString());
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY(`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

}
