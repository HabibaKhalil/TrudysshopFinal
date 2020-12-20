package activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trudysshop.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;

import static activity.CartView.AddToCart;
import static activity.CartView.RemoveFromCart;
import static activity.MenuActivity.product_id;
import static activity.MenuActivity.products;


public class DetailsView extends AppCompatActivity {

    ListView listView;
    //TextView textview;
    ArrayAdapter<String> adapter;
    static int list [];
    static Location location;
    TextView textview;
    static int Shop_id;
    static String output;
    static ArrayList<Integer> Output_price;
    static ArrayList<Double> distances;
    static ArrayList<String> Output_product;
    static ArrayList<String> shops;
    static boolean sort;
    static String product;
    static int position_shop;
    static int cart_operation;
    static boolean cancel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        textview= findViewById(R.id.textView2);

        listView= (ListView) findViewById(R.id.Listshops);
        adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        //new Connection().execute();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort according to");
        Output_price=new ArrayList<>(1000);
        Output_product=new ArrayList<>(1000);
        distances=new ArrayList<>(1000);
        shops=new ArrayList<>(1000);

// add a list
        String[] animals = {"Price", "Distance", "Cancel"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: sort=true; cancel=false; new Connection().execute(); break;
                    case 1: sort=false;cancel=false; new Connection().execute(); break;
                    case 2: cancel=true;new Connection().execute(); break;
                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position_shop=position;
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailsView.this);
                builder2.setTitle("Cart");
                String[] options = {"Add to cart", "Remove from cart", "Cancel"};
                builder2.setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                          //position_cart=which;
                        switch(which){
                            case 0 :  cart_operation=0; Intent intent = new Intent( DetailsView.this,
                                    Cart2.class);
                                startActivity(intent); break;
                            case 1 : cart_operation=1;Intent intent2 = new Intent( DetailsView.this,
                                    Cart2.class);
                                startActivity(intent2); break;
                        }



                    }
                });

            // create and show the alert dialog
            AlertDialog dialog = builder2.create();
            dialog.show();
        }
        });




    }

    class Connection extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result= "";
            String host ="http://192.168.64.2/android_api/Shop_product.php";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";
                while((line= reader.readLine())!=null){
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result= stringBuffer.toString();



            }
            catch (Exception e){
                return new String("There exception: " + e.getMessage());
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonResult = new JSONObject(result);
                int success= jsonResult.getInt("success");
                if(success==1){
                    JSONArray Shop_product = jsonResult.getJSONArray("Shop_product");
                    for(int i=0; i< Shop_product.length(); i++){
                        JSONObject Shops= Shop_product.getJSONObject(i);
                        int ProductName_id= Shops.getInt("ProductName_id");
                        if (ProductName_id==product_id){
                            product= products.get(product_id-1);
                            textview.setText(product);
                            Shop_id=Shops.getInt("ShopName_id");
                            String price= Shops.getString("Price");
                            String special_offer=Shops.getString("Special offers");
                            String shop = Shops.getString("Shop Name");


                            double Longitude=Double.parseDouble(Shops.getString("Longitude"));
                            double Latitude=Double.parseDouble(Shops.getString("Latitude"));
                            double lat=MainActivity.lat;
                            double lng=MainActivity.longi;

                            double dist=distance(Latitude, Longitude,lat, lng);
                            dist=round(dist, 2);
                            output = " Store: " + shop + "\n Price: " + price + " LE" + "\n Distance to store: " + dist + " Km" ;

                            if (!special_offer.isEmpty())
                                output=output + "\n Special offer : " +special_offer  ;

                           Output_price.add(Integer.parseInt(price));
                           distances.add(dist);
                           Output_product.add(output);
                           shops.add(shop);
                        }

                    }
                    if (cancel==true){
                        for (int i = 0; i < Output_product.size(); i++) {
                            adapter.add(Output_product.get(i));
                        }
                    }
                    else if (cancel==false) {
                        if (sort == true) {
                            insertionSort1();
                            for (int i = 0; i < Output_product.size(); i++) {
                                adapter.add(Output_product.get(i));
                            }
                        }
                        else {
                            insertionSort2();
                            for (int i = 0; i < Output_product.size(); i++) {
                                adapter.add(Output_product.get(i));
                            }

                        }
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Products not available anywhere", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515; // distance in miles
            dist= dist*1.609344; //distance in km

            return (dist);
        }
    }
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static void insertionSort1() {
        int n = Output_price.size();
        for (int j = 1; j < n; j++) {
            int key1 = Output_price.get(j);
            String key2 = Output_product.get(j);
            String key3= shops.get(j);
            int i = j-1;
            while ( (i > -1) && ( Output_price.get(i) > key1 ) ) {
                Output_price.set(i+1,Output_price.get(i));
                Output_product.set(i+1,Output_product.get(i));
                shops.set(i+1,shops.get(i));

                i--;
            }
            Output_price.set(i+1,key1);
            Output_product.set(i+1,key2);
            shops.set(i+1,key3);

        }
    }
    public static void insertionSort2() {
        int n = distances.size();
        for (int j = 1; j < n; j++) {
            double key1 = distances.get(j);
            String key2 = Output_product.get(j);
            String key3= shops.get(j);
            int i = j-1;
            while ( (i > -1) && ( distances.get(i) > key1 ) ) {
                distances.set(i+1,distances.get(i));
                Output_product.set(i+1,Output_product.get(i));
                shops.set(i+1,shops.get(i));

                i--;
            }
            distances.set(i+1,key1);
            Output_product.set(i+1,key2);
            shops.set(i+1,key3);

        }
    }






    }

//Toast.makeText(AddTocartActivity.this, "You are Already added to cart!", Toast.LENGTH_SHORT).show();

