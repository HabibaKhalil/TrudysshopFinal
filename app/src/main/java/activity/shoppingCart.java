package activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trudysshop.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import activity.Cart2;
import activity.MainActivity;

public class shoppingCart extends AppCompatActivity {
    TextView textview;
    ArrayAdapter<String> adapter;
    ListView listView;
    static ArrayList<String> Shops;
    static int shop_id;
    static double lat;
    static double lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart2);
        textview = findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        Shops = new ArrayList<>(1000);
        listView.setAdapter(adapter);
        new Connection().execute();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                shop_id=position;
                AlertDialog.Builder builder = new AlertDialog.Builder(shoppingCart.this);
                builder.setTitle("Show Route on GoogleMaps");
                String[] location = {"Yes", "No"};
                builder.setItems(location, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                MainActivity.currentlocation = false;
                                new Connection2().execute();
                                Intent i = new Intent(getApplicationContext(),
                                        MapsActivity.class);
                                startActivity(i);
                                finish();
                                break;

                        }
                    }
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }
    class Connection extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String host = "http://192.168.64.2/android_api/FindInCart.php";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();


            } catch (Exception e) {
                return new String("There exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if (success == 1) {
                    JSONArray Cart = jsonResult.getJSONArray("Cart");

                    for (int i = 0; i < Cart.length(); i++) {

                        JSONObject cart = Cart.getJSONObject(i);
                        String Product = cart.getString("Product");
                        String Store = cart.getString("Shop");
                        String uid = cart.getString("user_id");


                        if (uid.equals(MainActivity.uid)) {
                            String output = "Product : " + Product + "\n" +" Shop : " + Store;
                            Shops.add(Store);
                            adapter.add(output);

                        }
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }

    class Connection2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String host = "http://192.168.64.2/android_api/Shop.php";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();


            } catch (Exception e) {
                return new String("There exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if (success == 1) {
                    JSONArray Shop = jsonResult.getJSONArray("Shop");

                    for (int i = 0; i < Shop.length(); i++) {

                        JSONObject shops =Shop.getJSONObject(i);
                        String shop = shops.getString("Shop Name");
                        if(shop.equals(Shops.get(shop_id))){
                            lat=Double.parseDouble(shops.getString("Latitude"));
                            lng=Double.parseDouble(shops.getString("Longitude"));
                            break;
                        }

                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }


}
