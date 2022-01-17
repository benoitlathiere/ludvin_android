package org.ludvin.masterhandi2013.visiteur;

import java.util.Timer;
import java.util.TimerTask;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class Demarrage extends Activity implements OnClickListener {

	private static String TAG="visiteur";	//pour Log.d();
	
	//UI
	TextView TV_version;
	ImageButton btnLogo;
	
	//wifi
	WifiManager wifiManager;
	
	//global
	GlobalVars globalvars ;
	
	//divers
	private static TimerTask scanTask;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demarrage);
		this.wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		globalvars = ((GlobalVars) getApplication());	//variables globales
		
		//mode accessible ?
		AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE); //source : http://stackoverflow.com/questions/5116867/how-to-know-if-android-talkback-is-active
		globalvars.setAccessible(am.isEnabled());
		globalvars.setAccessible(true);
		if (this.globalvars.getAccessible())
			{Toast toast = Toast.makeText(this, "Bienvenue dans l'outil de localisation des utilisateurs déficients visuels en intérieur.", Toast.LENGTH_SHORT);toast.show();}
		
		//UI
		TV_version = (TextView) findViewById(R.id.TV_version);
		btnLogo = (ImageButton) findViewById(R.id.btnLogo);
		btnLogo.setOnClickListener(this);
		
		//version
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		TV_version.setText("\r\nVersion "+pInfo.versionName);
		
		//tâche pour programmer lancement activité
		final Handler handler = new Handler();
		Timer t = new Timer();
		scanTask = new TimerTask() {
	        public void run() {
					handler.post(new Runnable() {
	                        public void run() {
	                        	activite();
	                        }
	               });
	        }
	    };
		t.schedule(scanTask, 10000, 60000);
		
		//verif antenne Wifi
		if (! this.wifiManager.isWifiEnabled()) {
			//source : http://developer.android.com/guide/topics/ui/dialogs.html#AlertDialog
			// 1. Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// 2. Chain together various setter methods to set the dialog characteristics
			builder.setMessage("La fonction Wifi doit être activée pour utiliser le logiciel. Que voulez-vous faire ?");
			builder.setTitle("Fonction Wifi requise");
			//listeners sur dialog :
			builder.setPositiveButton("Activer le Wifi", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) { 
			        	   allumeWifi();
			        	   Demarrage.scanTask.run();
			        	   //activite();
			           }
				});
			builder.setNegativeButton("Quitter l'application", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) { finish(); }
				});
			AlertDialog dialog = builder.create();
			dialog.show();
			Demarrage.scanTask.cancel();
		}
		

		

	}
	
	
	/**
	 * Méthode qui active l'antenne Wifi
	 */
	protected void allumeWifi() {
		{Toast toast = Toast.makeText(this, "Activation du Wifi.", Toast.LENGTH_SHORT);toast.show();}
		wifiManager.setWifiEnabled(true);
		//globalvars.setWifi(true);
	}


	/**
	 * Chargement de l'activité suivante
	 */
	private void activite() {
		scanTask.cancel();
		startActivity (new Intent (this, Accueil.class));
		finish();
	}


	public void onClick(View v) {
		if (v.getId()==R.id.btnLogo){
			activite();
			Log.d(TAG,"on lance l'activité");
		}
	}
	
	
}
