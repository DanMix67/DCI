package dci.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dci.webimporation.DataCreator;

public class Main
{

    /*
     * DataCreator object for class
     */
    private static DataCreator dataCreator;

    /*
     * List of urls
     */
    private static List<String> urls;

    public static void main(String[] args) throws Exception
    {
        urls = new ArrayList<String>();
        readUrls();
        dataCreator = new DataCreator(urls);
        dataCreator.downloadData();
        Map<String, String> webData = dataCreator.getWebData();
        System.out.println(webData);
    }

    private static void readUrls()
    {
        try (BufferedReader br = new BufferedReader(
                new FileReader(System.getProperty("user.dir") + "\\resources\\DCIShows.txt")))
        {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null)
            {
                urls.add(sCurrentLine);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}