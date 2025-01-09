package org.ank.crawler.fetcher;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Concrete implementation using JSoup to fetch HTML.
 */
public class JsoupHtmlFetcher implements HtmlFetcher {

    private static final int TIMEOUT_MS = 5000;

    /*@Override
    public FetchedContent fetch(String uri) throws IOException {
        Connection.Response response = Jsoup.connect(uri)
                .timeout(TIMEOUT_MS)
                .execute();

        return new FetchedContent(response.statusCode(), response.body());
    }*/

    @Override
    public FetchedContent fetch(String uri) throws IOException {
        int attempts = 0;
        IOException lastException = null;

        while (attempts < MAX_RETRIES) {
            try {
                Connection.Response response = Jsoup.connect(uri)
                        .timeout(TIMEOUT_MS)
                        .userAgent(USER_AGENT)
                        .execute();

                return new FetchedContent(response.statusCode(), response.body());
            } catch (IOException e) {
                lastException = e;
                attempts++;
                LOGGER.warning("Fetch attempt " + attempts + " failed for " + uri + ": " + e.getMessage());
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        throw lastException;
    }
}
