package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Models.Post;
import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.ViewPostResponse;

public interface ViewPostResponseListener {
    void didError(String message);

    void didFetch(ViewPostResponse body, String message);
}
