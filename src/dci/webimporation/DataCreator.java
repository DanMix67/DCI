package dci.webimporation;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dci.corp.Corps;
import dci.show.Event;

public class DataCreator
{

    /*
     * List of urls for data
     */
    private List<String> urlList;

    /*
     * WebReader for the class
     */
    private final WebReader webReader;

    /*
     * Contains a map of the url to the web data on it
     */
    private Map<String, String> webData;

    /*
     * DocumentBuilderFactory for class
     */
    private DocumentBuilderFactory factory;

    private final String TABLE_TAG = "Table";
    private final String TD_TAG = "td";
    private final String CLASS_TAG = "class";
    private final String CAPTION = "captionTotal";
    private final String SUB_CAPTION = "subcaptionTotal";
    private final String CONTENT_SCORE = "content score";
    private final String TEAM_CLASS = "content topBorder rightBorderDouble";
    private final String CATEGORY = "categoryTotal";

    /**
     * Creates data structure
     * 
     * @param urls
     */
    public DataCreator(List<String> urls)
    {
        urlList = urls;
        webReader = new WebReader();
        webData = new HashMap<String, String>();
        factory = DocumentBuilderFactory.newInstance();
    }

    /**
     * Downloads the data from each url into the map
     * 
     * @throws Exception
     */
    public void downloadData() throws Exception
    {
        // Places the data from each url into the webData map
        for (String url : urlList)
        {
            String newWebData = WebReader.getText(url);
            webData.put(url, newWebData);
        }
    }

    /**
     * Reads data from each site into the proper objects
     * 
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void readData() throws ParserConfigurationException, SAXException, IOException
    {
        for (String url : webData.keySet())
        {
            String siteData = webData.get(url);
            process(siteData);
        }
    }

    public void process(String siteData) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(siteData)));
        Element rootElement = document.getDocumentElement();

        Event currentEvent = new Event();

        HashMap<Integer, String> captionHeads = new HashMap<Integer, String>();
        HashMap<Integer, String> subcaptionHeads = new HashMap<Integer, String>();
        Integer captionCounter = 1;
        Integer subcaptionCounter = 1;

        HashMap<String, HashMap<Integer, String>> teamScores = new HashMap<String, HashMap<Integer, String>>();
        Integer scoreCounter = 1;
        String currentTeam = null;

        NodeList children = rootElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            Node ren = children.item(i);
            if (TABLE_TAG.equals(ren.getNodeName()))
            {
                NodeList tableElements = ren.getChildNodes();
                for (int j = 0; j < children.getLength(); j++)
                {
                    Node tdNode = tableElements.item(j);
                    if (TD_TAG.equals(tdNode.getNodeName()))
                    {
                        NamedNodeMap tdAttributes = tdNode.getAttributes();
                        Node classNode = tdAttributes.getNamedItem(CLASS_TAG);
                        String classString = classNode.getNodeValue();
                        String tdValue = tdNode.getNodeValue();

                        if (classString.contains(CAPTION))
                        {
                            captionHeads.put(captionCounter, tdValue);
                            captionCounter++;
                        } else if (classString.contains(SUB_CAPTION) || classString.contains(CATEGORY))
                        {
                            subcaptionHeads.put(subcaptionCounter, tdValue);
                            subcaptionCounter++;
                        } else if (classString.equals(TEAM_CLASS))
                        {
                            currentTeam = tdValue;
                            HashMap<Integer, String> scores = new HashMap<Integer, String>();
                            teamScores.put(currentTeam, scores);
                            scoreCounter = 1;
                        } else if (classString.contains(CONTENT_SCORE))
                        {
                            HashMap<Integer, String> currentScore = teamScores.get(currentTeam);
                            currentScore.put(scoreCounter, tdValue);
                            scoreCounter++;
                        }
                    }
                }
            }
        }

        createData(currentEvent);
    }

    private void createData(Event currentEvent)
    {

    }

    public Map<String, String> getWebData()
    {
        return webData;
    }

}