package my.map;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.OverlayItem;

public class main extends MapActivity implements OnSharedPreferenceChangeListener {
   


	/** Called when the activity is first created. */
	
	SMSListener listener;
	SharedPreferences prefs;
	private BMapManager mapManager ;
	
	private MapView mapView;
	private MapController mapController;
	
	
	
	private int longitude;
	private int latitude;
	private LocationManager locationManager = null;
	private MyLocationOverlay myLocationOverlay = null;
	String strkey = "3774687D7CFED9D498A102897F5EDA83BC2AC949";
	final static int EXIT=1;
	final static int ABOUT=EXIT+1;
	public String ph;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.main);
        //设置首选项
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        ph = prefs.getString("ponenumber", "");
      
         
        mapManager = new BMapManager(getApplication());
        mapManager.init(strkey, new MKGeneralListener() {
			
			public void onGetPermissionState(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onGetNetworkState(int arg0) {
				// TODO Auto-generated method stub
				if (arg0==MKEvent.ERROR_NETWORK_CONNECT) {
					Toast.makeText(main.this, "您的网络出错啦！", Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
      
        mapManager.start();
        super.initMapActivity(mapManager);
        
        mapView = (MapView)findViewById(R.id.bmapView);
		mapView.setTraffic(true);
		mapView.setBuiltInZoomControls(true);
		mapView.setDrawOverlayWhenZooming(true);
		Drawable marker = this.getResources().getDrawable(R.drawable.iconmarka);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		
		final CustomItemizedOverlay overlay = new CustomItemizedOverlay(marker,this);
		mapController = mapView.getController();
        
        Button QueryButton = (Button)findViewById(R.id.button1);
         
       
         QueryButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 
				final String longitudeString = SMSListener.latitudeString;
				final String latitudeString = SMSListener.longitudeString;
				
				if (longitudeString==null || latitudeString==null) {
					Toast.makeText(main.this, "未获取到位置信息", Toast.LENGTH_LONG).show();
				}else {
					try {
						longitude = (int) (1000000 * Double.parseDouble(longitudeString));
						 latitude = (int) (1000000 * Double.parseDouble(latitudeString));
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(main.this, "位置信息格式错误", Toast.LENGTH_LONG).show();
					}
					
					GeoPoint geoPoint = new GeoPoint(latitude, longitude);
					OverlayItem overlayItem = new OverlayItem(geoPoint, "", "定位");
					overlay.addOverlay(overlayItem);
					List<Overlay> mapOverlays = mapView.getOverlays();
					mapOverlays.add(overlay);
					myLocationOverlay = new MyLocationOverlay(main.this, mapView);
					
					mapView.getOverlays().add(myLocationOverlay);
					
					mapController.setCenter(geoPoint);
					mapController.setZoom(16);
					
					
				}
				
				locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				boolean GPS_status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);					
				String status = "";
				if (GPS_status) {
					status += "GPS 已开启";
						
				}else {
					
					status += "GPS 未开启，定位会产生错误，请先开启";
											}
					
				Toast.makeText(main.this, status, 1).show();
				
				
			}
		});
		
      
    }
   
   
	
	@Override
	protected void onDestroy() {
		if (mapManager != null) {
			
			mapManager.destroy();
			mapManager = null;
			
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if(mapManager != null){
			mapManager.stop();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if (mapManager != null) {
			
			mapManager.start();
			
		}
		super.onResume();
	}
	
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,EXIT,1,"退出");
		menu.add(0,ABOUT,2,"关于");	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	 @SuppressWarnings("unused")
	@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
		 switch (item.getItemId()) {
		case R.id.item1:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case EXIT: finish();
		case ABOUT:{
			AlertDialog.Builder builder = new Builder(main.this); 
	         builder.setTitle("联系作者"); 
	         builder.setPositiveButton("确定", null); 
	         //builder.setIcon(android.R.drawable.ic_dialog_info); 
	         builder.setMessage("QQ:2392078052\n2013.10.9"); 
	         builder.show(); 
			}


		}
			return true;
		}
	 
	
	 

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
	
	
	
 	//send SMS
    
    public void SendK(View view) {
    	
    	
    	if (ph == "") {
    		Toast.makeText(this, "请先绑定号码", Toast.LENGTH_LONG).show();
		}else {
			try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(ph, null, "k", null, null);
    		Toast.makeText(this, "开启服务", Toast.LENGTH_LONG).show();
    		Log.i("TAG", "ERROR");
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "发送失败，请重新发送", Toast.LENGTH_LONG).show();
			
		}
    	
	}
    	
  }
  public void Sendg(View view) {
    	
    	
    	if (ph == "") {
    		Toast.makeText(this, "请先绑定号码", Toast.LENGTH_LONG).show();
		}else {
			try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(ph, null, "g", null, null);
    		Toast.makeText(this, "停止服务", Toast.LENGTH_LONG).show();
    		Log.i("TAG", "ERROR");
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "发送失败，请重新发送", Toast.LENGTH_LONG).show();
			
		}
    	
	}
    	
  }
  public void Sendd(View view) {
  	
  	
  	if (ph == "") {
  		Toast.makeText(this, "请先绑定号码", Toast.LENGTH_LONG).show();
		}else {
			try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(ph, null, "d", null, null);
  		Toast.makeText(this, "对目标设置电子围栏", Toast.LENGTH_LONG).show();
  		Log.i("TAG", "ERROR");
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "发送失败，请重新发送", Toast.LENGTH_LONG).show();
			
		}
  	
	}
  	
}
  public void Sendt(View view) {
  	
  	
  	if (ph == "") {
  		Toast.makeText(this, "请先绑定号码", Toast.LENGTH_LONG).show();
		}else {
			try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(ph, null, "t", null, null);
  		Toast.makeText(this, "定时获取位置信息", Toast.LENGTH_LONG).show();
  		Log.i("TAG", "ERROR");
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "发送失败，请重新发送", Toast.LENGTH_LONG).show();
			
		}
  	
	}
  	
}
	
}