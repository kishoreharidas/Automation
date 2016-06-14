package com.kishorevh.automation;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jayway.jsonpath.JsonPath;

public class MainActivity extends Activity {
	
	private class GetStatus extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {


			Log.i(DISPLAY_SERVICE, "Starting GetStatus service call");
			
			String response = null;
			String status = null;
			
			try {
				RestClient client = new RestClient("http://dweet.io/get/latest/dweet/for/kishorevh-ledcontrol");
			    client.Execute(RestClient.RequestMethod.GET);
			    response = client.getResponse();
			    status = JsonPath.parse(response).read("$.with[0].content.status");

				Log.i(DISPLAY_SERVICE, status);
			} catch (Exception e) {
			    e.printStackTrace();
			}

			Log.i(DISPLAY_SERVICE, "GetStatus Service call complete");
			return status;
			
		}


		protected void onPostExecute(String status) {
			Log.i(DISPLAY_SERVICE, "Post Execute" + status);
			
			
		if (status!=null) {
			
			TextView current_status = (TextView) findViewById(R.id.current_status);
			

			current_status.setText("Current Status : " + status);

		} else{
			Log.e(DISPLAY_SERVICE, "Null Message on Execute ");
			
			Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
		}
		}


		}
	
	private class UpdateStatus extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {


			Log.i(DISPLAY_SERVICE, "Starting UpdateStatus service call");
			
			String response = null;
			String status = null;
			
			try {
				RestClient client = new RestClient("http://dweet.io/dweet/for/kishorevh-ledcontrol");
				client.AddParam("status", params[0]);
				client.Execute(RestClient.RequestMethod.GET);
			    response = client.getResponse();
			    status = JsonPath.parse(response).read("$.with.content.status");
			    
			} catch (Exception e) {
			    e.printStackTrace();
			}

			return status;
			
		}


		protected void onPostExecute(String status) {
		if (status!=null) {
			
			TextView current_status = (TextView) findViewById(R.id.current_status);
			
			current_status.setText("Current Status : " + status);

		} else{
			Log.e(DISPLAY_SERVICE, "Null Message on Execute ");
			
			Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
		}


//		Button b = (Button)findViewById(R.id.my_button);
//
//
//		b.setClickable(true);
		}


		}
	
	private class ClickPicture extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {


			Log.i(DISPLAY_SERVICE, "Starting service call");
			String response = null;
			String status = null;
			try {
				RestClient client = new RestClient("http://dweet.io/dweet/for/sharmaiotdevice");

				client.AddParam("ClickPhoto","true");
				client.Execute(RestClient.RequestMethod.GET);
				response = client.getResponse();
			    status = JsonPath.parse(response).read("$.with.content.ClickPhoto");
			} catch (Exception e) {
			    e.printStackTrace();
			}

			return status;
			
		}


		protected void onPostExecute(String str) {
			
			if(str != null){
				new AlertDialog.Builder(MainActivity.this)
			    .setTitle("Photo Click")
			    .setMessage("Picture has been clicked")
			    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            dialog.cancel();
			        }
			     })
			    .setIcon(android.R.drawable.ic_dialog_info)
			     .show();
			}else{
				Log.e(DISPLAY_SERVICE, "Null Message on Execute ");
				
				Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
			}
			
//		Button b = (Button)findViewById(R.id.my_button);
//
//
//		b.setClickable(true);
		}


		}
	
    private class LoadImage extends AsyncTask<Void, String, Bitmap> {
        @Override
        
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
 
        }
         protected Bitmap doInBackground(Void... params) {
        	 
        	try {
        		RestClient client = new RestClient("http://dweet.io/get/dweets/for/kishorevh-ledcontrol");

     			client.Execute(RestClient.RequestMethod.GET);
     			String response = client.getResponse();

     			int i=0;
     			String url = null;
     			
     			while(true){
     				url = JsonPath.parse(response).read("$.with["+i+"].content.url");
     				
     				if(StringUtils.isNotBlank(url))
     					break;
     				
     				i++;
     				if(i > 100){
     					break;
     				}
     			}

     			Log.i(DISPLAY_SERVICE, url);
     			
     			bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
     			
        	} catch (Exception e) {
 			    e.printStackTrace();
 			}

            return bitmap;
         }
 
         protected void onPostExecute(Bitmap image) {
 
             if(image != null){
             img.setImageBitmap(image);
             pDialog.dismiss();
 
             }else{
 
             pDialog.dismiss();
             Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
 
             }
         }
     }
    
    ImageView img;
    Bitmap bitmap;
    ProgressDialog pDialog;
 
    
	Button buttonOn;

	Button buttonOff;

	Button buttonBlink;

	Button buttonRefresh;
	
	Button buttonCamClick;
	
	Button buttonShowPic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		        
		new GetStatus().execute();

		addListenerOnButtonOn();
		addListenerOnButtonOff();
		addListenerOnButtonBlink();
		addListenerOnButtonRefresh();
		addListenerOnButtonCameraClick();
		addListenerOnButtonShowPicture();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	public void addListenerOnButtonOn() {

		buttonOn = (Button) findViewById(R.id.button1);

		buttonOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			    new UpdateStatus().execute("on");
			}

		});

	}

	public void addListenerOnButtonOff() {

		buttonOff = (Button) findViewById(R.id.button2);

		buttonOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			    new UpdateStatus().execute("off");
			}

		});

	}
	
	public void addListenerOnButtonBlink() {

		buttonBlink = (Button) findViewById(R.id.button3);

		buttonBlink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			    new UpdateStatus().execute("blink");
			}

		});

	}

	public void addListenerOnButtonRefresh() {

		buttonRefresh = (Button) findViewById(R.id.button4);

		buttonRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new GetStatus().execute();
			}

		});

	}
	
	public void addListenerOnButtonCameraClick() {

		buttonCamClick = (Button) findViewById(R.id.button5);

		buttonCamClick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new ClickPicture().execute();
			}

		});

	}
	
	public void addListenerOnButtonShowPicture() {

		buttonShowPic = (Button) findViewById(R.id.button6);

		img = (ImageView)findViewById(R.id.img);
		 
		buttonShowPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				
				new LoadImage().execute();			
			}

		});

	}
}
