package org.ank.crawler;

import org.ank.crawler.controller.CrawlController;
import org.ank.crawler.frontier.Frontier;
import org.ank.crawler.frontier.SimpleFrontier;
import org.ank.crawler.processor.LinkExtractorProcessor;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.DomainScope;
import org.ank.crawler.scope.Scope;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * Example main class demonstrating a Simple Web Crawler:
 * CrawlController + Frontier + Scope + Pipeline of Processors.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Example seed URL
        String seedUrl = "https://www.teya.com";

        // Extract domain
        String domain = extractDomain(seedUrl);
        if (domain == null || domain.isBlank()) {
            LOGGER.severe("Cannot parse domain from " + seedUrl);
            return;
        }

        // Build our domain-limiting scope
        Scope scope = new DomainScope(domain);

        // Build the pipeline of processors (just one: link extraction)
        List<Processor> processors = List.of(new LinkExtractorProcessor());

        // Create a Frontier with concurrency (e.g., 5 threads)
        Frontier frontier = new SimpleFrontier(5);

        // Create a CrawlController
        CrawlController controller = new CrawlController(scope, processors, frontier);

        // Begin the crawl
        controller.beginCrawl(seedUrl);

        // At the end, retrieve visited URIs
        System.out.println("\n\n----- CRAWL COMPLETE -----");
        var visited = controller.getVisitedUris();
        System.out.println("Visited " + visited.size() + " URIs in total.");
        visited.forEach(uri -> System.out.println(" - " + uri));
    }

    private static String extractDomain(String urlString) {
        try {
            var url = new URL(urlString);
            var host = url.getHost();
            return host.replaceFirst("^www\\.", "");
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
