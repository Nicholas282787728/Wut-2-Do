import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Converter{
    private Scanner sc;
    private String constant = "boardgames";

    public Converter(File file){
        try{
            sc = new Scanner(file);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        String constant = "boardgames";
        File file = new File("results_" + constant + ".txt");
        new Converter(file).run();
    }

    public void run(){
        try{
            PrintStream file = new PrintStream("results_" + constant + "_parsed.txt");
            while(sc.hasNextLine()){

                String shop_name = sc.nextLine().trim();
                String address = sc.nextLine().trim();
                String postal_code = "";
                String unit = "";

                postal_code = findPostal(address);
                unit = findUnit(address);
                file.println(shop_name);
                file.println(unit);
                file.println(address);
                file.println(postal_code);
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }

    public String findPostal(String str){
        Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9]");
        Matcher matcher = pattern.matcher(str);

        if(matcher.find()){
            return matcher.group();
        }
        else{
            return "";
        }
    }

    public String findUnit(String str){
        Pattern pattern = Pattern.compile("[0-9][0-9][-][0-9][0-9]");
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            int i = matcher.end();
            while(str.charAt(i) != ' '){
                i++;
            }
            if(str.charAt(i-1) == ',')
                i--;

            String toReturn = str.substring(matcher.start(), i);
            return toReturn;
        }
        else{
            return "";
        }
    }

    public String findBuilding(String str){

        return "Building";
    }

    public String findAddress(String str){

        return "Address";
    }

}