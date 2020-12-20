package activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.trudysshop.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import static activity.MainActivity.currentlocation;
import static activity.shoppingCart.Shops;
import static activity.shoppingCart.shop_id;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
   Location currentLocation;
   FusedLocationProviderClient fusedLocationProviderClient;
   private static final int REQUEST_CODE = 101;
   Button btnGetDirection;
  // static LatLng place1;
   //static LatLng destination;
   static MarkerOptions origin;
   static MarkerOptions dest;
   static LatLng MyLocation;
   static LatLng destination;
   GoogleMap map;
   Polyline currentPolyline;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_maps);
      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
      fetchLocation();
      btnGetDirection=findViewById(R.id.btnGetDirection);
      btnGetDirection.setOnClickListener(new View.OnClickListener() {
         @Override

         public void onClick(View v) {
               if(currentlocation==false) {
            String URL= getUrl(origin.getPosition(),dest.getPosition(),"driving");
             new FetchURL(MapsActivity.this).execute(URL, "driving");

         }
               else
                  Toast.makeText(getApplicationContext(), "Destination not set", Toast.LENGTH_SHORT).show();
         }
      }
      );
}
      private String getUrl(LatLng position, LatLng position1, String DirectionMode) {
         String str_origin="origin=" + MyLocation.latitude +","+MyLocation.longitude;
         String str_dest="destination=" + destination.latitude +","+destination.longitude;
         String mode = "mode=" + DirectionMode;
         String parameters= str_origin + "&" +str_dest +"&" + mode;
         String output="json";
         String url="https://maps.googleapis.com/maps/api/directions/"+ output + "?" +parameters+ "&key=" +"AIzaSyD9Y79N7GMTHoeVs_q_t9Q1xx9hYdMr1mw";
         return url;

   }

   public void fetchLocation() {
      if (ActivityCompat.checkSelfPermission(
              this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
              this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
         return;
      }
      Task<Location> task = fusedLocationProviderClient.getLastLocation();
      task.addOnSuccessListener(new OnSuccessListener<Location>() {
         @Override
         public void onSuccess(Location location) {
            if (location != null) {
               currentLocation = location;
               //setLocation(currentLocation);
               Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
               SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap);
               assert supportMapFragment != null;
               supportMapFragment.getMapAsync(MapsActivity.this);
            }
         }
      });
   }
   @Override
   public void onMapReady(GoogleMap googleMap) {
      map=googleMap;
      //LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MyLocation = new LatLng(30.0096878, 31.2910490);
      //MyLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

      if(currentlocation==false) {
         destination = new LatLng(shoppingCart.lat, shoppingCart.lng);
         dest = new MarkerOptions().position(destination).title(Shops.get(shop_id));
         googleMap.animateCamera(CameraUpdateFactory.newLatLng(destination));
         googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 5));
         googleMap.addMarker(dest);
      }

      origin= new MarkerOptions().position(MyLocation).title("I am here!");
      googleMap.animateCamera(CameraUpdateFactory.newLatLng(MyLocation));
      googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 5));
      googleMap.addMarker(origin);


   }
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      switch (requestCode) {
         case REQUEST_CODE:
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               fetchLocation();
            }
            break;
      }
   }
   public void onTaskDone(Object... values){
      if(currentPolyline!=null)
         currentPolyline.remove();
      currentPolyline=map.addPolyline((PolylineOptions) values[0]);
   }

}