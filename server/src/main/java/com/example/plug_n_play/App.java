package com.example.plug_n_play;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Map;

public class App {

	final static String API_URL =
	//		"https://plug-n-play.ai/wp-content/plugins/plunplay/plug-n-play-ai-api.php?key=ai81hjahjak897187897a899auy8haj1ah2";
			
	"https://plug-n-play.ai/app/plugnplay/website/get-next/";
	
	final static String BASE_FOLDER = "/home/deploy";
	final static String DOWNLOADS_FOLDER = "/home/deploy/crawler-data";
	
	final static String SUFFIX_WORKING = "_working";
	
	final static String STATUS_START = "1";
	final static String STATUS_END = "2";
	final static String STATUS_ERROR = "3";
	
	private static Logger logger = Logger.getLogger(App.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(App.class.getResourceAsStream("log4j.properties"));
		
		String jsonString = "";
		String url = "";
		String site_id = "";
		String pagesCount = "";
		String maxDataMB = "";

		try {
			jsonString = Http.get(API_URL);
		
			JSONObject obj = new JSONObject(jsonString);
			url = obj.getString("url");
			site_id = obj.get("site_id").toString();
			
			if ("0".equals(site_id))
			{
				logger.info(jsonString + " No sites to process");
				return;
			}
			
			URL u = new URL(url); 
			System.setProperty("log4j.appender.file.File", BASE_FOLDER + "/crawler-logs/" + u.getHost() + ".log");
			
			pagesCount = obj.getString("pagesCount");
			maxDataMB = obj.getString("maxDataMB");
			
			//
			// We work in the temp folder with working folder name being site id
			//
			String tmpdir = DOWNLOADS_FOLDER;
			String fileSeparator = File.separator;
			String folderSeparator = tmpdir.endsWith(fileSeparator) ? "" : fileSeparator; 
			String dirWorking = tmpdir + folderSeparator + site_id + SUFFIX_WORKING;
			String dirFinished = tmpdir + folderSeparator + site_id;
			
			if (new File(dirWorking).exists() || new File(dirFinished).exists())
			{
				System.exit(2);
			}
	        	
			Http.post(ErrorLogger.LOG_URL, 
					String.format("{\"site_id\":\"%s\", \"status\": \""+STATUS_START+"\", \"message\":\"%s\"}", site_id, u.getHost() + " started"));
			logger.info(jsonString + " Crawl started");						
			
			int pagesCountInt = Integer.parseInt(pagesCount);
			int maxDataMBInt = Integer.parseInt(maxDataMB) * 1024*1024;
			Crawler.SITE_ID = site_id;
			CrawlerStart.run(url, dirWorking, maxDataMBInt, pagesCountInt);
			
			//
			// Rename working folder to site_id in order Embeddings to be able to pick it up
			//
			new File(dirWorking).renameTo(new File(dirFinished));
			
			// delete frontier folder
			new File(dirFinished + folderSeparator + "frontier").delete();
			
			writeFileMappings(Crawler.filesMap, tmpdir + folderSeparator + site_id + "/mappings.dat");
			
			Http.post(ErrorLogger.LOG_URL, 
					String.format("{\"site_id\":\"%s\", \"status\": \""+STATUS_END+"\", \"message\":\"%s\"}", site_id, u.getHost() + " finished"));
			logger.info(jsonString + " Crawl finished");
		
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			
			CrawlerStart.shutdown();
			
			String newLine = "\r\n";
			StringBuilder sbError = new StringBuilder();
			sbError.append("json").append(jsonString).append(newLine);
			sbError.append("url").append(url).append(newLine);
			sbError.append("site_id").append(site_id).append(newLine);
			sbError.append("pagesCount").append(pagesCount).append(newLine);
			sbError.append("maxDataMB").append(maxDataMB).append(newLine);

			ErrorLogger.logException(e, sbError.toString(), site_id);

			//
			// Create site_id.log file
			//
			try {
				PrintWriter writer = new PrintWriter(new FileWriter(BASE_FOLDER + "/" + site_id + ".log"));
				writer.write(sbError.toString());
				e.printStackTrace(writer);
				writer.close();
			} catch (IOException io) {}
			

			System.exit(1);
		}
	}
	
	
	
	private static void writeFileMappings(Map<String, String> map, String file) {
		BufferedWriter bf = null; 
		  
        try { 
  
            // create new BufferedWriter for the output file 
            bf = new BufferedWriter(new FileWriter(file)); 
  
            // iterate map entries 
            for (Map.Entry<String, String> entry : 
                 map.entrySet()) { 
  
                // put key and value separated by a colon 
                bf.write(entry.getKey() + "="
                         + entry.getValue()); 
  
                // new line 
                bf.newLine(); 
            } 
  
            bf.flush(); 
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        } 
        finally { 
  
            try { 
  
                // always close the writer 
                bf.close(); 
            } 
            catch (Exception e) { 
            } 
        } 			
	}	
}
