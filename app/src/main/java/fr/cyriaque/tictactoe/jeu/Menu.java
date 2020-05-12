package fr.cyriaque.tictactoe.jeu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.internal.common.BsonUtils;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;

import java.util.Random;

import fr.cyriaque.tictactoe.R;
import fr.cyriaque.tictactoe.database.CreationPartie;
import fr.cyriaque.tictactoe.database.JoueurLigne;

import static com.mongodb.client.model.Filters.eq;

public class Menu extends AppCompatActivity {

    private static final String TAG = Menu.class.getSimpleName();
    StitchAppClient stitchClient = null;
    private RemoteMongoCollection<CreationPartie> creationPartie;
    private RemoteMongoCollection<JoueurLigne> joueurLigne;
    private JoueurLigne joueur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        this.stitchClient = Stitch.getDefaultAppClient();
        final RemoteMongoClient mongoClient = stitchClient.getServiceClient(
                RemoteMongoClient.factory, "mongodb-atlas");
        creationPartie = mongoClient
                .getDatabase(CreationPartie.TICTACTOE_DATABASE)
                .getCollection(CreationPartie.TICTACTOE_CREATIONPARTIE_COLLECTION, CreationPartie.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(CreationPartie.codec)));

        joueurLigne = mongoClient
                .getDatabase(JoueurLigne.TICTACTOE_DATABASE)
                .getCollection(JoueurLigne.TICTACTOE_JOUEURLIGNE_COLLECTION, JoueurLigne.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(JoueurLigne.codec)));
        joueur = new JoueurLigne(new ObjectId(),"erreur");
        ObjectId objectId;
        String pseudo;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                objectId = null;
                pseudo = "";
            } else {
                objectId = (ObjectId) extras.get("UserID");
                pseudo = extras.getString("Pseudo");
            }
        } else {
            objectId = (ObjectId) savedInstanceState.getSerializable("UserID");
            pseudo = (String) savedInstanceState.getSerializable("Pseudo");

        }

        Button creer = findViewById(R.id.creer);
        Button rejoindre = findViewById(R.id.rejoindre);
        TextView bonjour = findViewById(R.id.bonjour);

        bonjour.setText("Bonjour " + pseudo);

        creer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectId objectIdPartie = new ObjectId();
                creerPartie(objectIdPartie,objectId);
                Log.e("app","ID DE LA PARTIE :"+objectIdPartie);
                Intent intent2 = new Intent(Menu.this,attenteJoueur.class);
                intent2.putExtra("PartieID",objectIdPartie);
                intent2.putExtra("Pseudo",pseudo);
                startActivity(intent2);
            }
        });

        rejoindre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this,RejoindrePartie.class);
                intent.putExtra("UserID",objectId);
                intent.putExtra("Pseudo",pseudo);
                startActivity(intent);
            }
        });
    }

    private Task<JoueurLigne> getJoueur(ObjectId objectId){
        Document doc = new Document();
        doc.append("_id",objectId);

       return joueurLigne.findOne(doc);
    }


    public void creerPartie(ObjectId objectIdPartie,ObjectId objectIdJoueur){
        final CreationPartie newPartie = new CreationPartie(objectIdPartie,random_code(),objectIdJoueur,new ObjectId(),false);
        creationPartie.insertOne(newPartie).addOnFailureListener(e -> Log.e(TAG, "failed to insert todo item", e));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String random_code() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 4;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString.toUpperCase();
    }

}
