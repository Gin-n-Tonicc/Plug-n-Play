package com.example.plug_n_play;

import org.apache.log4j.Logger;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorLogger {

	private static Logger logger = Logger.getLogger(App.class);
	
	public static final String LOG_URL = "https://plug-n-play.ai/app/plugnplay/website/crawler-log/";
	
	public static void logException(Exception e, String data, String site_id) {
		
		logger.error(data, e);
		
		StringBuilder sbError = new StringBuilder();
		
		sbError.append(data);
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		if (e!= null)
			e.printStackTrace(pw);
		sbError.append(sw.toString());

		sendEmail(sbError.toString());
		
		try {
			Http.post(LOG_URL, 
					String.format("{\"site_id\":\"%s\", \"status\": \"3\", \"message\":\"%s %s\"}", site_id, "Crawl error", sbError.toString()));
		} catch (Exception ex) {
			// ignore
		}
	}
	
	public static void sendEmail(String body) {
		Email email = EmailBuilder.startingBlank()
			    .from("Plug-N-Play.ai Crawler", "teodor.g@nciphers.com")
			    .to("Atanas Walks", "akrachev@gmail.com")
			    .withSubject("Crawler Error")
			    .withPlainText(body)
			    .buildEmail();

			Mailer mailer = MailerBuilder
			    .withSMTPServer("mail.nciphers.com", 26, "teodor.g@nciphers.com", "YouTube#123")
			    .withTransportStrategy(TransportStrategy.SMTP)
			    .buildMailer();

			mailer.sendMail(email);					
	}
}
