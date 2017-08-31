package com.pilio.ocrpicture;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;


public class CamService extends Service {

	private static final String TAG = "CameraDemo";
	private final IBinder mBinder = new LocalBinder();

	public TextView mylog;
	String mylogtext="\noutput log\n";
	
	private FileWriter writer;

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	
	public File root;
	public File file;

	Camera camera;
	ScheduledFuture<?> beeperHandle;
	Parameters cameraParameters;

	/* Service Life cycle Overrides */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "started");
		camera = Camera.open();
		cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_ON);
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(cameraParameters);
		camera.startPreview();
		try {
			camera.setPreviewDisplay(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		takePicsPeriodically(60);
	}

	@Override
	public void onDestroy() {
		stopPics();
		super.onDestroy();
	}
	


	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		CamService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return CamService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return myRemoteServiceStub;
	}

	/* ----------------------------------- */

	private IMyRemoteService.Stub myRemoteServiceStub = new IMyRemoteService.Stub() {

		/* Basic Service Methods */
		public boolean isCollecting() {
			return (beeperHandle != null);
		}
		/* ------------------------- */
	};

	public void takePicsPeriodically(long period) {
		final Runnable beeper = new Runnable() {
			public void run() {
				
				camera.startPreview();
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		};
		camera.startPreview();
		beeperHandle = scheduler.scheduleAtFixedRate(beeper, period, period,
				TimeUnit.SECONDS);
	}

	public void stopPics() {
		beeperHandle.cancel(true);
		beeperHandle = null;
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	/* Camera Call backs */
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for j peg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		private String fname;

		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// write to local sand box file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to s d card
				
		
				fname = String.format(
						"/sdcard/%d.jpg", System.currentTimeMillis());
				
				Log.d("r_upload", "picture filename= " + fname);
							
				outStream = new FileOutputStream(fname);
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			
			
			Log.d("r_upload", "onPictureTaken - jpeg");
			
		 	root = Environment.getExternalStorageDirectory();
		 	file = new File(root, "b1.csv");
		 	file.delete();
			
			 try {
				 
					
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here1"+"\n");
					
					writer.flush();
					writer.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		
			
			HttpURLConnection connection = null;
			DataOutputStream outputStream = null;
			DataInputStream inputStream = null;

			String pathToOurFile = fname;
			String urlServer = "http://176.58.100.152/ocr_android/upload.php";
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary =  "*****";

			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1*1024*1024;
			
			Log.d("r_upload","pathToOurFile "+pathToOurFile);
		  
			 try {
				 
					
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("pathToOurFile"+pathToOurFile+"\n");
					
					writer.flush();
					writer.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			 
			try
			{
				
				
				 try {
					 
						
				    	file.createNewFile();
						writer = new FileWriter(file, true);
						writer.write("inTry"+"\n");
						
						writer.flush();
						writer.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 
				 
		    Log.d("r_upload","in try");
		    
		    Log.d(TAG, "in try");
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
			
			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here1"+fileInputStream+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");
			
			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here7"+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			 
			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here8"+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			outputStream = new DataOutputStream( connection.getOutputStream() );
			
	
			
			
			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here81"+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here82"+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here83"+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			outputStream.writeBytes(lineEnd);

			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here9"+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			 try {		
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("here10"+"\n");			
					writer.flush();
					writer.close();	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			
			while (bytesRead > 0)
			{
			outputStream.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();
			
			Log.d("r_upload","serverResponseMessage "+serverResponseMessage);
			
			
			 try {
				 
					
			    	file.createNewFile();
					writer = new FileWriter(file, true);
					writer.write("srvermessage?"+"\n");
					
					writer.flush();
					writer.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			}
			catch (Exception ex)
			{
			//Exception handling
			  Log.d("r_upload","failed post");
			  
			  
			  
				 try {		
				    	file.createNewFile();
						writer = new FileWriter(file, true);
						writer.write("failed post"+outputStream+"\n");			
						writer.flush();
						writer.close();	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
			  
			  
			  
			  
			}	
			
			
			//add request header

		
	 
		}
	};

}
