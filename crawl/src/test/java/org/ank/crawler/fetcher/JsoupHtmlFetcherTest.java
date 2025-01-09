package org.ank.crawler.fetcher;

import constant.TestConstants;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.io.InterruptedIOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JsoupHtmlFetcher}.
 */
class JsoupHtmlFetcherTest {

    private JsoupHtmlFetcher fetcher;

    @BeforeEach
    void setUp() {
        fetcher = new JsoupHtmlFetcher();
    }

    @Test
    void fetch_shouldReturnFetchedContentForValidUrl() throws IOException {
        // Arrange
        final Connection mockConnection = mock(Connection.class);
        final Connection.Response mockResponse = mock(Connection.Response.class);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("<html><body>Example</body></html>");
        when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
        when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
        when(mockConnection.execute()).thenReturn(mockResponse);

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TestConstants.VALID_SEED_URL)).thenReturn(mockConnection);

            // Act
            final FetchedContent content = fetcher.fetch(TestConstants.VALID_SEED_URL);

            // Assert
            assertNotNull(content, "FetchedContent should not be null");
            assertEquals(200, content.statusCode(), "Status code should match");
            assertEquals("<html><body>Example</body></html>", content.content(), "Content should match");
        }
    }

    @Test
    void fetch_shouldRetryOnIOException() throws IOException {
        // Arrange
        final Connection mockConnection = mock(Connection.class);

        when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
        when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
        when(mockConnection.execute()).thenThrow(new IOException("Network error"));

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TestConstants.VALID_SEED_URL)).thenReturn(mockConnection);

            // Act & Assert
            final IOException exception = assertThrows(IOException.class, () -> fetcher.fetch(TestConstants.VALID_SEED_URL), "Should throw IOException after retries fail");
            assertTrue(exception.getMessage().contains("Network error"), "Exception message should indicate network error");
        }
    }

    @Test
    void fetch_shouldFailForInvalidUrl() {
        // Arrange
        final String invalidUrl = "invalid-url";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> fetcher.fetch(invalidUrl), "Should throw IllegalArgumentException for invalid URL");
    }

    @Test
    void fetch_shouldHandleInterruptedExceptionGracefully() throws IOException {
        // Arrange
        final Connection mockConnection = mock(Connection.class);

        when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
        when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
        when(mockConnection.execute()).thenThrow(new InterruptedIOException("Thread interrupted"));

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(TestConstants.VALID_SEED_URL)).thenReturn(mockConnection);

            // Simulate interruption
            Thread.currentThread().interrupt();

            // Act & Assert
            final IOException exception = assertThrows(IOException.class, () -> fetcher.fetch(TestConstants.VALID_SEED_URL), "Should throw IOException when interrupted");
            assertTrue(exception.getMessage().contains("Thread interrupted"), "Exception message should indicate interruption");
            assertTrue(Thread.interrupted(), "Thread should remain interrupted");
        }
    }
}
