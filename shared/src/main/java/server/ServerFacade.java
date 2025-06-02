package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import server.request.RegisterRequest;
import server.result.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public static void main(String[] args) throws ResponseException {
        var serverFacade = new ServerFacade("http://localhost:8080");

        var response = serverFacade.register("user2", "myPassword", "email@mail.net");

        System.out.printf("Register success:\nusername: %s\nauthToken: %s", response.username(), response.authToken());
    }

    public AuthPair register(String username, String password, String email) throws ResponseException {
        // return username and an authToken
        var registerRequest = new RegisterRequest(username, password, email);
        var response = makeRequest("POST", "/user", registerRequest, RegisterResult.class);

        assert response != null;
        return new AuthPair(response.username(), response.authToken());
    }

    public AuthPair login(String username, String password) {
        // return username and an authToken
        return null;
    }

    public void logout(String authToken) {
        // logout
    }

    public ArrayList<GameData> listGames(String authToken) {
        // return list of games
        return null;
    }

    public int create(String authToken, String gameName) {
        // return game ID
        return 1;
    }

    public void join(String authToken, ChessGame.TeamColor playerColor, int gameID) {
        // join a game
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();

            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
