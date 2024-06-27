package com.example.plug_n_play;

import com.example.plug_n_play.model.Site;
import com.example.plug_n_play.repository.SiteRepository;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerStart {

    final static int MAX_CRAWL_DEPTH = -1;
    final static int MAX_PAGES_COUNT = -1;
    final static int NUMBER_OF_CRAWELRS = 10;

    static CrawlController controller = null;

    private final SiteRepository siteRepository;

    public CrawlerStart(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public void run () throws Exception {
        CrawlConfig config = new CrawlConfig();
        config.setMaxDepthOfCrawling(MAX_CRAWL_DEPTH);
        config.setIncludeBinaryContentInCrawling(true);
        config.setIncludeHttpsPages(true);
        config.setMaxPagesToFetch(MAX_PAGES_COUNT);
        PageFetcher pageFetcher = new PageFetcher(config);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        controller = new CrawlController(config, pageFetcher, robotstxtServer);


        for (Site site : siteRepository.findAll()) {
            controller.addSeed(site.getUrl());
        }

        controller.start(Crawler.class, NUMBER_OF_CRAWELRS);

        controller.waitUntilFinish();
    }
}
