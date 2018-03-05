package com.mhorvath.crawler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

public class Spider {
	private static final int IMAGES_TO_GET = 100;
	private static final String DOWNLOAD_DISK_LOCATION = "E://Eclipse//WikiHowWebcrawler_images//";
	private static final String EDITED_IMAGES_DISK_LOCATION = "E://Eclipse//WikiHowWebcrawler_images//Edited//";
	private static final String FILEFORMAT_JPG = "jpg";
	private static final String FILEFORMAT_PNG = "png";
	private static final int WIKIHOW_IMAGE_WIDTH = 728;
	private static final int WIKIHOW_IMAGE_HEIGHT = 546;
	public  static int NUM_PAGES_VISITED = 0;
	private static Set<String> pagesVisited = new HashSet<String>();
	private static List<String> pagesToVisit = new LinkedList<String>();
	private static int downloadedImages = 0;
	
	private static int TITLE_COVER_X = 298;
	private static int TITLE_COVER_Y = 526;
	private static int TITLE_COVER_WIDTH = 450;
	private static int TITLE_COVER_HEIGHT = 20;
	
	SpiderLeg leg = new SpiderLeg();
	
	  /**
	   * Our main launching point for the Spider's functionality. Internally it creates spider legs
	   * that make an HTTP request and parse the response (the web page).
	   * 
	   * @param url
	   *            - The starting point of the spider
	   */
	  public void work()
	  {
		 
	      while(leg.getLinks().size() < IMAGES_TO_GET)
	      {
	          leg.crawl(); // Lots of stuff happening here. Look at the crawl method in SpiderLeg

	          pagesToVisit.addAll(leg.getLinks());
	      }
	      System.out.println("\n**Done** Visited " + pagesVisited.size() + " web page(s), collected " + leg.getLinks().size() + " images.");

	      for(String imageLink : leg.getLinks()){
	    	  System.out.println(imageLink);
	      }
	      this.downloadImages();
	      this.coverTitle();
	  }
	  
	  public static void addVisitedUrl(String url)
	  {
		  pagesVisited.add(url);
	  }
	  
	  public static Set<String> getPagesVisited()
	  {
		  Set<String> copy = new HashSet<String>();
		  copy.addAll(pagesVisited);
		  return copy;
	  }
	  
	  private void downloadImages()
	  {
		  
		  Iterator<String> linksIterator = leg.getLinks().iterator();
			  while(linksIterator.hasNext() && downloadedImages < IMAGES_TO_GET)
			  {
				  try
				  {
					  URL url = new URL(linksIterator.next());
					  InputStream in = new BufferedInputStream(url.openStream());
					  ByteArrayOutputStream out = new ByteArrayOutputStream();
					  byte[] buf = new byte[1024];
					  int n = 0;
					  while (-1!=(n=in.read(buf)))
					  {
					     out.write(buf, 0, n);
					  }
					  out.close();
					  in.close();
					  byte[] response = out.toByteArray();
					  FileOutputStream fos = new FileOutputStream(DOWNLOAD_DISK_LOCATION + String.format("%03d", downloadedImages) + "." + FILEFORMAT_JPG );
					  fos.write(response);
					  fos.close();
					  downloadedImages++;
					  System.out.println("Downloaded " + downloadedImages + " image(s)." );
				  }catch(Exception e)
				  {
					  System.out.println(e.getMessage());
				  }
				 
			  }
	  }
	  
	  private void coverTitle()
	  {
		  File folder = new File(DOWNLOAD_DISK_LOCATION);
		  File[] listOfFiles = folder.listFiles();
		  BufferedImage img;

		      for (int i = 0; i < listOfFiles.length; i++) {
		        if (listOfFiles[i].isFile()) {
		        	System.out.println("Covering WikiHow title for " + listOfFiles[i].getName());
		        	try {
		    			img = ImageIO.read(new File(DOWNLOAD_DISK_LOCATION + listOfFiles[i].getName()));
		    			if(img.getWidth() == WIKIHOW_IMAGE_WIDTH && img.getHeight() == WIKIHOW_IMAGE_HEIGHT) //Check if it's a proper image
		    			{
			    			// Obtain the Graphics2D context associated with the BufferedImage.
			    			Graphics2D g = img.createGraphics();
	
			    			// Draw on the BufferedImage via the graphics context.
			    			g.setColor(Color.BLACK);
	
			    			
			    			g.fill(new Rectangle2D.Double(TITLE_COVER_X, TITLE_COVER_Y, TITLE_COVER_WIDTH, TITLE_COVER_HEIGHT));
			    			// Clean up -- dispose the graphics context that was created.
			    			g.dispose();
			    			
			    			 File outputfile = new File(EDITED_IMAGES_DISK_LOCATION + listOfFiles[i].getName().substring(0, listOfFiles[i].getName().indexOf(".")) + "_edited." + FILEFORMAT_PNG);
			    			 ImageIO.write(img, "png", outputfile);
		    			}
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		        }
		      }
	  }
}
