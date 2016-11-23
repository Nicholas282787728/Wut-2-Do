import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

// This file is incomplete. It returns all the MRT stations including those that have yet to be constructed.
// As such, manual work is required to remove the other MRT stations.
// Also, there are random stations such as "reserved stations" that requires manual filtering.
// This generates a file called "mrt_stations.txt" which contains the list of MRT stations.
// Then, use this program in conjunction with MrtStationsReplace.java to convert it from
// e.g "Pasir Ris MRT" to "Pasir+Ris+MRT" to be used for geocoding

class GetMrtStations{
    private PrintStream file;
    private Document doc;

    public GetMrtStations(File file, String website){
        try{
            this.file = new PrintStream(file);
            doc = Jsoup.connect(website).get();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(){
        Elements transverseTree = doc.body().select("div#bodyContent > div#mw-content-text > table[class = wikitable] > tbody > tr:not([bgcolor])");
        ArrayList<String> nodes = new ArrayList<String>();
        for (Element nodeTR : transverseTree) {
			// locations is the list of elements with tag <tr>
            // if this text does not exist, then add it in so there's no repeats
            // concatenate the phrase " MRT" into the text
            if(!nodes.contains(nodeTR.child(1).text() + " MRT"))
                nodes.add(nodeTR.child(1).text() + " MRT");
        }
        for(int i=0; i<nodes.size(); i++)
            file.println(nodes.get(i));
    }
}