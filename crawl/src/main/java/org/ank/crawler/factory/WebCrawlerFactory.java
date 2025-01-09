package org.ank.crawler.factory;

import org.ank.crawler.controller.CrawlController;
import org.ank.crawler.frontier.Frontier;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.Scope;

import java.util.List;

/**
 * Factory for creating a configured CrawlController.
 */
public class WebCrawlerFactory {

    private final Scope scope;
    private final List<Processor> processors;
    private final Frontier frontier;

    public WebCrawlerFactory(Scope scope, List<Processor> processors, Frontier frontier) {
        this.scope = scope;
        this.processors = processors;
        this.frontier = frontier;
    }

    public CrawlController createController() {
        return new CrawlController(scope, processors, frontier);
    }
}
