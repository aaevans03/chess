package serverfacade.request;

import com.google.gson.annotations.Expose;

public record CreateRequest(String authToken, @Expose String gameName) {
}
