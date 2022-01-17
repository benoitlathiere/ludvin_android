package org.ludvin.masterhandi2013.visiteur;


import android.app.Application;
import android.widget.Toast;

/**
 * Classe permettant de définir des méthodes et variables globales à l'application
 * Source : http://stackoverflow.com/questions/708012/android-how-to-declare-global-variables
 * @author Projet LUD'VIN
 *
 */
public class GlobalVars extends Application{
	
	private boolean Accessible=false;	//Mode Accessible de LUDVIN
	private boolean Wifi=false;
	
	/**
	 * Retourne l'état du mode Accessible
	 * @return	Boolean
	 */
	public boolean getAccessible() {
		return this.Accessible;
	}
	
	/**
	 * Permet de changer l'état du mode Accessible
	 * @param b	Booléen pour changer l'état
	 */
	public void setAccessible(boolean b) {
		this.Accessible=b;
	}

	/**
	 * Bascule l'état du mode Accessible
	 * @param b
	 */
	public void toggleAccessible() {
		if (this.Accessible) {
			this.Accessible=false;
			{Toast toast = Toast.makeText(this, "Accessibilité désactivée", Toast.LENGTH_SHORT);toast.show();}
		} else {
			this.Accessible=true;
			{Toast toast = Toast.makeText(this, "Accessibilité activée", Toast.LENGTH_SHORT);toast.show();}
		}
	}
	
	
	public boolean getWifi() {
		return this.Wifi;
	}
	
	public void setWifi(boolean b) {
		this.Wifi=b;
	}
	


}
