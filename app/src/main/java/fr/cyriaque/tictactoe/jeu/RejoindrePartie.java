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
import com.google.android.material.textfield.TextInputEditText;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.internal.common.BsonUtils;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;

import fr.cyriaque.tictactoe.R;
import fr.cyriaque.tictactoe.database.CreationPartie;
import fr.cyriaque.tictactoe.database.Partie;

public class RejoindrePartie extends AppCompatActivity {
    StitchAppClient stitchClient = null;
    private RemoteMongoCollection<CreationPartie> creationPartie;
    private RemoteMongoCollection<Partie> partie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_partie);

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
        ObjectId objectId;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                objectId = null;
            } else {
                objectId = (ObjectId) extras.get("UserID");
            }
        } else {
            objectId = (ObjectId) savedInstanceState.getSerializable("UserID");
        }

        Button valider =  findViewById(R.id.rejoindrePartie);
        Button retour = findViewById(R.id.retourAccueil);
        TextView erreurCode = findViewById(R.id.erreurCodeRejoindre);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText codeRejoindre = findViewById(R.id.codeRejoindre);

                if(codeRejoindre.getText().toString().trim().length() > 0){

                    getPartieByCode(codeRejoindre.getText().toString().toUpperCase()).addOnCompleteListener(new OnCompleteListener<CreationPartie>() {
                        @Override
                        public void onComplete(@NonNull Task<CreationPartie> task) {

                            if (!task.getResult().getCode().equals(codeRejoindre.getText().toString().toUpperCase())) {
                                erreurCode.setText("Le code "+codeRejoindre.getText()+" est faux" );
                                erreurCode.setVisibility(View.VISIBLE);
                            }
                            else if (task.isSuccessful()) {
                                if(!task.getResult().isValide()){
                                    Document filterDoc = new Document().append("_id", task.getResult().get_id());
                                    Document updateDoc = new Document().append("$set",
                                            new Document()
                                                    .append("idJoueur", objectId)
                                                    .append("valide", true)
                                    );

                                    final Task<RemoteUpdateResult> updateTask =
                                            creationPartie.updateOne(filterDoc, updateDoc);
                                    updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
                                        @Override
                                        public void onComplete(@NonNull Task <RemoteUpdateResult> task2) {
                                            if (task2.isSuccessful()) {
                                                ObjectId id1 = new ObjectId();
                                                creerJeu(id1,task.getResult().get_id(),task.getResult().getIdCreateur());
                                                Intent intent = new Intent(RejoindrePartie.this,Jeu.class);
                                                intent.putExtra("MonID",objectId);
                                                intent.putExtra("IdCreationPartie", task.getResult().get_id());
                                                startActivity(intent);
                                            } else {
                                                Log.e("app", "failed to update document with: ", task2.getException());
                                            }
                                        }
                                    });

                                }else{
                                    erreurCode.setText("La partie est deja lancée" );
                                    erreurCode.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Log.e("app", "Failed to findOne: ", task.getException());
                            }
                        }
                    });

                }else{

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

    private Task<CreationPartie> getPartie(ObjectId objectId){
        Document doc = new Document();
        doc.append("_id",objectId);

        return creationPartie.findOne(doc);
    }

    private Task<CreationPartie> getPartieByCode(String code){
        Document doc = new Document();
        return creationPartie.findOne(doc);
    }

    public void creerJeu(ObjectId _id,ObjectId objectIdPartie,ObjectId objectIdJoueur){

        int Case1 = 0;
        int Case2 = 0;
        int Case3 = 0;
        int Case4 = 0;
        int Case5 = 0;
        int Case6 = 0;
        int Case7 = 0;
        int Case8 = 0;
        int Case9 = 0;


        final Partie newPartie = new Partie(_id,objectIdPartie,Case1,Case2,Case3,Case4,Case5,Case6,Case7,Case8,Case9,objectIdJoueur);
        partie.insertOne(newPartie).addOnFailureListener(e -> Log.e("app", "failed to insert todo item", e));
    }
}
