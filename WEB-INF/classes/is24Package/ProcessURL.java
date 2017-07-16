package is24Package;

/*
 * Name: Aaron May
 * Date: 06/11/16
 *
 * This program takes from a form the url parameter and uses that to be parsed by JSOUP.
 * The contents of this are then extracted and sent back to the View layer.
 */

//Servlet imports
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.lang.IllegalArgumentException;
import java.util.*;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class ProcessURL extends HttpServlet 
{

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  { 
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String url = request.getParameter("url");//get url text from form
    request.setAttribute("url",url);//set URL to be output later
    Document doc = null;

    try {
      doc = Jsoup.connect(url).get();//parse URL and store
    } catch(UnknownHostException | IllegalArgumentException | SocketTimeoutException ex) {
      request.setAttribute("errorMessage", "An error occured reaching your URL. Please try again.");
      request.setAttribute("processed",false);//notify that processing has completed
      RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
      rd.forward(request, response);//return to input as an error occured
      return;
    }

    request.setAttribute("htmlVersionType",getHTMLVersion(doc));//store HTML VERSION
    request.setAttribute("pageTitle",doc.title());//Store PAGE TITLE
    request.setAttribute("headerCount",getHeaders(doc));//Store list of PAGE HEADERS
    
    /*Store LINK COUNT*/
    int[] linkCount = new int[2];//used to store both internal and external link counts
      for (int i = 0; i < linkCount.length; i++) {
        linkCount[i] = 0;
    }

    int[] tempArray = getLinks(doc, "a[href]",url);//a href (standard hyperlink),                                 
    linkCount[0] += tempArray[0];
    linkCount[1] += tempArray[1];

    tempArray = getLinks(doc, "[src]",url);//src (link to an image)
    linkCount[0] += tempArray[0];
    linkCount[1] += tempArray[1];

    tempArray = getLinks(doc, "link[href]",url);//link (link to import like css)
    linkCount[0] += tempArray[0];
    linkCount[1] += tempArray[1];

    request.setAttribute("linkCount", linkCount);//Store LINK COUNTER totals

    request.setAttribute("containsLoginForm", containsLoginForm(doc));//Store if url contains a LOGIN FORM
    
    request.setAttribute("processed",true);//notify that processing has completed
    RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
    rd.forward(request, response);//return to main home
    return;
  }

  /*
   * Captures doctype from the the document, then tests whether it is html5 or later
   * Formatting guidelines taken from http://www.w3schools.com/tags/tag_doctype.asp
   */
  private static String getHTMLVersion(Document doc) {
    String docType = doc.toString().substring(0, doc.toString().indexOf(">") + 1).toUpperCase();
    if (docType.equals("<!DOCTYPE HTML>")) { //HTML 5
      return "HTML 5";
    }
    else { //HTML 4 and XHTML
      return docType.substring(docType.indexOf("DTD") + 4, docType.indexOf("//EN"));
    }
  }

 /*
  * Retreives all header elements from doc and stores them into an Elements object
  * Then stores header counts into array starting from position 0 for h1, position 1 for h2 and so on.
  * Array is then returned
  */

  private int[] getHeaders(Document doc) {
    Elements hTags = doc.select("h1, h2, h3, h4, h5, h6");//store all elements that contain a header tag
    int[] headerCount = new int[6];//store headerCount totals
    for (int i = 0; i < headerCount.length; i++) {//initialize all positions to zero
      headerCount[i] = hTags.select("h" + Integer.toString(i + 1)).size();
    } 
    return headerCount;
  }

  /*
   * Parameter linkType is used to get multiple types of links e.g. a href (standard hyperlink), 
   * src (link to an image), link (link to import like css)
   * returns an array of 2 ints, the first is the count of internal links, 
   * the second is a list of external links
   *
   * Definition of internal or external link
   * internal = contains the domain within any part of the link e.g. (github), or doesnt contain WWW && HTTP
   * external = doesnt contain domain and either has WWW or HTTP
   */
  private int[] getLinks(Document doc, String linkType, String urlArg) throws MalformedURLException{
    URL url = new URL(urlArg);
    String formatedUrl = "";

    /*Remove http or www depending on string format and store into formatedUrl*/
    if (url.getHost().indexOf("www") != -1) {
      formatedUrl = url.getHost().substring(url.getHost().indexOf(".") + 1);//contains www
    }
    else {//strip the http(s)://
      formatedUrl = url.toString().substring(url.toString().lastIndexOf("/") + 1);
    }

    int[] linkCount = new int[2];
    for (int i = 0; i < linkCount.length; i++) {
      linkCount[i] = 0;//initialize all positions to zero
    }
    
    Elements links = doc.select(linkType);
      for (Element e : links) {//test if internal or external link
        if (e.toString().indexOf(formatedUrl) != -1 || (e.toString().indexOf("www.") == -1 && e.toString().indexOf("http") == -1)) {
          linkCount[0] += 1;//internal links
        }
        else {
          linkCount[1] += 1;//external links
        }
      }
      return linkCount;
  }

  /*
   * Login applied is those which contain a form with a password and either an input id which contains 'login' or 'name' and
   * an input field that is text type OR
   * Returns "Yes" is a login form is found using the above parameters otherwise it returns "No"
   */
  private String containsLoginForm(Document doc) {
    Elements forms = doc.select("form");//get all the forms within the page
    for (Element e : forms) {//loop through each form
      if (e.select("input[type='Password']").size() > 0 && 
         (e.select("input[id]").toString().indexOf("login") != -1 || e.select("input[id='name']").size() > 0) && 
          e.select("input[type='text']").size() > 0) {    
        return "Yes";
      }
    }
    return "No";
  }
}