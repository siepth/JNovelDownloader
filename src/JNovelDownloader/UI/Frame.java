package JNovelDownloader.UI;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import JNovelDownloader.Kernel.Analysis;
import JNovelDownloader.Kernel.DownloadThread;
import JNovelDownloader.Kernel.Downloader;
import JNovelDownloader.Kernel.ReadHtml;
import JNovelDownloader.Kernel.UrlData;
import JNovelDownloader.Option.About;
import JNovelDownloader.Option.Option;

public class Frame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<UrlData> booklist;
	private TextFiled urlTextField;
	private TextFiled authorTextField;
	private TextFiled bookNameTextField;
	private JTextField pageTextField;
	private JButton downloadButton;
	private JButton restartButton;
	//private JButton parseButton;
	private JLabel urlLabel;
	private JLabel authorLabel;
	private JLabel bookNameLabel;
	private JLabel pageLabel;
	private JPanel urlPanel;
	private JPanel downloadPanel;
	private JPanel bookNamePanel;
	
	private JTextArea resultTextArea;
	private JScrollPane resultScrollPane;
	private JPanel resultPanel;

	private JTextArea reportTextArea;
	private JScrollPane reportScrollPane;
	private JPanel reportPanel;
	//private Thread currentthread;
	
	
	private JButton settingButton;
	private double theNewVersion;

	//public Frame(final Downloader downloader, final ReadHtml readHtml,
	public Frame(
			final Option option) throws Exception {
		super(About.tittle + "-" + About.version + "  by " + About.author);
		setLayout(new FlowLayout()); // set frame layout
		//this.currentthread = null;
		/********************** 設定 ***************************/
		settingButton = new JButton("Setup");
		settingButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				OptionFrame frame = new OptionFrame(option, reportTextArea);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setSize(550, 350);
				frame.setVisible(true);
			}
		});
		add(settingButton);
		/********************** 設定書名 ***************************/
		bookNameLabel = new JLabel("Title");
//		String os = System.getProperty("os.name").toLowerCase();
//		if(os.indexOf("win") >= 0)
//		{
			bookNameTextField = new TextFiled("", 20);
//			
//		}
//		else
//		{
//			bookNameTextField = new JTextFieldSelf("", 20);
//		}

		booklist = new LinkedList<UrlData>();
		LinkedList<String> urlList = new LinkedList<String>();
		bookNamePanel = new JPanel();
		bookNamePanel.add(bookNameLabel);
		bookNamePanel.add(bookNameTextField);

		authorLabel = new JLabel("Author");
//		if(os.indexOf("win") >= 0)
//		{
			authorTextField = new TextFiled("", 20);
//		}
//		else
//		{
//			authorTextField = new JTextFieldSelf("", 20);
//		}

		bookNamePanel.add(authorLabel);
		bookNamePanel.add(authorTextField);
		add(bookNamePanel);
		/********************** 網址輸入 ***************************/
		urlLabel = new JLabel("URL：");
		urlPanel = new JPanel();
		// 定義輸入框
		urlPanel.add(urlLabel);
		urlTextField = new TextFiled("", 50); // 網址輸入視窗
		urlPanel.add(urlTextField);
		add(urlPanel);
		pageLabel = new JLabel("To Pages?");
		pageTextField = new JTextField("0", 4);
		downloadPanel = new JPanel();
		downloadPanel.add(pageLabel);
		downloadPanel.add(pageTextField);
		
		restartButton = new JButton("Reset");
		restartButton.addActionListener (new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent arg0) {
				resultTextArea.setText(null);
				//do nothing
			}
		 // test	
		});
		
		
		
		downloadButton = new JButton("Start");
		
		downloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				 new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO 自動產生的方法 Stub
						double startTime, donTime, totTime;
						UrlData currentbook;

						int skip = 0;
						startTime = System.currentTimeMillis();
						// reset the content
						//resultTextArea.setText(null);
						//downloadButton.disable();
						String url = urlTextField.getText();
						
						//if (url == null || url.equals("")) {
						if (url.isEmpty()) {
							resultTextArea.append("no input url...\r\n");
							skip = 1;
						}
						else {
							
							for (int ii = 0; ii < urlList.size(); ii++){
							
								if (urlList.get(ii).equals(url)){
									resultTextArea.append("book [" + urlTextField.getText() + "]..is already progressing...\r\n");
									skip = 1;
								}
							}
						}
						if (skip == 0){
							urlList.add(urlTextField.getText());
						try {
							resultTextArea.append("Initializing..[" + urlTextField.getText() + "]...\r\n");
							currentbook = check(option,pageTextField.getText(),
									bookNameTextField.getText(),
									authorTextField.getText(),urlTextField.getText());
							//downloadButton.enable();
							resultTextArea.setCaretPosition(resultTextArea.getText().length());
							if (currentbook != null) {// 確認所有該填的資料都有填寫
								// 下載、建書兩大元件初始化
								booklist.add(currentbook);
								//resultTextArea.append("Initializing..\r\n");
								currentbook.downloader.setUP(currentbook, resultTextArea);// 分析網址
								currentbook.reader.setUp(option.threadNumber,
										currentbook, 
										resultTextArea);
								//
								//resultTextArea.append("Start working\r\n");
								// resultTextArea.paintImmediately(resultTextArea
								// .getBounds());
								//resultTextArea.setCaretPosition(resultTextArea
								//		.getText().length());
								try {
									
									if (! currentbook.downloader.downloading(option, currentbook.reader, resultTextArea)) {
										resultTextArea.append("Download failure\r\n");// 下載失敗
										// resultTextArea
										// .paintImmediately(resultTextArea
										// .getBounds());
									} else {
										donTime = System.currentTimeMillis() - startTime;
										
										if (currentbook.reader.makeBook(option)) {// 開始解析所有的網頁
											resultTextArea.append("Text file created\r\n");
											
											currentbook.reader.delTempFile();
											
											resultTextArea.append("Clearing temp files\r\n");
											resultTextArea.setCaretPosition(resultTextArea.getText().length());

											totTime = System.currentTimeMillis() - startTime;
											int seq = booklist.indexOf(currentbook);
											DecimalFormat formatter = new DecimalFormat ("#,###");
											reportTextArea.append("(" + seq + ") [" + currentbook.bookname + 
													"] Data processed: " + formatter.format(currentbook.reader.getBytesIn()) + 
													"-->" + formatter.format(currentbook.reader.getBytesOut())
													+ " Words, Total process time: " + totTime / 1000 + "s"
													+ "; Download time: " + donTime / 1000 + "s"
													+ "; Data processing time: " + (totTime - donTime)/1000 + "s"
													+ "\r\n");
											reportTextArea.setCaretPosition(reportTextArea.getText().length());

											bookNameTextField.setText("");
											authorTextField.setText("");
											urlTextField.setText("");
											pageTextField.setText("0");
											
										}
										else
										{
											resultTextArea.append("Cannot make book!\r\n");
											resultTextArea.setCaretPosition(resultTextArea.getText().length());
										}
									}
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								resultTextArea.append("Download Fail!");
								resultTextArea.setCaretPosition(resultTextArea.getText().length());
							}
						} catch (NumberFormatException e) {
							// TODO 自動產生的 catch 區塊
							e.printStackTrace();
						} catch (IOException e) {
							// TODO 自動產生的 catch 區塊
							e.printStackTrace();
						}
						} //end of "skip"

					}

				}).start();
				// TODO Auto-generated method stub //下載指令放置處
				/************* 下載指令 *********/

			}
		});
		downloadPanel.add(downloadButton);
		downloadPanel.add(restartButton);
		/*
		parseButton = new JButton("偵測書名");
		parseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!urlTextField.getText().equals("")) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								int page=getPage(option, urlTextField.getText());
								String title = getTittle(option);
								String regex = "";
								regex = "([\\[【「（《［].+[\\]】」）》］])?\\s*[【《\\[]?\\s*([\\S&&[^】》]]+).*作者[】:：︰ ]*([\\S&&[^(（《﹝【]]+)";
								Matcher matcher;
								Pattern p;
								p = Pattern.compile(regex);
								matcher = p.matcher(title);
								if (matcher.find()) {
									bookNameTextField.setText(matcher.group(2));
									authorTextField.setText(matcher.group(3));
									pageTextField.setText(String.valueOf(page));
									resultTextArea.append("偵測完成，如有錯誤請手動修改。\r\n");
									resultTextArea.setCaretPosition(resultTextArea.getText().length());	
								} else {
									resultTextArea.append("偵測失敗\r\n");
									resultTextArea.setCaretPosition(resultTextArea.getText().length());	
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								resultTextArea.append("偵測失敗\r\n");
								resultTextArea.setCaretPosition(resultTextArea.getText().length());			
							}
						}
					}).start();
				} else {
					resultTextArea.append("因網址空白導致偵測失敗");
					resultTextArea.setCaretPosition(resultTextArea.getText().length());
				}
			}
		});
		downloadPanel.add(parseButton);
		*/
		add(downloadPanel);
		reportTextArea = new JTextArea(4, 50); // report
		reportTextArea.setLineWrap(true);
		reportScrollPane = new JScrollPane(reportTextArea);
		reportScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		reportScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		reportPanel = new JPanel();
		reportPanel.add(reportScrollPane);
		add(reportPanel);
		
		
		
		resultTextArea = new JTextArea(8, 50);// 訊息視窗
		resultTextArea.setLineWrap(true);
		resultScrollPane = new JScrollPane(resultTextArea);
		resultScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		resultScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultPanel = new JPanel();
		resultPanel.add(resultScrollPane);
		add(resultPanel);

		resultTextArea.append("Starting program...\r\n");

		option.printOption(reportTextArea);// 印出初始訊息
		
		DetectClipboardThread dClipboardThread = new DetectClipboardThread();
		dClipboardThread.start();

	}
	
	class DetectClipboardThread extends Thread implements Runnable {
		
		public void run() {
			Timer timer = new Timer();
			timer.schedule(new DateTask(), 1000, 2000); // check every 2 seconds
		}
	}
	
	class DateTask extends TimerTask {
		
		DetectClipboard dc = new DetectClipboard();
		
		@Override
		public void run() {
			if(dc.getClipboard().contains("ck101")) {
				urlTextField.setText(dc.getClipboard());
			}
		}
	}
	
	public class DetectClipboard implements ClipboardOwner {
	    private Clipboard clipboard;

	    public DetectClipboard() {
	    	DetectClipboard_init();
	    }

	    public void DetectClipboard_init(){
	        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    }

	    /**
	     * get Clipboard
	     * @return
	     */
	    public String getClipboard(){
	        Transferable content = clipboard.getContents(this);
	        try{
	            return (String) content.getTransferData(DataFlavor.stringFlavor);
	        }catch(Exception e){
	            e.printStackTrace();
	            //System.out.println(e);
	        }
	        return null;
	    }
	    
	    public void lostOwnership(Clipboard clipboard, Transferable contents) {
	        //System.out.println("lostOwnership...");
	    }
	}
 
	
	public void popVersionAlert(Option option) {
		try {
			theNewVersion = checkVersion(option);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (theNewVersion > About.versionNumber) {
			resultTextArea
					.append("The newest version: "
							+ String.valueOf(theNewVersion)
							+ "please visit http://www.pupuliao.info to update\r\n");
		} else {
			resultTextArea.append("Current newest version:" + String.valueOf(theNewVersion)
					+ "\r\n");
			resultTextArea.append("title, author can be detected automatically by just input URL\r\n");
		}
		if (theNewVersion > About.versionNumber) {
			JOptionPane.showMessageDialog(null,
					"The newest version is " + String.valueOf(theNewVersion) + " please visit official website to update",
					"Update available!", JOptionPane.WARNING_MESSAGE);
		}else if(option.replace !=true){
			JOptionPane.showMessageDialog(null,
					"New function of slang recovery can be enabled in setup",
					"Tips", JOptionPane.WARNING_MESSAGE);
		}
	}

	private UrlData check(Option option,String page, String bookName, String author,String url) throws IOException {
		UrlData book = null;
		
		book=Analysis.analysisUrl(url);
		
		if (book.wrongUrl) {
				resultTextArea.append("URL is not able to analyze\r\n");
				return null;
		}
	
		resultTextArea.append("Start to analyze [" + url + "]...\r\n");
		resultTextArea.setCaretPosition(resultTextArea.getText().length());	
		String tempBooknameString ="";
		String tempAuthorString = "";
		int pageNumber;
		
		//if(page.equals("0") || page.isEmpty()|| !page.matches("[1-9][0-9]*"))
			pageNumber=getPage(option, url);
		//else pageNumber = Integer.parseInt(page);
		
		//if(bookName.isEmpty() || author.isEmpty()){
			String title = getTittle(option, url);
			System.out.print("["+title+"]");
			String regex = "";
			regex = "\\s*([\\[【「（《［].+[\\]】」）》］])?\\s*[【《\\[]?\\s*([\\S&&[^】》]]+).*作者[】:：︰ ]*([\\S&&[^(（《﹝【]]+)";
			Matcher matcher;
			Pattern p;
			p = Pattern.compile(regex);
			matcher = p.matcher(title);
			tempBooknameString ="";
			tempAuthorString = "";
			if (matcher.find()) {
				/*
				resultTextArea.append( ">>original>>" + title
				+ " match (" + matcher.groupCount() + ")"	
				+ ">0>" + matcher.group(0) + ">1>" + matcher.group(1)
				+ ">2>" + matcher.group(2) + ">3>" + matcher.group(3));
				*/
				tempBooknameString = matcher.group(2);
				tempAuthorString = matcher.group(3);
				//return false; //debug
				
				
			}else{
				tempBooknameString = title;
			}
		//}
		book.bookname = tempBooknameString;
		book.author = tempAuthorString;
		book.page = pageNumber;
	
		//if(page.equals("0") || page.isEmpty()|| !page.matches("[1-9][0-9]*"))	{
			pageTextField.setText(String.valueOf(pageNumber));
		//}
		//if(bookName.isEmpty()){
			bookNameTextField.setText(tempBooknameString);
		////}
		//if(author.isEmpty()){
			authorTextField.setText(tempAuthorString);
		//}
		return book;
	}

	private double checkVersion(Option option) throws Exception {
		//String targetURL = "http://code.google.com/p/jnoveldownload/downloads/list";
		String targetURL = "https://sourceforge.net/projects/jnoveldownload/files";
		String to = option.tempPath + "version.html";
		double version = 0;
		DownloadThread downloadThread = new DownloadThread(targetURL, to, 0);
		try {
			downloadThread.start();
			downloadThread.join();
		} catch (Exception e) {
			// TODO: handle exception
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(to), "UTF-8"));
		// <a href="detail?name=JNovelDownloader_v2_1.jar&amp;can=2&amp;q=">
		String temp;
		while ((temp = reader.readLine()) != null) {
			if (temp.indexOf("<tr title=\"JNovelDownloader_v") >= 0) {
				String temp2[] = temp.split("_");
				version = Double.parseDouble(temp2[1].charAt(1) + "."
						+ temp2[2].charAt(0));
				break;
			}
		}
		reader.close();

		return version;
	}

	public void popPathAlert() {
		JOptionPane.showMessageDialog(null, "There are troubles with your download path or temp file path, please check the setting",
				"Wrong Path", JOptionPane.WARNING_MESSAGE);
	}
	
	private int getPage(Option option,String url) throws IOException {
		int result = 0;
		String fn;
		
		fn = option.tempPath + url.replaceAll("[:;/!\\.]", "") + ".tmp";
		//System.out.println(fn + "-->" + fn.replaceAll("[:;/!\\.]", ""));

		
		
		DownloadThread downloadthread = new DownloadThread(url, fn, 1);
		downloadthread.start();
		try {
			downloadthread.join();
		} catch (InterruptedException e) {

		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fn),  "UTF-8"));
		String temp;
		String temp2[];
		while ((temp = reader.readLine()) != null) {
			if (temp.indexOf("class=\"pg\"") >= 0) {
				if (temp.indexOf("class=\"last\"") >= 0) {
					temp2 = temp.split("class=\"last\">");
					temp2 = temp2[1].split("</a>");
				//	System.out.print(temp2[0]);
				//	System.out.print(temp2[0].replaceAll("\\.", ""));
					temp2[0] = temp2[0].replaceAll("\\.", "");
					temp2[0] = temp2[0].replaceAll(" ", "");
				//	System.out.print(temp2[0]);
					result = Integer.parseInt(temp2[0]);
				}else if(temp.indexOf("class=\"nxt\"") >= 0){
					temp2 = temp.split("class=\"nxt\"");
					temp2 = temp2[0].split("<a href");
					temp2 = temp2[temp2.length-2].split("</a>");
					temp2 = temp2[0].split(">");
					result = Integer.parseInt(temp2[1]);
				}else if(temp.indexOf("<strong>")>=0){
					temp2 = temp.split("<strong>");
					temp2 = temp2[1].split("</strong>");
					result = Integer.parseInt(temp2[0]);
				}
				break;
			}
		}
		reader.close();
		return result;

	}

	private String getTittle(Option option, String url) throws IOException {// 必須要先執行過getPage
		
		String fn;
		
		//System.out.println(fn + "-->" + fn.replaceAll("[:;/!\\.]", ""));
		fn = option.tempPath + url.replaceAll("[:;/!\\.]", "") + ".tmp";

		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fn), "UTF-8"));
		String temp;
		String temp2[];
		String result = null;
		int title = 0;
		while ((temp = reader.readLine()) != null) {
			if (temp.indexOf("<title>") >= 0) {
				
				//System.out.println(temp);
				temp2 = temp.split("title>");
				while(temp2.length<2){
					temp += reader.readLine();
					temp2 = temp.split("title>");
					System.out.println("title[0]:" + temp2[0] + "; title[1]:" + temp2[1]);
				}
				temp2 = temp2[1].split(" - ");
				result = temp2[0];
//				result = Replace.replace(result, "【", "[");
//				result = Replace.replace(result, "】", "]");
//				result = Replace.replace(result, ":", "");
//				result = Replace.replace(result, " ", "");
				break;
			}
		}
		reader.close();
		return result;
	}

}
