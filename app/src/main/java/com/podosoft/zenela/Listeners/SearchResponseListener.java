package com.podosoft.zenela.Listeners;

import com.podosoft.zenela.Responses.ProfileResponse;
import com.podosoft.zenela.Responses.SearchResponse;

public interface SearchResponseListener {
    void didFetch(SearchResponse response, String message);
    void didError(String message);
}
