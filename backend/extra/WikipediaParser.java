import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

//Learning Point: Number of Element in Elements are siblings. .size() calculates how many siblings are there.
// .children() refers to the children. .childNodes() return Nodes, not Elements.
// .select() can get everything in the document. but getElementBy*() only gets the children.

class WikipediaParser{
    private PrintStream file;
    private Document doc;

    public WikipediaParser(File file, String website){
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
        Elements transverseTree = doc.body().select("div#bodyContent > div#mw-content-text > table[class = wikitable sortable]");
            /*
            Above line is the concise version of the below 5 lines;
            Element ele1 = doc.body().getElementById("content");
            ele1 = ele1.getElementById("bodyContent");
            ele1 = ele1.getElementById("mw-content-text");
            transverseTree initially only has one element. So we use .first() to change it from Elements to Element.
			transverseTree = transverseTree.first().getElementsByAttributeValueContaining("class", "wikitable sortable"); 
            */

        for (Element node : transverseTree) {
			boolean isLast = false;
            // locations is the list of elements with tag <tr>;
            Elements locations = node.select("tbody > tr");
			// remove the unnecessary header in index 0; not sure why it is appearing though
            locations.remove(0);
 			// for every tag <tr>; 
            for(Element location : locations){ 	
                if(node.equals(transverseTree.last()) && location.equals(locations.last())){
                    isLast = true;
                }				
 					//.child(0) refers to name of the location, childNodes()
                    // refer to the text nodes, and get(0) refers to the 
                    // node at 0 index i.e before the first tag (i.e <br>)
                List<Node> texts = location.child(0).childNodes();
                int j = 0;
                    // iterate till the first text, which is the name of location without additional details
                    // e.g The Grand Cathay
                while(!(texts.get(j).nodeName().equals("#text"))){
                    j++;
                }
                file.println(texts.get(j).toString().trim());

 					//.child(3) refers to address of the location
                    // sanitize input before output as .text();
                texts = location.child(3).childNodes();
                for(Node text : texts){
                    if(text.nodeName().equals("sup")){
                        text.remove();
                        break;
                    }
                }

                if(!isLast){
                    file.println(location.child(3).text().trim());
                    file.println();
                }
                else{
                    file.print(location.child(3).text().trim());
                }
            }
        }
    }
}