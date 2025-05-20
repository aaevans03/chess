package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import service.ClearService;
import service.result.ClearResult;
import spark.Request;
import spark.Response;

public class ClearHandler {

    static MemoryUserDAO user = new MemoryUserDAO();
    static MemoryAuthDAO auth = new MemoryAuthDAO();
    static MemoryGameDAO memory = new MemoryGameDAO();

    // Test if objects in each DAO implementation are removed
    private static void testPrinting() {
        System.out.println(user.getUser("alex"));
        System.out.println(user.getUser("bob"));
        System.out.println(auth.getAuthData("12345"));
        System.out.println(auth.getAuthData("54321"));
        System.out.println(memory.listGames());
    }

    public static Object handleClear(Request request, Response response) {

//        System.out.println("Called " + request.pathInfo());
//        testPrinting();

        ClearResult result = ClearService.clear();

//        testPrinting();

        return new Gson().toJson(result);
    }
}
