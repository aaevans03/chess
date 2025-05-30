package server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.result.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    ClearService clearService;

    ClearHandler(UserDAO userDB, AuthDAO authDB, GameDAO gameDB) {
        clearService = new ClearService(userDB, authDB, gameDB);
    }

    public Object handleClear(Request ignoredRequest, Response ignoredResponse) throws DataAccessException {
        ClearResult result = clearService.clear();

        var objectEncoderDecoder = new ObjectEncoderDecoder();
        return objectEncoderDecoder.encode(result);
    }
}
