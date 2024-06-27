package com.example.plug_n_play.services.crawler;

import com.example.plug_n_play.repository.PageRepository;
import com.example.plug_n_play.repository.SiteRepository;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlerFactory implements CrawlController.WebCrawlerFactory<WebCrawler> {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final CrawlerExtractor crawlerExtractor;

    @Override
    public Crawler newInstance() {
        return new Crawler(
                pageRepository,
                siteRepository,
                crawlerExtractor
        );
    }
}
