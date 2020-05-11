package fr.cyriaque.tictactoe.jeu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.internal.common.BsonUtils;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import fr.cyriaque.tictactoe.R;
import fr.cyriaque.tictactoe.database.JoueurLigne;

public class MainActivity extends AppCompatActivity {

    StitchAppClient stitchClient = null;
    private RemoteMongoCollection<JoueurLigne> items;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.stitchClient = Stitch.getDefaultAppClient();

        StitchAuth auth = stitchClient.getAuth();

        // The Stitch app is configured for Anonymous login in the Stitch UI
        auth.loginWithCredential(new AnonymousCredential()).addOnCompleteListener(new OnCompleteListener<StitchUser>() {
            @Override
            public void onComplete(@NonNull Task<StitchUser> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Anonymous login successful!");
                } else {
                    Log.e(TAG, "Anonymous login failed!", task.getException());
                }
            }
        });
        final RemoteMongoClient mongoClient = stitchClient.getServiceClient(
                RemoteMongoClient.factory, "mongodb-atlas");

        items = mongoClient
                .getDatabase(JoueurLigne.TICTACTOE_DATABASE)
                .getCollection(JoueurLigne.TICTACTOE_JOUEURLIGNE_COLLECTION, JoueurLigne.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(JoueurLigne.codec)));



        setContentView(R.layout.activity_main);
        Button valider =  findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText codeMain = findViewById(R.id.codeMain);

                if (codeMain.getText().toString().trim().length() > 0){
                    ObjectId objectId = new ObjectId();
                    addJoueur(objectId,codeMain.getText().toString().trim());


                    Intent intent = new Intent(MainActivity.this,Menu.class);
                    intent.putExtra("UserID",objectId);
                    intent.putExtra("Pseudo",codeMain.getText().toString().trim());
                    startActivity(intent);
                }else{
                    TextView codeErreur = findViewById(R.id.codeErreur);
                    codeErreur.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addJoueur(ObjectId objectId,final String pseudo) {
        final JoueurLigne newItem = new JoueurLigne(objectId, pseudo);
        items.insertOne(newItem).addOnFailureListener(e -> Log.e(TAG, "failed to insert todo item", e));
    }
    private Task<ArrayList<JoueurLigne>> getJoueurs() {
        return items.find().into(new ArrayList<JoueurLigne>());
    }
}
