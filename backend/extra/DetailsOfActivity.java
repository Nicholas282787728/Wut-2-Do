import java.io.*;
import java.util.*;

class DetailsOfActivity{
    private String shop_name;
    private String unit_no;
    private String building_name;
    private String address;
    private int postal_code;

    public DetailsOfActivity(String shop_name, String unit_no,
        String building_name, String address, int postal_code){
        this.shop_name = shop_name;
        this.unit_no = unit_no;
        this.building_name = building_name;
        this.address = address;
        this.postal_code = postal_code;
    }
    
    public String get_shop(){return shop_name;}
    public String get_unit(){return unit_no;}
    public String get_building(){return building_name;}
    public String get_address(){return address;}
    public int get_postal(){return postal_code;}
}