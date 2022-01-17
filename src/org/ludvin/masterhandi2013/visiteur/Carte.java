package org.ludvin.masterhandi2013.visiteur;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Carte extends Activity implements OnClickListener {

	//UI
	ImageButton btnRetour;
	ImageView testimage;
	
	//divers
	GlobalVars globalvars ;	//variables globale
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carte);
		globalvars = ((GlobalVars)getApplicationContext());	//variables globales
	
		//UI
		btnRetour = (ImageButton) findViewById(R.id.btnRetour);
		btnRetour.setOnClickListener(this);
		testimage = (ImageView) findViewById(R.id.testimage);
		
		//source : http://stackoverflow.com/questions/2739971/overlay-two-images-in-android-to-set-an-imageview
		Resources r = getResources();
		Drawable[] layers = new Drawable[2];
		
		layers[0] = r.getDrawable(R.drawable.pin_black_small);
		layers[1] = r.getDrawable(R.drawable.etage3);
		LayerDrawable layers2 = new LayerDrawable(layers);
		testimage.setImageDrawable(layers2);
		//testimage.setAdjustViewBounds(false);
		//testimage.setRotation(90);
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
		getMenuInflater().inflate(R.menu.carte, menu);
		return true;
	}

}
