package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Responses.CommentResponse;
import com.podosoft.zenela.Responses.LoginResponse;

public interface CommentResponseListener {
    void didError(String message);

    void didLogin(CommentResponse body, String message);
}
