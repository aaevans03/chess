package serverfacade.request;

import com.google.gson.annotations.Expose;

public record RegisterRequest(@Expose String username, @Expose String password, @Expose String email) {
}
