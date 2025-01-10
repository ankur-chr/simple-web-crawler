package org.ank.crawler.factory;

import constant.TestConstants;
import org.ank.crawler.controller.CrawlController;
import org.ank.crawler.frontier.Frontier;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WebCrawlerFactory}.
 */
class WebCrawlerFactoryTest {

    private Scope mockScope;
    private List<Processor> mockProcessors;
    private Frontier mockFrontier;
    private WebCrawlerFactory factory;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        mockScope = mock(Scope.class);
        mockProcessors = List.of(mock(Processor.class));
        mockFrontier = mock(Frontier.class);

        // Initialize the factory with mocked components
        factory = new WebCrawlerFactory(mockScope, mockProcessors, mockFrontier);
    }

    @Test
    void createController_shouldReturnValidCrawlController() {
        // Act
        final CrawlController controller = factory.createController();

        // Assert
        assertNotNull(controller, "CrawlController should not be null");

        // Validate behavior if possible
        assertDoesNotThrow(() -> controller.beginCrawl("https://example.com"), "beginCrawl should not throw any exception");
    }

    @Test
    void createController_shouldUseProvidedComponentsForBehavior() {
        // Mock the scope to return true for the test URL
        when(mockScope.isInScope(TestConstants.BASE_EXAMPLE_URL)).thenReturn(true);

        // Simulate URL scheduling and crawling behavior
        doAnswer(invocation -> {
            String url = invocation.getArgument(0);
            if (url.equals(TestConstants.BASE_EXAMPLE_URL)) {
                // Simulate that the frontier is scheduling the URL
                return null;
            }
            return null;
        }).when(mockFrontier).schedule(anyString());

        // Act
        final CrawlController controller = factory.createController();
        controller.beginCrawl(TestConstants.BASE_EXAMPLE_URL);

        // Assert
        verify(mockScope, atLeastOnce()).isInScope(TestConstants.BASE_EXAMPLE_URL); // Verify scope check
        verify(mockFrontier, atLeastOnce()).schedule(TestConstants.BASE_EXAMPLE_URL); // Verify scheduling
        verify(mockFrontier, atLeastOnce()).start(mockProcessors, mockScope); // Verify start
    }

}
