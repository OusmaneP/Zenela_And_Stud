package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.Responses.RandomVideosResponse;

public interface ProfileResponseListener {
    void didFetch(ProfileResponse response, String message);
    void didError(String message);
}
