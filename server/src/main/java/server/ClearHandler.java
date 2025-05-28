package server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.ClearService;
import service.result.ClearResult;
import spark.Request;
import spark.Response;

public class ClearHandler {
    ClearService clearService;

    ClearHandler(UserDAO userDB, AuthDAO authDB, GameDAO gameDB) {
        clearService = new ClearService(userDB, authDB, gameDB);
    }

    public Object handleClear(Request request, Response response) throws DataAccessException {
        ClearResult result = clearService.clear();

        var objectEncoderDecoder = new ObjectEncoderDecoder();
        return objectEncoderDecoder.encode(result);
    }
}
