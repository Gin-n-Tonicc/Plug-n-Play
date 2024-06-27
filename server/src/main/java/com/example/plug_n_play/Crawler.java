package com.example.plug_n_play;


import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Pattern;


public class Crawler extends WebCrawler {

    final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g|jpg|png|tiff?|ttf|font|woff|woff2|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|rar|zip|gz))$");

    private static final Pattern DOC_PATTERN = Pattern.compile(".*(\\.(doc|docx|txt|pdf))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return false;
    }

    @Override
    public void visit(Page page) {

    }
}
