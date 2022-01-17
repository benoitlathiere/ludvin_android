package org.ludvin.masterhandi2013.visiteur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceRecognition extends Activity implements OnClickListener, TextToSpeech.OnInitListener {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private static final int VOICE_RECOGNITION_NB_MAX_RESULTS = 5;
	private static final String TAG = "visiteur";
	private static final int LEXIQUE_SIZE = 9;
	private Button btnStart;
	private TextView txtResult;
	private ListView listResult;
	private TextToSpeech tts;
	private static final String[] tmpLexique = {"où suis-je", "point d'intérêt", "toilette", "ascenseur", "escalier", "quitter", "aide", "pizza", "wifi", "SOS"};
	private static final List<String> Lexique = Arrays.asList(tmpLexique);
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recognition);
        
        btnStart = (Button) this.findViewById(R.id.button1);
        btnStart.setOnClickListener(this);
        txtResult = (TextView) this.findViewById(R.id.textView1);
        listResult = (ListView) this.findViewById(R.id.listView1);
        Toast toast = Toast.makeText(this, "Lancement de la commande vocale.", Toast.LENGTH_SHORT);toast.show();
		tts = new TextToSpeech(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_voice_recognition, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
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
					Toast.makeText(getApplicationContext(), "empty list", Toast.LENGTH_SHORT).show();
				} else {
					//listResults.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results));
					//tts.speak("RŽsultats", TextToSpeech.QUEUE_ADD, null);
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
						tts.speak("Pas dans le lexique !", TextToSpeech.QUEUE_ADD, null);
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
				tts.speak("Je n'ai pas compris", TextToSpeech.QUEUE_ADD, null);
			}
		}
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.FRENCH);
		} else {
			Log.e("TTS", "init failed");
		}
	}
	
	private void processCommand(String cmd) {
		if (cmd.equals(Lexique.get(0))) { //localisation
			txtResult.setText("Résultat : " + "Vous êtes ici.");
			tts.speak("Vous tes ici", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(1))) { //POIs
			txtResult.setText("Résultat : " + "Pas de POI ici.");
			tts.speak("Pas de POI ici", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(2))) { //toilettes
			txtResult.setText("RŽsultat : " + "Les toilettes sont lˆ-bas.");
			tts.speak("Les toilettes sont lˆ-bas", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(3))) { //ascenseur
			txtResult.setText("Résultat : " + "Prenez les escaliers.");
			tts.speak("Prenez les escaliers", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(4))) { //escalier
			txtResult.setText("Résultat : " + "Prenez l'ascenseur.");
			tts.speak("Prenez l'ascenseur", TextToSpeech.QUEUE_ADD, null);
		}
		
		if (cmd.equals(Lexique.get(5))) { //quitter
			QuitAppDialog dialog = new QuitAppDialog();
			dialog.show(getFragmentManager(), "quitter");
		}
		if (cmd.equals(Lexique.get(6))) { //aide
			listResult.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Lexique));
		}
		if (cmd.equals(Lexique.get(7))) { //pizza
			tts.speak("Ce n'est pas bon pour le régime", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(8))) { //wifi
			tts.speak("Vous êtes ici", TextToSpeech.QUEUE_ADD, null);
		}
		if (cmd.equals(Lexique.get(9))) { //SOS
			tts.speak("Vous avez dit SOS", TextToSpeech.QUEUE_ADD, null);
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
	}
	
	
	public static class QuitAppDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //getActivity() requires API 11 !
			builder.setMessage("Voulez vous quitter l'application ?");
			builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
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
	
}
