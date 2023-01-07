package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Responses.LoginResponse;
import com.podosoft.zenela.Responses.PostNotificationResponse;

public interface PostNotificationResponseListener {

    void didError(String message);

    void didFetch(PostNotificationResponse body, String message);
}
