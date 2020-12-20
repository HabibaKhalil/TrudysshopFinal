package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.trudysshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import activity.DetailsView;
import activity.MainActivity;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler2;
import helper.SessionManager;

import static activity.DetailsView.position_shop;
import static app.AppController.TAG;

public class CartView extends AppCompatActivity {
    private ProgressDialog pDialog;
    private SessionManager session;
    static String shop;
    private SQLiteHandler2 db;
    TextView textview;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);
        textview=findViewById(R.id.textView);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        shop= (DetailsView.shops).get(position_shop);


        // Session manager
        //session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler2(getApplicationContext());
        registerUser(MainActivity.uid,DetailsView.product,shop);
    }

    public static void AddToCart(){


    }

    public static void RemoveFromCart(){

    }

    public void registerUser(final String user_id, final String Product,
                             final String Shop) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CART, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        JSONObject Cart = jObj.getJSONObject("Cart");
                        String user_id = Cart.getString("user_id");
                        String Product = Cart.getString("Product");
                        String Shop = Cart.getString("Shop");

                        // Inserting row in users table
                        db.addUser(user_id, Product, Shop);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("Product", Product);
                params.put("Shop", Shop);



                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }







}
