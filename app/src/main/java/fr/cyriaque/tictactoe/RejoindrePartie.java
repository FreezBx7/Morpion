package fr.cyriaque.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class RejoindrePartie extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_partie);

        Button valider =  findViewById(R.id.rejoindrePartie);
        Button retour = findViewById(R.id.retourAccueil);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText codeRejoindre = findViewById(R.id.codeRejoindre);

                if(codeRejoindre.getText().toString().trim().length() > 0){
                    Intent intent = new Intent(RejoindrePartie.this,Jeu.class);
                    startActivity(intent);
                }else{
                    TextView erreurCode = findViewById(R.id.erreurCodeRejoindre);
                    erreurCode.setVisibility(View.VISIBLE);
                }

            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RejoindrePartie.this,Menu.class);
                startActivity(intent);
            }
        });
    }
}
