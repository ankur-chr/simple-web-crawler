package org.ank.crawler;

import org.ank.crawler.controller.CrawlController;
import org.ank.crawler.factory.WebCrawlerFactory;
import org.ank.crawler.frontier.SimpleFrontier;
import org.ank.crawler.processor.LinkExtractorProcessor;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.DomainScope;
import org.ank.crawler.scope.Scope;
import org.ank.crawler.util.Constants;

import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
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

        final String seedUrl = preprocessSeedUrl(args[0]);
        final int threadCount = parseThreadCount(args);

        try {
            // Start the crawl
            executeCrawl(seedUrl, threadCount);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred during execution", e);
        }
    }

    /**
     * Preprocesses the seed URL to ensure it includes a protocol.
     *
     * @param seedUrl The raw input seed URL.
     * @return The preprocessed seed URL.
     */
    private static String preprocessSeedUrl(String seedUrl) {
        if (!seedUrl.startsWith(Constants.HTTP_PROTOCOL) && !seedUrl.startsWith(Constants.HTTPS_PROTOCOL)) {
            seedUrl = Constants.HTTPS_PROTOCOL + seedUrl; // Default to HTTPS
            LOGGER.info("Preprocessed seed URL to: " + seedUrl);
        }
        return seedUrl;
    }


    /**
     * Parses the thread count from the arguments or uses a default value.
     *
     * @param args The program arguments.
     * @return The parsed thread count.
     */
    private static int parseThreadCount(String[] args) {
        try {
            return args.length > 1 ? Integer.parseInt(args[1]) : Constants.DEFAULT_THREAD_COUNT;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid thread count argument. Defaulting to " + Constants.DEFAULT_THREAD_COUNT);
            return Constants.DEFAULT_THREAD_COUNT;
        }
    }

    /**
     * Executes the crawl with the given seed URL and thread count.
     *
     * @param seedUrl     The seed URL to start crawling.
     * @param threadCount The number of threads to use.
     * @throws Exception If an error occurs during execution.
     */
    private static void executeCrawl(String seedUrl, int threadCount) throws Exception {
        // Extract and validate the base domain
        final String baseDomain = extractBaseDomain(seedUrl);

        // Initialize components
        final Scope scope = new DomainScope(baseDomain);
        final List<Processor> processors = List.of(new LinkExtractorProcessor());
        final SimpleFrontier frontier = new SimpleFrontier(threadCount);

        // Create factory and controller
        final WebCrawlerFactory factory = new WebCrawlerFactory(scope, processors, frontier);
        final CrawlController controller = factory.createController();

        // Begin crawling
        LOGGER.info("Starting crawl for seed URL: " + seedUrl);
        controller.beginCrawl(seedUrl);

        // Output results
        printResults(controller);
    }

    /**
     * Extracts the base domain from the seed URL.
     *
     * @param seedUrl The seed URL.
     * @return The base domain.
     * @throws MalformedURLException If the seed URL is invalid.
     */
    private static String extractBaseDomain(String seedUrl) throws MalformedURLException {
        try {
            final String baseDomain = DomainScope.extractBaseDomain(seedUrl);
            LOGGER.info("Extracted base domain: " + baseDomain);
            return baseDomain;
        } catch (MalformedURLException e) {
            LOGGER.severe("Failed to extract the base domain from the seed URL: " + seedUrl + ". Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Outputs the results of the crawl on STD OUT! (for the DEMO!)
     *
     * @param controller The CrawlController.
     */
    private static void printResults(CrawlController controller) {
        LOGGER.info("\n----- CRAWL COMPLETE -----");
        final var visited = controller.getVisitedUris();

        // ---------------------------------------------------------------- //
        // ----- Printing directly on CONSOLE / STD OUT for the DEMO! ----- //
        visited.forEach(uri -> System.out.println(" - " + uri));
        System.out.println("Visited " + visited.size() + " URIs in total.");
    }
}
