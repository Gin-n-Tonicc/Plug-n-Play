package com.example.plug_n_play;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerStart {

	final static int MAX_CRAWL_DEPTH = 4;
    final static int NUMBER_OF_CRAWELRS = 2;
    final static boolean DYNAMIC_PAGES = false;
    
    final static int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
	
    static CrawlController controller = null;  
    		
    /**
     * Test run here
     */
	public static void main(String[] args) throws Exception {
        int MAX_DOWNLOAD_SIZE = 1048576 * 10;
        
        String CRAWL_STORAGE = "c:/temp/myssp";
        String CRAWL_URL = "https://m2mservices.com/";

        run(CRAWL_URL, CRAWL_STORAGE, MAX_DOWNLOAD_SIZE, 100);
	}
	
	
	public static void shutdown() {
		controller.shutdown();
	}
		
	public static void run(String url, String storageFolder, int maxDataMB, int maxPagesCount) throws Exception {

        /*
         * Instantiate crawl config
         */
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(storageFolder);
        config.setMaxDepthOfCrawling(MAX_CRAWL_DEPTH);
        config.setIncludeBinaryContentInCrawling(true);
        config.setIncludeHttpsPages(true);
        config.setMaxDownloadSize(maxDataMB);
        config.setMaxPagesToFetch(maxPagesCount);
        PageFetcher pageFetcher = new PageFetcher(config);
        
        /*
         * Instantiate controller for this crawl.
         */        
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        controller = new CrawlController(config, pageFetcher, robotstxtServer);


        /*
         * Add seed URLs
         */
        //controller.addSeed("https://siteground.com");

        Crawler.CRAWL_STORAGE = storageFolder;
        Crawler.CRAWL_URL = url;
        Crawler.DYNAMIC_PAGES = DYNAMIC_PAGES;
        Crawler.maxDataMB = maxDataMB;
        controller.addSeed(Crawler.CRAWL_URL);        
        
        		
        /*
         * Start the crawl.
         */
        controller.start(Crawler.class, NUMBER_OF_CRAWELRS);
        
        controller.waitUntilFinish();
    }
}
