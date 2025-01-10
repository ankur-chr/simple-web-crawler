package org.ank.crawler.processor;

import constant.TestConstants;
import org.ank.crawler.fetcher.FetchedContent;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link LinkExtractorProcessor}'s behavior for extracting hyperlinks.
 */
class LinkExtractorProcessorTest {

    @Test
    void testProcessWith200Status() {
        // Sample HTML with absolute and relative links
        final String html = """
                <html>
                  <body>
                    <a href="https://www.example.com/abs">Absolute Link</a>
                    <a href="/relative">Relative Link</a>
                  </body>
                </html>
                """;
        final FetchedContent fetched = new FetchedContent(200, html);
        final LinkExtractorProcessor processor = new LinkExtractorProcessor();

        final Set<String> discovered = processor.process(fetched, TestConstants.URL_EXAMPLE_BASE);

        // Should convert the relative link to https://www.example.com/relative
        assertTrue(discovered.contains(TestConstants.URL_EXAMPLE_ABS));
        assertTrue(discovered.contains(TestConstants.URL_EXAMPLE_RELATIVE));
        assertEquals(2, discovered.size());
    }

    @Test
    void testProcessNon200Status() {
        // If status code != 200 => no links extracted
        final FetchedContent fetched = new FetchedContent(404, "<html>404 Not Found</html>");
        final LinkExtractorProcessor processor = new LinkExtractorProcessor();
        final Set<String> discovered = processor.process(fetched, TestConstants.URL_EXAMPLE_BASE);
        assertTrue(discovered.isEmpty());
    }

    @Test
    void testEmptyHtml() {
        final FetchedContent fetched = new FetchedContent(200, "");
        final LinkExtractorProcessor processor = new LinkExtractorProcessor();
        final Set<String> discovered = processor.process(fetched, TestConstants.URL_EXAMPLE_BASE);
        assertTrue(discovered.isEmpty());
    }
}
