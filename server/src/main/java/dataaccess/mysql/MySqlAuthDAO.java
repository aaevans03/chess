package dataaccess.mysql;

import dataaccess.AuthDAO;
import model.AuthData;

public class MySqlAuthDAO implements AuthDAO {
    @Override
    public void clearAuthData() {

    }

    @Override
    public String createAuthData(String username) {
        return "";
    }

    @Override
    public AuthData getAuthData(String authToken) {
        return null;
    }

    @Override
    public void deleteAuthData(AuthData authData) {

    }
}
