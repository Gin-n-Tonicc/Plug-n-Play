package com.example.plug_n_play.services.crawler;


import com.example.plug_n_play.model.Site;
import com.example.plug_n_play.repository.PageRepository;
import com.example.plug_n_play.repository.SiteRepository;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class Crawler extends WebCrawler {
    final static Pattern FILTERS = Pattern.compile(".*\\.(css|js|xml|json).*|.*(\\.(bmp|gif|jpe?g|jpg|png|tiff?|ttf|font|woff|woff2|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|rar|zip|gz|ico|svg))$");
    private final String[] toNotInclude = {"/wp-content/", "/wp-json/", "/wp-includes/", "/img/", "/css"};
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final CrawlerExtractor crawlerExtractor;

    @Override
    public boolean shouldVisit(Page referringPage, WebURL webURL) {

        var url = webURL.getURL();

        for (var x : toNotInclude) {
            if (url.contains(x)) {
                return false;
            }
        }

        return !FILTERS.matcher(url).matches();
    }

    @Override
    public void visit(Page pageToVisit) {
        if (pageToVisit.getParseData() instanceof HtmlParseData htmlParseData) {
            Document htmlDoc = Jsoup.parse(htmlParseData.getHtml());
            WebURL webURL = pageToVisit.getWebURL();
            String url = webURL.getURL();
            String parentUrl = webURL.getParentUrl();

            System.out.println("WEB URL: " + url);
            System.out.println("WEB PARENT URL: " + webURL.getParentUrl());
            System.out.println("WEB DOMAIN: " + webURL.getDomain());
            Site site = siteRepository.getSiteByUrlContaining(webURL.getDomain()).orElse(null);

            if (site == null || (parentUrl != null && !url.contains(parentUrl))) {
                System.out.println(" SKIPPED");
                return;
            }

            Map<String, String> metadata = crawlerExtractor.extractMetadata(htmlDoc);
            String title = crawlerExtractor.extractTitle(htmlDoc);
            String body = crawlerExtractor.extractBody(htmlDoc);

            var page = new com.example.plug_n_play.model.Page();
            page.setUrl(url);
            page.setData(body);
            page.setTitle(title);
            page.setMetaData(metadata.toString());
            page.setSite(site);
            pageRepository.save(page);

            System.out.println(" SAVED");
        }
    }
}
