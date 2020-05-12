package fr.cyriaque.tictactoe.jeu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;

import java.util.Objects;

import fr.cyriaque.tictactoe.R;
import fr.cyriaque.tictactoe.database.CreationPartie;
import fr.cyriaque.tictactoe.database.JoueurLigne;
import fr.cyriaque.tictactoe.database.Partie;

public class gagner extends AppCompatActivity {

    private RemoteMongoCollection<JoueurLigne> joueurLigne;
    private RemoteMongoCollection<CreationPartie> creationPartie;
    StitchAppClient stitchClient = null;
    private RemoteMongoCollection<Partie> partie;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gagner);

        this.stitchClient = Stitch.getDefaultAppClient();
        final RemoteMongoClient mongoClient = stitchClient.getServiceClient(
                RemoteMongoClient.factory, "mongodb-atlas");
        partie = mongoClient
                .getDatabase(Partie.TICTACTOE_DATABASE)
                .getCollection(Partie.TICTACTOE_PARTIE_COLLECTION, Partie.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(Partie.codec)));
        joueurLigne = mongoClient
                .getDatabase(JoueurLigne.TICTACTOE_DATABASE)
                .getCollection(JoueurLigne.TICTACTOE_JOUEURLIGNE_COLLECTION, JoueurLigne.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(JoueurLigne.codec)));
        creationPartie = mongoClient
                .getDatabase(CreationPartie.TICTACTOE_DATABASE)
                .getCollection(CreationPartie.TICTACTOE_CREATIONPARTIE_COLLECTION, CreationPartie.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(CreationPartie.codec)));


        Button retour = findViewById(R.id.finalRetour);
        TextView finalText = findViewById(R.id.egalite);

        String egalite;
        ObjectId monID;
        ObjectId IdPartie;
        String pseudo;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                monID = null;
                egalite = "";
                IdPartie = null;
                pseudo = "";
            } else {
                monID = (ObjectId) extras.get("MonID");
                IdPartie = (ObjectId) extras.get("IDPARTIE");
                egalite = (String) extras.get("egalite");
                pseudo = extras.getString("Pseudo");
            }
        } else {
            monID = (ObjectId) savedInstanceState.getSerializable("MonID");
            IdPartie = (ObjectId) savedInstanceState.getSerializable("IDPARTIE");
            pseudo = (String) savedInstanceState.getSerializable("Pseudo");
            egalite = (String) savedInstanceState.getSerializable("egalite");
        }
        Log.d("app", "mon Id : "+monID);
        Log.d("app", "IdPArtie : "+IdPartie);
        Log.d("app", "pseudo : "+pseudo);
        Log.d("app", "egalite : "+egalite);
        getPartie(IdPartie).addOnCompleteListener(new OnCompleteListener<Partie>() {
            @Override
            public void onComplete(@NonNull Task<Partie> task) {

                if (task.isSuccessful()) {

                    assert egalite != null;
                    if(egalite.equals("oui")){
                        finalText.setText("EGALITE");
                    }else{
                        if (Objects.equals(monID, task.getResult().getJoueur())) {
                            finalText.setText("Dommage, tu as perdu !");

                        } else {
                            finalText.setText("Felicitation, tu as gagné !");

                        }
                    }
                    retour.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deletePartie(task.getResult());
                            deleteCreationPartie(task.getResult());
                            Intent intent = new Intent(gagner.this, Menu.class);
                            intent.putExtra("Pseudo",pseudo);
                            startActivity(intent);
                        }
                    });




                } else {
                    Log.e("app", "Failed to findOne: ", task.getException());
                }
            }

        });



    }

    private Task<Partie> getPartie(ObjectId objectId){
        Document doc = new Document();
        doc.append("idCreationPartie",objectId);

        return partie.findOne(doc);
    }

    public void deleteCreationPartie(Partie partieEncours){
        Document doc = new Document();
        doc.append("_id",partieEncours.get_Id());
        partie.deleteOne(doc).addOnCompleteListener(new OnCompleteListener<RemoteDeleteResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteDeleteResult> task) {
                if (task.isSuccessful()) {
                    long numDeleted = task.getResult().getDeletedCount();
                    Log.d("app", String.format("successfully deleted %d documents", numDeleted));
                } else {
                    Log.e("app", "failed to delete document with: ", task.getException());
                }
            }
        });
    }
    private void deletePartie(Partie partieEncours){
        Document doc = new Document();
        doc.append("_id",partieEncours.getIdCreationPartie());
        creationPartie.deleteOne(doc).addOnCompleteListener(new OnCompleteListener<RemoteDeleteResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteDeleteResult> task) {
                if (task.isSuccessful()) {
                    long numDeleted = task.getResult().getDeletedCount();
                    Log.d("app", String.format("successfully deleted %d documents", numDeleted));
                } else {
                    Log.e("app", "failed to delete document with: ", task.getException());
                }
            }
        });
    }
}
