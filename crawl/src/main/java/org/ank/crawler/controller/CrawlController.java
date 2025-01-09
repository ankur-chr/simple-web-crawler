package org.ank.crawler.controller;

import org.ank.crawler.frontier.Frontier;
import org.ank.crawler.frontier.SimpleFrontier;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.Scope;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The "brain" of the crawl.
 * Manages the Frontier, sets up the Processors, and kicks off the crawl.
 * <p>
 * This would be analogous to the main engine that coordinates
 * scope checking, scheduling, and the processing pipeline.
 */
public class CrawlController {

    private static final Logger LOGGER = Logger.getLogger(CrawlController.class.getName());
    private final Frontier frontier;
    private final List<Processor> processors;
    private final Scope scope;

    /**
     * @param scope      The Scope used to decide if a URI is in or out of scope
     * @param processors A pipeline of Processors to handle each URI
     * @param frontier   The frontier that schedules URIs for processing
     */
    public CrawlController(Scope scope, List<Processor> processors, Frontier frontier) {
        this.scope = scope;
        this.processors = processors;
        this.frontier = frontier;
    }

    /**
     * Start the crawl from a given seed URL.
     */
    public void beginCrawl(String seedUrl) {
        // Check if seed is in scope
        if (!scope.isInScope(seedUrl)) {
            LOGGER.log(Level.WARNING, "Seed URL not in scope: {0}", seedUrl);
            return;
        }
        // Enqueue seed
        frontier.schedule(seedUrl);

        // Start the frontier to process scheduled URLs
        frontier.start(processors, scope);
    }

    /**
     * Retrieve visited URIs after the crawl finishes.
     */
    public Set<String> getVisitedUris() {
        return frontier.getVisited();
    }
}
