package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.trudysshop.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.io.HttpRequestParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static activity.DetailsView.cart_operation;
import static activity.DetailsView.position_shop;

public class Cart2 extends AppCompatActivity {
    static String shop;
    RequestQueue requestQueue;
    String URL_STORE = "http://192.168.64.2/android_api/Cart2.php";
    String URL_DELETE="http://192.168.64.2/android_api/DeleteFromCart.php";
    static boolean inCart;
    static int id;
    //HashMap<String,String> hashMap = new HashMap<>();
    ProgressDialog progressDialog2;
    HttpRequestParser httpParse;
    Button menu;
    Button cart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);
        menu=findViewById(R.id.btnMenu);
        cart=findViewById(R.id.btnCart);
        shop = (DetailsView.shops).get(position_shop);
        new Connection().execute();

        menu.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MenuActivity.class);
                startActivity(i);
                finish();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        shoppingCart.class);
                startActivity(i);
                finish();
            }
        });



    }

    public void store() {


    requestQueue =Volley.newRequestQueue(

    getApplicationContext());
    StringRequest request = new StringRequest(Request.Method.POST, URL_STORE, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("user_id", MainActivity.uid);
            parameters.put("Product", DetailsView.product);
            parameters.put("Shop", shop);


            return parameters;
        }

    };
        requestQueue.add(request);

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
                    inCart=false;
                    for (int i = 0; i < Cart.length(); i++) {

                        JSONObject cart = Cart.getJSONObject(i);
                        String Product = cart.getString("Product");
                        String Store = cart.getString("Shop");
                        String uid = cart.getString("user_id");
                        id = cart.getInt("id");

                        if (uid.equals(MainActivity.uid) && (shop.equals(Store)) && ((DetailsView.product).equals(Product))) {
                            if (cart_operation == 0) {
                                Toast.makeText(getApplicationContext(), "Already saved in cart", Toast.LENGTH_SHORT).show();
                            }
                                inCart = true;
                                break;


                        }
                    }
                    if((inCart==false)&&(cart_operation==0)){
                        store();
                        Toast.makeText(getApplicationContext(), "Product saved in cart", Toast.LENGTH_SHORT).show();
                    }
                    if((inCart==false)&&(cart_operation==1)){
                        Toast.makeText(getApplicationContext(), "Product not saved in cart", Toast.LENGTH_SHORT).show();
                    }
                    if((inCart==true)&&(cart_operation==1)){
                        Delete();
                        Toast.makeText(getApplicationContext(), "Product Removed from cart", Toast.LENGTH_SHORT).show();
                    }





                } else {

                    if (cart_operation==0){
                        store();
                        Toast.makeText(getApplicationContext(), "Product saved in cart", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }


    public void Delete() {


        requestQueue =Volley.newRequestQueue(

                getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", ""+id);



                return parameters;
            }

        };
        requestQueue.add(request);

    }

}