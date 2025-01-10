
# Simple Web Crawler

A lightweight yet powerful, **Java 21** web crawler designed for flexibility, performance, and extensibility. This application was built with a modular architecture to demonstrate clean code, concurrency, and effective software design principles.

---

## **Features**

### **Core Components**

1. **Scope**
   - Determines whether a URL should be crawled based on predefined rules.
   - Example: `DomainScope` restricts crawling to the seed domain and its subdomains.

2. **Frontier**
   - Manages the queue of URLs to be crawled.
   - Includes a thread-safe implementation (`SimpleFrontier`) for concurrent processing.

3. **Processor**
   - Handles content extraction and processing for crawled pages.
   - Example: `LinkExtractorProcessor` extracts hyperlinks from pages.

4. **CrawlController**
   - Orchestrates the entire crawling process by integrating `Scope`, `Frontier`, and `Processor`.

### **Additional Features**

- **Concurrency**: Multi-threaded crawling with a configurable thread pool.
- **Extensibility**: Easily add custom processors for tasks such as analytics or content indexing.
- **Modularity**: Each component (e.g., scope, processor) can be independently extended or replaced.

---

## **Key Design Principles**

1. **Separation of Concerns**: Each class has a single responsibility (e.g., crawling, processing).
2. **Scalability**: Built with concurrency in mind, ensuring efficient URL processing.
3. **Testability**: Unit tests ensure robustness for edge cases like malformed URLs and subdomains.
4. **Error Handling**: Graceful handling of invalid URLs, connection timeouts, and retries.

---

## **Project Structure - Key details **

| File                          | Description                                                                                  |
|-------------------------------|----------------------------------------------------------------------------------------------|
| **`CrawlController.java`**    | Manages the crawling lifecycle, integrating all major components.                            |
| **`SimpleFrontier.java`**     | Handles URL queueing and thread-safe processing.                                             |
| **`DomainScope.java`**        | Restricts URLs to the same domain and its subdomains.                                         |
| **`LinkExtractorProcessor.java`** | Extracts hyperlinks (outlinks) from crawled pages.                                         |
| **`JsoupHtmlFetcher.java`**   | Fetches HTML content using JSoup.                                                            |
| **`Main.java`**               | Application entry point for initializing and executing the crawler.                          |

---

## **Prerequisites**

1. **Java 21**: Ensure you have Java 21 or later installed.
2. **Maven**: Used for building and running the application.

---

## **Build & Run Instructions**

### **Clone the Repository**
```bash
git clone https://github.com/ankur-chr/simple-web-crawler.git
cd simple-web-crawler
```

### **Build the Project**
```bash
mvn clean install
```

### **Run the Application**
```bash
mvn exec:java -Dexec.args="www.teya.com 5"
```
- **Arguments**:
  - `www.teya.com`: The seed URL to start crawling. Optionally, can have protocol specified (http:// or https:// only)
  - `5`: (Optional) Number of threads for concurrent crawling (default: `5`).

### **Output**
- Displays all visited URLs in the console.
- Total count of visited URLs is shown at the end of the crawl.

---

## **Error Handling**

- **Malformed URLs**: Skipped with detailed logging.
- **Connection Errors**: Retry logic with a delay.
- **Out-of-Scope URLs**: Ignored based on the domain.

---

## **Future Enhancements**

1. **Respect Robots.txt**: Add functionality to honor `robots.txt` rules.
2. **Custom Crawl Strategies**: Priority-based or depth-limited crawling.
3. **Politeness Policies**: Delays between requests to avoid overloading servers.
4. **Data Persistence**: Store crawled data in a database for advanced analytics.
5. **Exponential BackOff based Retry**: Staggered retries via exponential back off policy.

---

## **Technical Decisions**

### **Why JSoup?**
- Lightweight and efficient for HTML parsing and HTTP handling.
- Reliable error management for malformed HTML.

### **Why Java 21?**
- Access to enhanced concurrency tools and updated language features.
- Demonstrates familiarity with modern Java features.

---

## **Contact**
For questions or further discussion, please reach out to **[Ankur Chrungoo](mailto:ankur.chrungoo@gmail.com)**.
