package com.example.luisp.ed2_lab_01;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    Huffman.compress(texto);
                } catch (IOException e) {
                    e.printStackTrace();
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
