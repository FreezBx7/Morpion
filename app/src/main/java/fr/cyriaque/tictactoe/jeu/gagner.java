package fr.cyriaque.tictactoe.jeu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.bson.types.ObjectId;

import java.util.Objects;

import fr.cyriaque.tictactoe.R;

public class gagner extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gagner);

        Button retour = findViewById(R.id.finalRetour);
        TextView finalText = findViewById(R.id.egalite);

        String egalite;
        ObjectId monID;
        ObjectId IDWiner;
        String pseudo;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                monID = null;
                egalite = "";
                IDWiner = null;
                pseudo = "";
            } else {
                monID = (ObjectId) extras.get("MonID");
                IDWiner = (ObjectId) extras.get("winer");
                egalite = (String) extras.get("egalite");
                pseudo = extras.getString("Pseudo");
            }
        } else {
            monID = (ObjectId) savedInstanceState.getSerializable("MonID");
            IDWiner = (ObjectId) savedInstanceState.getSerializable("winer");
            pseudo = (String) savedInstanceState.getSerializable("Pseudo");
            egalite = (String) savedInstanceState.getSerializable("egalite");
        }

        assert egalite != null;
        if(egalite.equals("oui")){
            finalText.setText("EGALITE");
        }else{
            if (Objects.equals(monID, IDWiner)) {
                finalText.setText("Dommage, tu as perdu !");

            } else {
                finalText.setText("Felicitation, tu as gagné !");

            }
        }

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(gagner.this, Menu.class);
                intent.putExtra("Pseudo",pseudo);
                startActivity(intent);
            }
        });
    }
}
