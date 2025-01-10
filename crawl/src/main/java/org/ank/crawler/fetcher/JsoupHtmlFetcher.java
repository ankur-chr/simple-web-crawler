package org.ank.crawler.fetcher;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Concrete implementation using JSoup to fetch HTML.
 * <p>
 * An inbuilt simple retry mechanism is incorporated (using retry delay interval)
 * An exponential backoff policy based retry can be implemented in the future.
 */
public class JsoupHtmlFetcher implements HtmlFetcher {

    private static final Logger LOGGER = Logger.getLogger(JsoupHtmlFetcher.class.getName());
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final String USER_AGENT = "WebCrawler/1.0";

    @Override
    public FetchedContent fetch(String uri) throws IOException {
        int attempts = 0;
        IOException lastException = null;

        // Simple retry policy has been implemented.
        // An exponential backOff policy for retries can be implemented as an enhancement.
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
