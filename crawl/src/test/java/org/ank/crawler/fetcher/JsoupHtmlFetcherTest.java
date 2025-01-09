package org.ank.crawler.fetcher;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests JsoupHtmlFetcher with real or invalid URLs.
 * We disable the real call by default to avoid network dependencies.
 */
class JsoupHtmlFetcherTest {

    @Test
    @Disabled("Enable for a real HTTP fetch test; may fail if offline or site is unreachable.")
    void testFetchRealUrl() throws IOException {
        JsoupHtmlFetcher fetcher = new JsoupHtmlFetcher();
        FetchedContent content = fetcher.fetch("https://www.example.com");

        // Typical statuses: 200 or 3xx for redirect
        int sc = content.statusCode();
        assertTrue(sc >= 200 && sc < 400, "Expected 2xx or 3xx, got " + sc);
        assertNotNull(content.content());
        assertFalse(content.content().isBlank());
    }

    @Test
    void testFetchInvalidUrl() {
        JsoupHtmlFetcher fetcher = new JsoupHtmlFetcher();
        // Expect an IOException for malformed or unreachable
        assertThrows(IOException.class, () -> fetcher.fetch("http:/bad-url"));
    }
}
