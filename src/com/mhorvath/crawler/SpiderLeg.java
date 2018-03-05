package com.mhorvath.crawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg
{
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static final String WIKIHOW_RANDOM_URL = "https://www.wikihow.com/Special:Randomizer";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;


    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl()
    {
        try
        {
            Connection connection = Jsoup.connect(WIKIHOW_RANDOM_URL).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            Elements linksOnPage;
            String pageUrl;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code, indicating that everything is great.
            {
                //linksOnPage = htmlDocument.select("a[href]");
                pageUrl = htmlDocument.select("a[href]").get(0).absUrl("href");
                if(!Spider.getPagesVisited().contains(pageUrl))
                {
                	Spider.NUM_PAGES_VISITED++;
                	System.out.println("\n**Visiting** Received web page at " + pageUrl);
                    Spider.addVisitedUrl(pageUrl);
                }
                
            }
            else
            {
            	System.out.println("\n**Failure** Status code was: " + connection.response().statusCode());
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements imagesOnPage = htmlDocument.select("img[src$=.jpg]");
            System.out.println("Found (" + imagesOnPage.size() + ") images");
            for(Element imageLink : imagesOnPage)
            {
                this.links.add(imageLink.absUrl("src"));
            }
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }


    public List<String> getLinks()
    {
        return this.links;
    }

}
