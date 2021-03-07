package de.androidnewcomer.einzelbeispiel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.*;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

    TextView textViewAnswer;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        textViewAnswer = (TextView) findViewById(R.id.textViewServerAntwort);
        editText = (EditText) findViewById(R.id.editText);
        button.setOnClickListener(this);
    }
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
