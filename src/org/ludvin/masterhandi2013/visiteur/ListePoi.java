package org.ludvin.masterhandi2013.visiteur;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class ListePoi extends Activity implements OnClickListener {

	//UI
	ImageButton btnRetour;
	
	//divers
	GlobalVars globalvars ;	//variables globales
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste_poi);
		globalvars = ((GlobalVars)getApplicationContext());	//variables globales
		
		if (globalvars.getAccessible())
			{Toast toast = Toast.makeText(this, "Affichage de la liste des zones alentour.", Toast.LENGTH_SHORT);toast.show();}
			
		//UI
		btnRetour = (ImageButton) findViewById(R.id.btnRetour);
		btnRetour.setOnClickListener(this);
	}

	
	public void onClick(View v) {
		if (v.getId()==R.id.btnRetour)
			if (globalvars.getAccessible())
				{Toast toast = Toast.makeText(this, "Retour à l'accueil de l'application.", Toast.LENGTH_SHORT);toast.show();}
			this.finish();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.liste_poi, menu);
		return true;
	}

}
