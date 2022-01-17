package org.ludvin.masterhandi2013.visiteur;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Accueil extends Activity implements OnClickListener, OnInitListener {

	static boolean Accessible=false;
	//éléments UI
	ImageButton btnCommandeVocale;
	ImageButton btnZones;
	ImageButton btnPoi;
	ImageButton btnCarte;
	TextView TV_localisation;
	
	//menu contextuel
	private static final int MENU_MODE_AVEUGLE = 0;
	private static final int MENU_COMMANDE_VOCALE = 1;
	private static final int MENU_AIDE = 2;
	private static final int MENU_QUITTER = 3;

	//serveur
	private static final String ws_url= "http://handiman.univ-paris8.fr/~ludvin/xxx.py"; //FIXME : à modifier
	private Calendar cal;

	//divers
	static GlobalVars globalvars ;	//variables globales
	private static final String TAG="visiteur";

	
	//wifi
	TimerTask scanTask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	int secondes = 3;	//secondes entre chaque scan
	
	//commande vocale
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private static final int VOICE_RECOGNITION_NB_MAX_RESULTS = 5;
	private static final String[] tmpLexique = {"où suis-je", "point d'intérêt", "toilette", "ascenseur", "escalier", "quitter", "aide", "pizza", "wifi", "S.O.S."};
	private static final List<String> Lexique = Arrays.asList(tmpLexique);
	private ListView listResult;
	private TextToSpeech tts;
	//private static final int LEXIQUE_SIZE = 9;
	
	
	//TTS global
	String text="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accueil);
		
		/*
		globalvars = ((GlobalVars) getApplication());	//chargement d'un objet global
		if (globalvars.getAccessible())
			{Toast toast = Toast.makeText(this, "Affichage de l'accueil de l'application.", Toast.LENGTH_SHORT);toast.show();}
		*/
		//accessible
		AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE); //source : http://stackoverflow.com/questions/5116867/how-to-know-if-android-talkback-is-active
		Accessible = am.isEnabled();
		
		
		//UI
		btnZones = (ImageButton) findViewById(R.id.btnZones);
		btnZones.setOnClickListener(this);
		btnPoi = (ImageButton) findViewById(R.id.btnPoi);
		btnPoi.setOnClickListener(this);
		btnCommandeVocale = (ImageButton) findViewById(R.id.btnCommandeVocale);
		btnCommandeVocale.setOnClickListener(this);
		btnCarte = (ImageButton) findViewById(R.id.btnCarte);
		btnCarte.setOnClickListener(this);
		TV_localisation = (TextView) findViewById(R.id.TV_localisation);
		
		//globalvars.VerifWifiActive();
		//String text = globalvars.scanner_alentours();	//on scanne
		//{Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);toast.show();}
		
		//TTS
		tts = new TextToSpeech(this, this);
		
	}

	public void CommandeVocale() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, VOICE_RECOGNITION_NB_MAX_RESULTS);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if (results.isEmpty()) {
					//Toast.makeText(getApplicationContext(), "Liste vide.", Toast.LENGTH_SHORT).show();
				} else {
					//listResults.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results));
					Iterator<String> itrResults = results.iterator();
					boolean match = false;
					while (itrResults.hasNext()) {
						String result = itrResults.next();
						Iterator<String> itrLexique = Lexique.iterator();
						while (itrLexique.hasNext()) {		
							String word = itrLexique.next();
							if (result.contains(word)) {
								Log.d(TAG, word + " est dans le lexique.");
								processCommand(word);
								match = true;
								break;
							}
						}
						if (match) //we've found a word in the vocabulary that matches what the user have said
							break;
					}
					if (match == false) {
						Log.d(TAG, "Pas dans le lexique.");
						tts.speak("Aucune commande trouvée dans le lexique !", TextToSpeech.QUEUE_ADD, null);
						//tts.speak("Voilà les phrases que j'ai comprises : " + results.toString().trim(), TextToSpeech.QUEUE_ADD, null);	//debug
					}
				}
			} else {
				//Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();	//debug
				// TODO : vérifier codes de retour pour éviter ça quand on clique sur "Annuler"
				tts.speak("Je n'ai pas compris", TextToSpeech.QUEUE_ADD, null);
			}
		}
	}
	
	private void processCommand(String cmd) {
		if (cmd.equals(Lexique.get(0))) { //localisation
			TV_localisation.setText("Résultat : " + "Vous êtes ici.");
			tts.speak("Vous êtes ici", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(1))) { //POIs
			TV_localisation.setText("Résultat : " + "Pas de POI ici.");
			tts.speak("Pas de POI ici", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(2))) { //toilettes
			TV_localisation.setText("Résultat : " + "Les toilettes sont là-bas.");
			tts.speak("Les toilettes sont là-bas", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(3))) { //ascenseur
			TV_localisation.setText("Résultat : " + "Prenez les escaliers.");
			tts.speak("Prenez les escaliers", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(4))) { //escalier
			TV_localisation.setText("Résultat : " + "Prenez l'ascenseur.");
			tts.speak("Prenez l'ascenseur", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(5))) { //quitter
			tts.speak("Au revoir !.", TextToSpeech.QUEUE_ADD, null);
			this.finish();
		}
		if (cmd.equals(Lexique.get(5))) { //quitter
			//QuitAppDialog dialog = new QuitAppDialog();
			//dialog.show(getFragmentManager(), "quitter");
			this.finish();
		}
		if (cmd.equals(Lexique.get(6))) { //aide
			listResult.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Lexique));
		}
		if (cmd.equals(Lexique.get(7))) { //pizza
			tts.speak("Ce n'est pas bon pour le régime", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(8))) { //wifi
			//tts.speak("Vous êtes ici", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(9))) { //SOS
			tts.speak("Vous avez dit SOS", TextToSpeech.QUEUE_ADD, null);
		}
		//tts.speak(cmd, TextToSpeech.QUEUE_ADD, null);	//debug
	}
	
	
	
	
	@SuppressLint("ValidFragment")
	/**
	 * Boite de dialogue pour confirmation
	 * @author Ben
	 *
	 */
	private class QuitAppDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //getActivity() requires API 11 !
			builder.setMessage("Voulez vous quitter l'application ?");
			builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//
				}
			});
			builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			return builder.create();
		}
	};
	
	
	/**
	 * Construit une architecture JSON 
	 * @author Johana Bodard
	 * @param login
	 * @return
	 */
	public JSONObject buildJsonObject(String login) {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonAPs = new JSONArray();
		try {
			jsonAPs.put(buildJsonAP("HANDIC", "5e:ff:56:a2:af:15", 59));
			jsonAPs.put(buildJsonAP("maison", "98:ea:dc:a6:15:15", 71));
			jsonObj.put("date", cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " +
															 + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
			jsonObj.put("login", login);
			jsonObj.put("bornes", jsonAPs);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
	
	/**
	 * Construit une borne au format JSON
	 * @author Johana Bodard
	 * @param bssid
	 * @param mac
	 * @param signal
	 * @return
	 */
	public JSONObject buildJsonAP(String bssid, String mac, int signal) {
		JSONObject AP = new JSONObject();
		try {
			AP.put("BSSID", bssid);
			AP.put("MAC", mac);
			AP.put("signal", signal);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return AP;
	}
	
	
	@SuppressWarnings("unused")
	private class sendJSON extends AsyncTask<JSONObject, Integer, Integer> {
		protected void onPreExecute() {	//do some setup here
		}
		@Override
		protected Integer doInBackground(JSONObject... jsonMsg) {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(ws_url);
			int nbObj = jsonMsg.length;
			int status = 0;
			for (int i = 0; i < nbObj; ++i) {
				try {
					Log.d(TAG, "Création de l'entité JSON String : ");
					StringEntity entity = new StringEntity(jsonMsg[i].toString());
					entity.setContentType("application/json;charset=UTF-8"); //content-type header
					entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8")); //encoding header
					httpPost.setEntity(entity);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					Log.d(TAG, "Envoi de la requête");
					HttpResponse httpResponse = httpClient.execute(httpPost);
					if (httpResponse == null) {
							Log.d(TAG, "Pas de réponse");
					} else {
						Log.d(TAG, "Status Code " + httpResponse.getStatusLine().getStatusCode());
						status =  httpResponse.getStatusLine().getStatusCode();
					}
				} catch (ClientProtocolException e) {
					Log.d(TAG, "Client Protocol Exception");
					e.printStackTrace();
				} catch (IOException e) {
					Log.d(TAG, "IO Exception");
					e.printStackTrace();
				}
			}
			return status;
		}
		protected void onProgessUpdate(Integer... progress) {
			//eg. show a progress bar
		}
		protected void onPostExecute(JSONObject result) {
			//do some post execution stuff like informing the user about the result of the execution
		}
	}
	
	/**
	 * Envoie un objet JSON sur un serveur
	 * @param j Objet JSON
	 * @return Réponse du serveur
	 */
	public static String transferJSON(JSONObject j) {
	    HttpClient httpclient= new DefaultHttpClient();
		HttpResponse  reponse;
	    HttpPost httppost= new HttpPost(ws_url);
	    try {
	    	StringEntity stringEntity = new StringEntity(j.toString(),"UTF-8"); 
	    	stringEntity.setContentType("application/json");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	    try {
			reponse = httpclient.execute(httppost);
			HttpEntity httpEntity = reponse.getEntity();
	        String xml = EntityUtils.toString(httpEntity);
	        return xml;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.d(TAG,"Exception : ClientProtocolException : Serveur introuvable");
		} catch (UnknownHostException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;  
	}
	
	/**
	 * Teste la connexion à Internet
	 * Necessite la permission  android.permission.ACCESS_NETWORK_STATE 
	 * @return
	 */
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) 
	      getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected())
	        return true;
	    return false;
	}
	
	/**
	 * Gestion des clics sur les boutons
	 * @param View	Vue cliquée
	 */
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.btnZones:
				startActivity (new Intent (this, ListeZones.class));
				break;
			case R.id.btnPoi:
				startActivity (new Intent (this, ListePoi.class));
				break;
			case R.id.btnCommandeVocale :
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, VOICE_RECOGNITION_NB_MAX_RESULTS);
				startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
				break;
			case R.id.btnCarte:
				startActivity (new Intent (this, Carte.class));
				break;
				
		}
	}
	

	/**
	 * Affichage du menu contextuel
	 * @param	Menu	Menu contextuel contenant les options
	 * @return	Boolean	Renvoie vrai/faux si le menu a bien été créé
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.accueil, menu);
		menu.add(0, MENU_COMMANDE_VOCALE, MENU_COMMANDE_VOCALE, "Commande vocale");
		//menu.add(0, MENU_AIDE, MENU_AIDE, "Aide");
		menu.add(0, MENU_QUITTER, MENU_QUITTER, "Quitter");
		
		return true;
	}


	/**
	 * API Android : Change le menu contextuel
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeItem(MENU_MODE_AVEUGLE);
		if(Accessible) {
			menu.add(0, MENU_MODE_AVEUGLE, MENU_MODE_AVEUGLE, "Désactiver le mode Accessible");
		} else {
			menu.add(0, MENU_MODE_AVEUGLE, MENU_MODE_AVEUGLE, "Activer le mode accessible");
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	
	/**
	 * API : Option du menu sélectionnée
	 * @param item Element du menu choisi
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
		switch (item.getItemId()){
			case MENU_MODE_AVEUGLE:
				//globalvars.toggleAccessible();
				if (Accessible)
					Accessible=false;
				else
					Accessible=true;
				break;
			case MENU_AIDE:
				startActivity (new Intent (this, Aide.class));
				break;
			case MENU_COMMANDE_VOCALE:
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, VOICE_RECOGNITION_NB_MAX_RESULTS);
				startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	            break;
			case MENU_QUITTER:
				System.exit(0);
				break;
	        default:
	        	Log.d(TAG,"ERREUR : onOptionsItemSelected() n'a pas de valeur pour item="+String.valueOf(item.getItemId()));
	        	break;
		}
		return true;
    }
	
	
	/**
	 * API : Chargement complet du moteur TTS
	 * @param	Status
	 */
	
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.FRENCH);
		} else {
			Log.e("TTS", "init failed");
		}
	}
	
	
	/**
	 * API : Destruction de l'application par Android
	 */
	protected void onDestroy() {
		super.onDestroy();
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
	}
	
}
