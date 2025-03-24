package dataaccess;

import exception.ResponseException;

import java.sql.SQLException;

abstract public class DatabaseConfigurations {
    void configureDatabase(String[] createStatements) throws ResponseException, SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try(var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, "Unable to configure database");
        }

    }
}
