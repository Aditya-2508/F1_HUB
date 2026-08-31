package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.search.SearchResponseDto;

public interface SearchService {

    /**
     * Performs a global search across supported F1Hub domains.
     *
     * @param query user-provided search text
     * @return grouped search results
     */
    SearchResponseDto search(String query);
}