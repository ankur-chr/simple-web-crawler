package org.ank.crawler.processor;

import org.ank.crawler.fetcher.FetchedContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * A Processor that parses HTML content, extracts hyperlinks, and returns them.
 * <p>
 * In a real application, there could be a chain of processors—this is like a single stage.
 */
public class LinkExtractorProcessor implements Processor {

    @Override
    public Set<String> process(FetchedContent fetchedContent, String sourceUri) {
        final Set<String> discovered = new HashSet<>();

        // If status code not 200, skip
        if (fetchedContent.statusCode() != 200) {
            return discovered;
        }

        // Parse the HTML content with JSoup
        final Document doc = Jsoup.parse(fetchedContent.content(), sourceUri);

        // Extract <a href="...">
        final Elements links = doc.select("a[href]");
        for (Element el : links) {
            final String absUrl = el.absUrl("href");
            if (absUrl != null && !absUrl.isBlank()) {
                discovered.add(absUrl);
            }
        }

        return discovered;
    }
}
