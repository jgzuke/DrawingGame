/**
 * all variables to store as well as some base level behaviors, pause and resume and start functions
 */
package com.drawinggame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
public class StartActivity extends Activity
{
	private Controller control;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 120;
	protected byte[] savedData = new byte[savePoints];
	@Override
	/**
	 * sets screen and window variables and reads in data
	 * creates control object and sets up IAB
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Long time = System.nanoTime();
		setWindow();
		control = new Controller(this, this, getScreenDimensions());
		setContentView(control.graphicsController);
		/*read();
		if(savedData[0] == 0)
		{
			savedData[0] = 1;
			setSaveData();
			write();
		}
		readSaveData();*/
		Long newTime = System.nanoTime();
		Long diff = (newTime-time)/100000;
		Log.e("myid", "totalTime: ".concat(Long.toString(diff)));
	}
	/**
	 * sets screen variables as well as audio settings
	 */
	protected void setWindow()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	/**
	 * sets dimensions of screen
	 */
	protected double[] getScreenDimensions()
	{
		int d1 = getResources().getDisplayMetrics().heightPixels;
		int d2 = getResources().getDisplayMetrics().widthPixels;
		if(d2>d1)
		{
			double[] dims = {d2, d1};
			return dims;
		} else
		{
			double[] dims = {d1, d2};
			return dims;
		}
	}
	@ Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	@ Override
	public void onStart()
	{
		super.onStart();
		//control.frameCaller.run();
	}
	/**
	 * reads saved data
	 * starts music, loads images
	 */
	@ Override
	public void onResume()
	{
		super.onResume();
		control.activityPaused = false;
		//control.frameCaller.run();
		/*read();
		if(savedData[0] == 1)
		{
			readSaveData();
		}*/
		control.soundController.startMusic();
	}
	/**
	 * stops music, stops timer, saves data
	 */
	@ Override
	public void onPause()
	{
		super.onPause();
		setSaveData();
		write();
		control.soundController.stopMusic();
		control.activityPaused = true;
		Log.e("mine", "onPau");
		//control.paused = true;
	}
	@ Override
	public void onStop()
	{
		Log.e("mine", "onStop");
		super.onStop();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * saves all required data 
	 */
	protected void saveGame()
	{
		setSaveData();
		write();
		readSaveData();
	}
	/**
	 * set data to write it to save file
	 */
	public void setSaveData()
	{ //TODO change some
		savedData[1] = (byte)((int) control.soundController.volumeMusic);		//1 volume music
		savedData[2] = (byte)((int) control.soundController.volumeEffect);		//2 volume effect
	}
	/**
	 * read data once it has been put into savedData array
	 */
	public void readSaveData()
	{ //TODO change some
		control.soundController.volumeMusic = savedData[1];
		control.soundController.volumeEffect = savedData[2];
	}
	/**
	 * reads data from file and sets variables accordingly
	 */
	private void read()
	{
		try
		{
			fileRead = openFileInput("ProjectSaveData");
		}
		catch(FileNotFoundException e){}
		try
		{
			fileRead.read(savedData, 0, savePoints);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			fileRead.close();
		}
		catch(IOException e){}
	}
	/**
	 * saves data to file
	 */
	private void write()
	{
		try
		{
			fileWrite = openFileOutput("ProjectSaveData", Context.MODE_PRIVATE);
		}
		catch(FileNotFoundException e){}
		try
		{
			fileWrite.write(savedData, 0, savePoints);
		}
		catch(IOException e){}
		try
		{
			fileWrite.close();
		}
		catch(IOException e){}
	}
}