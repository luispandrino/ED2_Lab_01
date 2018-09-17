package com.example.luisp.ed2_lab_01;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main extends AppCompatActivity {

    private Button btnUpload;
    private Button btnCod;
    private EditText txt;
    String texto = "";
    String AUX = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnUpload = (Button)findViewById(R.id.btnSubir);
        btnCod = (Button) findViewById(R.id.btnCodificar);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("*/*")
                        .setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccione un Archivo"),123);
            }
        });

        btnCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char[] msgChar = texto.toCharArray();
                ArrayList<Character> characters = new ArrayList<Character>();

                for (int i = 0; i < msgChar.length; i++){
                    if (!(characters.contains(msgChar[i]))){
                        characters.add(msgChar[i]);

                    }
                }

                int[]countChars= new int[characters.size()];
                for (int x = 0; x<countChars.length;x++){
                    countChars[x]=0;
                }

                for (int i= 0; i < characters.size(); i++){
                    char checker = characters.get(i);
                    for (int x = 0; x < msgChar.length ; x++){
                        if (checker == msgChar[x]){
                            countChars[i]++;
                        }
                    }
                }

                for (int i = 0; i < countChars.length - 1; i++) {
                    for (int j = 0; j < countChars.length - 1; j++) {
                        if (countChars[j] < countChars[j + 1]) {
                            int temp = countChars[j];
                            countChars[j] = countChars[j + 1];
                            countChars[j + 1] = temp;

                            char tempChar = characters.get(j);
                            characters.set(j, characters.get(j + 1));
                            characters.set(j + 1, tempChar);
                        }
                    }
                }
                for (int i = 0; i < characters.size(); i++ ){
                    AUX = AUX + characters.get(i) + "---->" + countChars[i] + "\n";
                }




            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 123 && resultCode == RESULT_OK){
            Uri selectedFile = data.getData();
            Toast.makeText(this,selectedFile.toString(),Toast.LENGTH_LONG).show();
            Toast.makeText(this,selectedFile.getPath(),Toast.LENGTH_LONG).show();

            try{
                ReadText(selectedFile);
                texto = ReadText(selectedFile);

            }catch (IOException e)
            {
                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
            }
        }

    }



    private String ReadText(Uri uri) throws IOException{
        InputStream Is =   getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(Is));
        StringBuilder stringBuilder = new StringBuilder();
        String Line;
        while ((Line = reader.readLine())!= null){
            stringBuilder.append(Line);


        }
        Is.close();
        reader.close();
        return stringBuilder.toString();



    }
}
