package com.example.puissance4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    EditText log;
    Button BTG;
    String recupe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        log = (EditText)findViewById(R.id.NomJ);
        BTG =(Button)findViewById(R.id.BTN_GO);


        BTG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recupe = log.getText().toString();
                int NBL =log.length();
                if(recupe.matches(""))
                {

                    Toast MSG1 = Toast.makeText(Main2Activity.this, "Entrez un pseudo"+recupe, Toast.LENGTH_SHORT);
                    MSG1.show();



                }
                else
                {
                    Intent i = new Intent(Main2Activity.this,Home.class);
                    i.putExtra("edittext", recupe);
                    i.putExtra("nbLettre", NBL);
                    startActivity(i);
                }

            }


        });







    }
}
