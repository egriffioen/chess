package result;

import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//public record ListGamesResult(List<Map<String, Object>> games, String message) {
//    public ListGamesResult(List<Map<String, Object>> games) {
public record ListGamesResult(ArrayList<GameData> games, String message) {
    public ListGamesResult(ArrayList<GameData> games) {
        this(games, null);
    }

    public ListGamesResult(String message) {
        this(null, message);
    }
}