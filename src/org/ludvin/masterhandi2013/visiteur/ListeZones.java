package org.ludvin.masterhandi2013.visiteur;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class ListeZones extends Activity implements OnClickListener, OnInitListener {

	//UI
	ImageButton btnRetour;
	
	//divers
	GlobalVars globalvars ;	//variables globales
	String text="";
	//private TextToSpeech tts;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste_zones);
		globalvars = ((GlobalVars)getApplicationContext());	//variables globales
		
		if (globalvars.getAccessible()) 
		{
			Toast toast = Toast.makeText(this, "Affichage de la liste des zones alentour.", Toast.LENGTH_SHORT);
			toast.show();
			//tts.speak("Affichage de la liste des zones alentours.", TextToSpeech.QUEUE_ADD, null);
		}
		
		//UI
		btnRetour = (ImageButton) findViewById(R.id.btnRetour);
		btnRetour.setOnClickListener(this);
		
		//tts = new TextToSpeech(this, this);
		
		
	}
	

	public void onClick(View v) {
		if (v.getId()==R.id.btnRetour)
			if (globalvars.getAccessible())
				{Toast toast = Toast.makeText(this, "Retour à l'accueil de l'application.", Toast.LENGTH_SHORT);toast.show();}
			this.finish();
	}

	
	/**
	 * Menu contextuel
	 * @param Menu
	 * @return Boolean
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.liste_zones, menu);
		return true;
	}


	@Override
	public void onInit(int status) {
		/*
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.FRENCH);
		} else {
			Log.e("TTS", "init failed");
		}*/
	}

	/**
	 * API : Destruction de l'application par Android
	 */
	protected void onDestroy() {
		super.onDestroy();
		/*
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}*/
	}
}
