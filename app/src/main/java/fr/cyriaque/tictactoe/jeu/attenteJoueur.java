package fr.cyriaque.tictactoe.jeu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.AsyncChangeStream;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.internal.common.BsonUtils;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

import fr.cyriaque.tictactoe.R;
import fr.cyriaque.tictactoe.database.CreationPartie;
import fr.cyriaque.tictactoe.database.Partie;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class attenteJoueur extends AppCompatActivity {
    private static final String TAG = attenteJoueur.class.getSimpleName();

    StitchAppClient stitchClient = null;
    private RemoteMongoCollection<CreationPartie> creationPartie;
    private RemoteMongoCollection<Partie> partie;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attente_joueur);
        this.stitchClient = Stitch.getDefaultAppClient();
        final RemoteMongoClient mongoClient = stitchClient.getServiceClient(
                RemoteMongoClient.factory, "mongodb-atlas");
        creationPartie = mongoClient
                .getDatabase(CreationPartie.TICTACTOE_DATABASE)
                .getCollection(CreationPartie.TICTACTOE_CREATIONPARTIE_COLLECTION, CreationPartie.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(CreationPartie.codec)));

        partie = mongoClient
                .getDatabase(Partie.TICTACTOE_DATABASE)
                .getCollection(Partie.TICTACTOE_PARTIE_COLLECTION, Partie.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(Partie.codec)));

        TextView codeRejoindre = findViewById(R.id.codeRejoindre2);
        ObjectId partieID;
        String pseudo;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                partieID = null;
                pseudo = "";
            } else {
                partieID = (ObjectId) extras.get("PartieID");
                pseudo = extras.getString("Pseudo");
            }
        } else {
            partieID = (ObjectId) savedInstanceState.getSerializable("PartieID");
            pseudo = (String) savedInstanceState.getSerializable("Pseudo");
        }

        getPartie(partieID).addOnSuccessListener(item -> {
            codeRejoindre.setText(item.getCode());
        });



        creationPartie.watchWithFilter(new BsonDocument("fullDocument.valide", new BsonBoolean(true)))
                .addOnCompleteListener(task -> {
                    AsyncChangeStream<CreationPartie, ChangeEvent<CreationPartie>> changeStream = task.getResult();
                    changeStream.addChangeEventListener((BsonValue documentId, ChangeEvent<CreationPartie> event) -> {

                        getPartie(partieID).addOnSuccessListener(item -> {

                            Intent intent = new Intent(attenteJoueur.this,Jeu.class);
                            intent.putExtra("IdCreationPartie", partieID);
                            intent.putExtra("MonID",item.getIdCreateur());
                            intent.putExtra("Pseudo", pseudo);
                            startActivity(intent);
                        });


                    });
                });

        Button annuler = findViewById(R.id.annuler);
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePartie(partieID);
                Intent intent = new Intent(attenteJoueur.this,Menu.class);
                intent.putExtra("Pseudo",pseudo);
                startActivity(intent);
            }
        });
    }

    private Task<CreationPartie> getPartie(ObjectId objectId){
        Document doc = new Document();
        doc.append("_id",objectId);

        return creationPartie.findOne(doc);
    }

    private void deletePartie(ObjectId partieID){
        Document doc = new Document();
        doc.append("_id",partieID);
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
