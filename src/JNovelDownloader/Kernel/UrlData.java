package JNovelDownloader.Kernel;


import JNovelDownloader.Option.Option;

import java.io.IOException;

import javax.swing.JTextArea;



public class UrlData {
	public String urlString;
	public String domain;
	
	public String bookname;
	public String author;
	public double initTime, startTime, donTime, totTime;
	public int downloaded;
	public int page;
	public int Tid;
	public boolean wrongUrl;
	
	public Downloader downloader;
	public ReadHtml reader;
	
	public UrlData() {
		downloader = new Downloader();
		reader = new ReadHtml();
	}
 
    
}
