package request;

import model.GameData;

public record UpdateGameRequest(String authToken, Integer gameID, GameData gameData) {
}
