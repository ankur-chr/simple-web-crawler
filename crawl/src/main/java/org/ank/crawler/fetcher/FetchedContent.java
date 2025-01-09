package org.ank.crawler.fetcher;

/**
 * Immutable data container for fetched content from a URL.
 */
public record FetchedContent(int statusCode, String content) {
}
