import java.io.*;
import java.util.*;

public class DataGenerator{
    private File file;
    private String website;

    public DataGenerator(){
        file = new File("mrt_stations.txt");
        website = "https://en.wikipedia.org/wiki/List_of_Singapore_MRT_stations";
    }

    public void run(){
        new GetMrtStations(file, website).parse();
        //new Converter(file).convert();
    }

    public static void main(String args[]) {
        new DataGenerator().run();
    }
}