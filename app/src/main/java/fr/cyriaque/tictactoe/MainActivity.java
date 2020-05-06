package fr.cyriaque.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button valider =  findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText codeMain = findViewById(R.id.codeMain);

                if (codeMain.getText().toString().trim().length() > 0){
                    Intent intent = new Intent(MainActivity.this,Menu.class);
                    startActivity(intent);
                }else{
                    TextView codeErreur = findViewById(R.id.codeErreur);
                    codeErreur.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
