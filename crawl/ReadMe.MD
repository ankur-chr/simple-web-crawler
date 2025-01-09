# Simple Web Crawler

A Simple web crawler in **Java 21**, designed to demonstrate a **modular**, **extensible** architecture. This project uses **JSoup** as the only external library (allowed for HTML parsing/HTTP).

---

## Features

- **Components**
    - **`Scope`**: Decides if a URL is in or out of scope.
    - **`Frontier`**: Manages scheduling, concurrency, and retrieval of URLs.
    - **`Processor`**: Processes fetched pages (e.g., extracting links).
    - **`CrawlController`**: Orchestrates the entire crawl by scheduling seeds and starting the Frontier.

- **Concurrent Fetching**
    - Multiple worker threads poll URLs from a queue.
    - Each thread fetches a URL, processes it, and discovers new URLs.

- **Domain-Limited Scope**
    - A simple **`DomainScope`** ensures only links within a specific domain (including subdomains) are crawled.

- **Extensible**
    - Add new Processors (e.g., for analytics, data storage, or image extraction).
    - Replace or enhance **`Scope`** to handle complex rules (paths, file types, `robots.txt`, etc.).
    - Introduce advanced scheduling or politeness strategies in **`Frontier`**.

---


### Key Files

1. **`CrawlController.java`**  
   Orchestrates the entire crawl, sets up the **Frontier**, **Scope**, and **Processors**.

2. **`Frontier.java`** & **`SimpleFrontier.java`**
    - Schedules URLs to be processed.
    - Spawns worker threads that fetch and process each URL. 
    - This is where concurrency and URI queueing happen.

3. **`Scope.java`** & **`DomainScope.java`**
    - Determines which URLs are “in scope.”
    - `DomainScope` restricts links to a specific domain.

4. **`Processor.java`** & **`LinkExtractorProcessor.java`**
    - Each `Processor` can parse or transform the fetched content.
    - `LinkExtractorProcessor` finds hyperlinks (outlinks) in HTML and returns them.

5. **`HtmlFetcher.java`** & **`JsoupHtmlFetcher.java`**
    - `JsoupHtmlFetcher` uses **JSoup** to perform HTTP requests and capture the response (`FetchedContent`).
    - This is where you can customize headers, user-agents, or timeouts.

6. **`Main.java`**
    - Demo entry point.
    - Creates all components (scope, processor list, frontier) and starts the crawl with a seed URL.

---

## Prerequisites

- **Java 21** (has been tested with OpenJDK v21)
- **Maven** (a Maven `pom.xml` is provided).

---

## How to Build & Run

1. **Clone or Download** this repository: https://github.com/ankur-chr/simple-web-crawler.git
2. Run the maven clean install and exec under the project root directory as follows:
    ```bash
    mvn clean install
    mvn exec:java

 ## Future enhancements

