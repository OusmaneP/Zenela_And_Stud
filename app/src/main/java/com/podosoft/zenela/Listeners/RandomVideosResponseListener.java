package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Responses.RandomPostsResponse;
import com.podosoft.zenela.Responses.RandomVideosResponse;

public interface RandomVideosResponseListener {
    void didFetch(RandomVideosResponse response, String message);
    void didError(String message);
}
