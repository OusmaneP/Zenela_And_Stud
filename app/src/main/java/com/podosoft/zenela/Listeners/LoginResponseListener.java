package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Responses.LoginResponse;

public interface LoginResponseListener {
    void didError(String message);

    void didLogin(LoginResponse body, String message);
}
