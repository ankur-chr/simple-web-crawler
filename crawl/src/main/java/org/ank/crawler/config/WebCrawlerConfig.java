package org.ank.crawler.config;

/**
 * Configuration class for Web Crawler settings.
 */
public record WebCrawlerConfig(String seedUrl, int threadCount) {
}
