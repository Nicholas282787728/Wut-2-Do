import java.io.*;
import java.util.*;

// this program replaces e.g "Pasir Ris MRT" to "Pasir+Ris+MRT"

public class MrtStationsReplace{
    private File fileInput;
    private PrintStream fileOutput;
    private Scanner sc;

    public MrtStationsReplace(){
        fileInput = new File("mrt_stations.txt");
        try{
            sc = new Scanner(fileInput);
            fileOutput = new PrintStream("mrt_stations_updated.txt");
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void run(){
        while(sc.hasNextLine()){
            String line = sc.nextLine().trim();
            // replaces ' ' with '+'
            fileOutput.println(line.replace(' ', '+'));
        }
    }

    public static void main(String args[]) {
        new MrtStationsReplace().run();
    }
}