package org.ludvin.masterhandi2013.visiteur;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CommandeVocale extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commande_vocale);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.commande_vocale, menu);
		return true;
	}

}
