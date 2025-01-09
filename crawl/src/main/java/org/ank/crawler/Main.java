package org.ank.crawler;

import org.ank.crawler.config.WebCrawlerConfig;
import org.ank.crawler.controller.CrawlController;
import org.ank.crawler.factory.WebCrawlerFactory;
import org.ank.crawler.frontier.SimpleFrontier;
import org.ank.crawler.processor.LinkExtractorProcessor;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.DomainScope;
import org.ank.crawler.scope.Scope;

import java.util.List;
import java.util.logging.Logger;

/**
 * Entry point for the Web Crawler application.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            LOGGER.severe("Please provide a seed URL as an argument.");
            return;
        }

        final String seedUrl = args[0];
        final int threadCount = args.length > 1 ? Integer.parseInt(args[1]) : 5;

        try {
            // Initialize configuration
            WebCrawlerConfig config = new WebCrawlerConfig(seedUrl, threadCount);

            // Initialize components
            Scope scope = new DomainScope(DomainScope.extractDomain(config.seedUrl()));
            List<Processor> processors = List.of(new LinkExtractorProcessor());
            SimpleFrontier frontier = new SimpleFrontier(threadCount);

            // Create factory and controller
            WebCrawlerFactory factory = new WebCrawlerFactory(scope, processors, frontier);
            CrawlController controller = factory.createController();

            // Begin crawling
            LOGGER.info("Starting crawl for seed URL: " + seedUrl);
            controller.beginCrawl(seedUrl);

            // Output results
            LOGGER.info("\n----- CRAWL COMPLETE -----");
            // Printing directly on console as part of the DEMO!
            var visited = controller.getVisitedUris();
            visited.forEach(uri -> System.out.println(" - " + uri));
            System.out.println("Visited " + visited.size() + " URIs in total.");
        } catch (Exception e) {
            LOGGER.severe("An error occurred: " + e.getMessage());
        }
    }
}
