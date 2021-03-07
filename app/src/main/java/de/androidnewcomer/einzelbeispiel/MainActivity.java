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
        final String matrikelnr = editText.getText().toString();
        buttonAbschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
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
                char[] matrikelarray = new char[matrikelnr.length()];
                char[] character = new char[11];

                for (int i = 0; i < matrikelarray.length; i++) {
                    matrikelarray[i] = matrikelnr.charAt(i);
                }
                char i = 'a';
                character[0] = '0';
                for (int j = 1; j < character.length; j++) {
                    character[j] = i;
                    i++;
                }
                for (int j = 1; j < matrikelarray.length; j=j+2) {
                    for (int k = 0; k < character.length; k++) {
                        if (k == matrikelarray[j]) matrikelarray[j] = character[k];
                    }
                }
                String newmatrikelnr = new String(matrikelarray);
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
        String fail = "gar nichts";
        try{
            String sentence = matrikelnr;

            fail = "clientSocket";
            Socket clientSocket = new Socket("se2-isys.aau.at", 53212);

            fail = "inFromServer";
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            fail = "outToServer";
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            fail = "Matrikelnr";
            outToServer.writeBytes(sentence + "\n");

            fail = "Text";
            answer = inFromServer.readLine();

            clientSocket.close();
        }catch (Exception e){
            answer = "crashed";
        }
    }
}
