package com.pilio.ocrpicture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.os.Environment;

public class MainActivity extends Activity {

	private Intent camService;
	protected boolean mBound;
	protected CamService mService;
	public TextView mylog;
	String mylogtext="\noutput log\n";
	public File root;
	public File file;

	private FileWriter writer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mylog = (TextView) findViewById(R.id.TextView01);
		
	    mylogtext=mylogtext+"about to start camservice 2018" + "\n";
	 	mylog.setText(mylogtext);
	 	
	 	Log.d("micro","dkljhsfklhasdkljfhasdkjfh");
	 	root = Environment.getExternalStorageDirectory();
	 	file = new File(root, "a1.csv");
		
		camService = new Intent(this, CamService.class);
		startService(camService);
		
		
		 try {
			 
				file.delete();
		    	file.createNewFile();
				writer = new FileWriter(file, true);
				writer.write("kjhkhkhkjhkjhkjhjk\n");
				
				writer.flush();
				writer.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		mylogtext=mylogtext+"camservice started" + "\n";
		mylog.setText(mylogtext);
				
     
	
	  
		
	    
        
	}
	
	


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(camService);
	}

}
