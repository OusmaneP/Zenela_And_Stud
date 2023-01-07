package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Responses.RandomPostsResponse;

public interface RandomPostsResponseListener {
    void didFetch(RandomPostsResponse response, String message);
    void didError(String message);
}
