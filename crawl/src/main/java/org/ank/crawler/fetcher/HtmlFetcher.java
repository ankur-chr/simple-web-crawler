package org.ank.crawler.fetcher;

import java.io.IOException;

/**
 * Minimal interface for fetching the content of a URI.
 * Could be extended for more advanced fetch logic,
 * e.g., request headers, caching, proxy, user agent, etc.
 */
public interface HtmlFetcher {
    /**
     * Fetch content from a URI and return it in a structured format.
     */
    FetchedContent fetch(String uri) throws IOException;
}
