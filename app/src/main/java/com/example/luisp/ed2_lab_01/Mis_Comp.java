package com.example.luisp.ed2_lab_01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class Mis_Comp extends AppCompatActivity {
    Double Razon;
    Double Factor;
    String NombreCod;
    String NombreOrg;
    String Aux;
    private ListView list;
    private ArrayList<String> Lista;
    private ArrayAdapter<String> adapter;
    private Button btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_comp);

        list = (ListView) findViewById(R.id.lstComp);
        btnUpdate = (Button) findViewById(R.id.btnActualizar);
        Lista = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom, Lista);
        list.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        Aux = extras.getString("Resultado");
        NombreOrg = extras.getString("Nombre_Original");
        NombreCod =  extras.getString("Nombre_Codificado");
        Factor = extras.getDouble("Factor");
        Razon = extras.getDouble("Razon");
        Lista.add(Aux);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapter.notifyDataSetChanged();
            }
        });


    }

}