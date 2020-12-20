package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.trudysshop.R;

import java.io.BufferedReader;
import java.net.URI;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    ListView listView;
    //TextView textview;
    ArrayAdapter<String> adapter;
    TextView textview;
    static int product_id;
    static ArrayList<String> products;
    //static String product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        listView= (ListView) findViewById(R.id.ProductsList);
        textview=findViewById(R.id.textView2);
        adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);


        products=new ArrayList<>(1000);

       // textview=findViewById(R.id.textView2);
       // textview.setText("Available products");

        new Connection().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                product_id=position + 1;
                //product=products.get(position);
                Intent intent = new Intent( MenuActivity.this,
                        DetailsView.class);
                startActivity(intent);

            }

        });
    }
    class Connection extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result= "";
            String host ="http://192.168.64.2/android_api/Product.php";
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
                    JSONArray Product = jsonResult.getJSONArray("Product");
                    for(int i=0; i< Product.length(); i++){
                        JSONObject Products= Product.getJSONObject(i);
                        int id= Products.getInt("id");
                        String Name= Products.getString("Product Name");
                        String description= Products.getString("description");
                        String image_url = Products.getString("image_url");
                        String output= id + " - " + Name + " \n Description: " +description + " \n URL: "+ image_url;
                        adapter.add(output);
                        products.add(Name);



                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "There's no products yet", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }
}
