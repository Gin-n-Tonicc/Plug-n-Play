package com.example.plug_n_play;


import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.regex.Pattern;


public class Crawler extends WebCrawler{

	final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g|tiff?|ttf|font|woff|woff2|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|rar|zip|gz))$");
	
	private static final Pattern DOC_PATTERN = Pattern.compile(".*(\\.(doc|docx|txt|pdf))$");
	
	public static String SITE_ID = "0";
	public static String CRAWL_STORAGE = "c:/temp/siteground";
	public static String CRAWL_URL = "https://siteground.com";
	public static URI cralwUri = null;
	public static boolean DYNAMIC_PAGES = false;
	public static int dataSize = 0;
	public static int maxDataMB = 50 * 1000000; // 50 MB
	
	//WebDriver webDriver = new FirefoxDriver();
	WebDriver webDriver = null;
	
	private static Logger logger = Logger.getLogger(App.class);
	
	public static HashMap<String, String> filesMap = new HashMap<String, String>();
	
	/**
     * Specify whether the given url should be crawled or not based on
     * the crawling logic. Here URLs with extensions css, js etc will not be visited
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        System.out.println(url.getURL().toLowerCase());

        try {
        if (cralwUri == null)
        	cralwUri = new URI(CRAWL_URL);
        
	        URI uri = new URI(url.getURL());
	        
	        String href = url.getURL().toLowerCase();
	        boolean result = !FILTERS.matcher(href).matches() && 
	        		
	        		// same domain or PDF/DOC on another domain
	        		(cralwUri.getHost().equals(uri.getHost()) || DOC_PATTERN.matcher(href).matches());
	
	        return result;
        } catch (URISyntaxException e) {
        	ErrorLogger.logException(e, url.getURL(), SITE_ID);
        	return false;
        }
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by the program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        String fileName = CRAWL_STORAGE + "/" + generateNameFromUrl(url);
        
        System.out.println(url);
        if (dataSize > maxDataMB) {
        	logger.info(">>> Exeeded max Data MB: " + dataSize);
        	this.myController.shutdown();        	
        }
        
        if (DOC_PATTERN.matcher(url).matches())
        {
        	String extension = url.substring(url.lastIndexOf(".")+1);
            // .txt files        	
        	if (page.getParseData() instanceof TextParseData)
        	{
	        	TextParseData txtParseData = (TextParseData) page.getParseData();
	            try {
	            	writeToFile(fileName, txtParseData.getTextContent(), url);
	            } catch (IOException e) {
	            	ErrorLogger.logException(e, fileName, SITE_ID);
	            }
        	} 
            // .doc and .pdf files
        	else if (page.getParseData() instanceof BinaryParseData)
        	{
                try {
                	writeToFile(fileName, page.getContentData(), url);
                } catch (IOException e) {
                	ErrorLogger.logException(e, fileName, SITE_ID);
                }        		
        	}
        }
        //
        // html files
        //
        else if (page.getParseData() instanceof HtmlParseData) {
        	
        	if (!fileName.toLowerCase().endsWith(".html"))
        		fileName = fileName + ".html";
        	
        	if (DYNAMIC_PAGES) {
                try {
                	webDriver.get(url);
                	String content = webDriver.findElement(By.tagName("body")).getText();
                	
                	writeToFile(fileName, content, url);
                } catch (IOException e) {
                	ErrorLogger.logException(e, url, SITE_ID);
                }        		
        	} 
        	else {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();      
                String text = htmlParseData.getText(); //extract text from page
                String html = htmlParseData.getHtml(); //extract html from page
        		
                try {
                	writeToFile(fileName, html, url);
                } catch (IOException e) {
                	ErrorLogger.logException(e, fileName, SITE_ID);
                }        		        		
        	}
        }
    }
    
    
    
    public static String generateNameFromUrl(String url){

        // Replace useless chareacters with UNDERSCORE
        String uniqueName = url.replace("://", "_").replace(".", "_").replace("/", "_");
        // Replace last UNDERSCORE with a DOT
        uniqueName = uniqueName.substring(0,uniqueName.lastIndexOf('_'))
                +"."+uniqueName.substring(uniqueName.lastIndexOf('_')+1,uniqueName.length());
        return uniqueName;
    }
    
    private void writeToFile(String fileName, String str, String url) throws IOException {
    	dataSize += str.length();
    	
    	logger.info(fileName);
    	
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);        
        writer.close();    	
        
        filesMap.put(fileName,  url);
    }
    

    
    private void writeToFile(String fileName, byte[] data, String url) throws IOException {
    	dataSize += data.length;
    	
    	logger.info(fileName);
    	
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(fileName));
        writer.write(data);        
        writer.close();
        
        filesMap.put(fileName,  url);
    }

}
