package server;

import service.ClearService;
import service.result.ClearResult;
import spark.Request;
import spark.Response;

public class ClearHandler {

    public static Object handleClear(Request request, Response response) {

        ClearResult result = ClearService.clear();

        var objectEncoderDecoder = new ObjectEncoderDecoder();
        return objectEncoderDecoder.encode(result);
    }
}
