package de.androidnewcomer.einzelbeispiel;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.net.*;

public class MainActivity extends Activity{

    TextView textViewAnswer;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonAbschicken = (Button) findViewById(R.id.buttonAbschicken);
        final Button buttonBerechnen = (Button) findViewById(R.id.buttonBerechnung);
        textViewAnswer = (TextView) findViewById(R.id.textViewServerAntwort);
        editText = (EditText) findViewById(R.id.editText);
        buttonAbschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String matrikelnr = editText.getText().toString();
                SimpleThread t = new SimpleThread(matrikelnr);

                t.start();

                try {
                    t.join();
                }catch (InterruptedException ie){
                    //
                }

                textViewAnswer.setText(t.answer);
                if (!t.answer.equals("Dies ist keine gueltige Matrikelnummer")){
                buttonBerechnen.setVisibility(View.VISIBLE);
                }
            }
        });
        buttonBerechnen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textviewBerechnung = (TextView) findViewById(R.id.textViewBerechnung);
                String matrikelnr = editText.getText().toString();
                int[] matrikelarray = new int[matrikelnr.length()];
                char[] newmatrikel = new char[matrikelnr.length()];
                char[] character = new char[11];

                for (int i = 0; i < matrikelarray.length; i++) {
                    matrikelarray[i] = Character.getNumericValue(matrikelnr.charAt(i));
                }
                char i = 'a';
                character[0] = '0';
                for (int j = 1; j < character.length; j++) {
                    character[j] = i;
                    i++;
                }
                for (int j = 0; j < newmatrikel.length; j++) {
                    if (j%2 == 1) newmatrikel[j] = character[matrikelarray[j]];
                    else newmatrikel[j] = (char) (matrikelarray[j]+'0');
                }
                String newmatrikelnr = new String(newmatrikel);
                textviewBerechnung.setText(newmatrikelnr);
            }
        });
    }
}

class SimpleThread extends Thread{
    String matrikelnr;
    String answer;
    public SimpleThread(String matrikelnr) {
        this.matrikelnr = matrikelnr;
    }

    @Override
    public void run (){
        try{
            String sentence = matrikelnr;

            Socket clientSocket = new Socket("se2-isys.aau.at", 53212);

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            outToServer.writeBytes(sentence + "\n");

            answer = inFromServer.readLine();

            clientSocket.close();
        }catch (Exception e){
            answer = "crashed";
        }
    }
}
