package fr.cyriaque.tictactoe.jeu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.AsyncChangeStream;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.internal.common.BsonUtils;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;

import fr.cyriaque.tictactoe.R;
import fr.cyriaque.tictactoe.database.CreationPartie;
import fr.cyriaque.tictactoe.database.JoueurLigne;
import fr.cyriaque.tictactoe.database.Partie;

public class Jeu extends AppCompatActivity {
    StitchAppClient stitchClient = null;
    private RemoteMongoCollection<Partie> partie;
    private Button bouton1;
    private Button bouton2;
    private Button bouton3;
    private Button bouton4;
    private Button bouton5;
    private Button bouton6;
    private Button bouton7;
    private Button bouton8;
    private Button bouton9;

    private ImageView croix1;
    private ImageView croix2;
    private ImageView croix3;
    private ImageView croix4;
    private ImageView croix5;
    private ImageView croix6;
    private ImageView croix7;
    private ImageView croix8;
    private ImageView croix9;

    private ImageView rond1;
    private ImageView rond2;
    private ImageView rond3;
    private ImageView rond4;
    private ImageView rond5;
    private ImageView rond6;
    private ImageView rond7;
    private ImageView rond8;
    private ImageView rond9;
    private RemoteMongoCollection<JoueurLigne> joueurLigne;
    private RemoteMongoCollection<CreationPartie> creationPartie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);
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
        bouton1 = findViewById(R.id.button1);
        bouton2 = findViewById(R.id.button2);
        bouton3 = findViewById(R.id.button3);
        bouton4 = findViewById(R.id.button4);
        bouton5 = findViewById(R.id.button5);
        bouton6 = findViewById(R.id.button6);
        bouton7 = findViewById(R.id.button7);
        bouton8 = findViewById(R.id.button8);
        bouton9 = findViewById(R.id.button9);

        croix1 = findViewById(R.id.croix1);
        croix2 = findViewById(R.id.croix2);
        croix3 = findViewById(R.id.croix3);
        croix4 = findViewById(R.id.croix4);
        croix5 = findViewById(R.id.croix5);
        croix6 = findViewById(R.id.croix6);
        croix7 = findViewById(R.id.croix7);
        croix8 = findViewById(R.id.croix8);
        croix9 = findViewById(R.id.croix9);

        rond1 = findViewById(R.id.rond1);
        rond2 = findViewById(R.id.rond2);
        rond3 = findViewById(R.id.rond3);
        rond4 = findViewById(R.id.rond4);
        rond5 = findViewById(R.id.rond5);
        rond6 = findViewById(R.id.rond6);
        rond7 = findViewById(R.id.rond7);
        rond8 = findViewById(R.id.rond8);
        rond9 = findViewById(R.id.rond9);
        TextView TourDeQui = findViewById(R.id.tourJoueur);


        ObjectId monID;
        ObjectId IdCreationPartie;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                monID = null;
                IdCreationPartie = null;
            } else {
                monID = (ObjectId) extras.get("MonID");
                IdCreationPartie = (ObjectId) extras.get("IdCreationPartie");
            }
        } else {
            monID = (ObjectId) savedInstanceState.getSerializable("MonID");
            IdCreationPartie = (ObjectId) savedInstanceState.getSerializable("IdCreationPartie");
        }
        Log.d("app",monID.toString() + " / " +IdCreationPartie.toString());
        principalJeu(IdCreationPartie,TourDeQui,monID);


    }

    public void principalJeu(ObjectId IdCreationPartie,TextView TourDeQui,ObjectId monID){
        getPartie(IdCreationPartie).addOnCompleteListener(new OnCompleteListener<Partie>() {
            @Override
            public void onComplete(@NonNull Task<Partie> task) {

                if (!task.getResult().getIdCreationPartie().equals(IdCreationPartie)) {
                    Log.e("app", "Aucunne partie de trouvé a corriger absolument");
                } else if (task.isSuccessful()) {
                    if(!gagner(task.getResult())){
                        getJoueur(task.getResult().getJoueur()).addOnSuccessListener(item -> {
                            TourDeQui.setText(item.getPseudo());

                            if(monID.equals(task.getResult().getJoueur())){
                                //A moi de jouer
                                getPlateauJoueur(task.getResult());
                                cliqueSurBouton(bouton1,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton2,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton3,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton4,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton5,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton6,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton7,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton8,task.getResult().getJoueur(),IdCreationPartie);
                                cliqueSurBouton(bouton9,task.getResult().getJoueur(),IdCreationPartie);

                                getCreationPartie(IdCreationPartie).addOnCompleteListener(new OnCompleteListener<CreationPartie>() {
                                    @Override
                                    public void onComplete(@NonNull Task<CreationPartie> task) {

                                        if (!task.getResult().get_id().equals(IdCreationPartie)) {
                                            Log.e("app", "Aucunne creation partie de trouvé a corriger absolument");
                                        } else if (task.isSuccessful()) {
                                            ObjectId joueur1 = task.getResult().getIdCreateur();
                                            ObjectId joueur2 = task.getResult().getIdJoueur();
                                            partie.watchWithFilter(new BsonDocument("fullDocument.joueur", new BsonObjectId(joueur1)))
                                                    .addOnCompleteListener(task2 -> {
                                                        AsyncChangeStream<Partie, ChangeEvent<Partie>> changeStream = task2.getResult();
                                                        changeStream.addChangeEventListener((BsonValue documentId, ChangeEvent<Partie> event) -> {


                                                            principalJeu(IdCreationPartie,TourDeQui,monID);

                                                        });
                                                    });

                                        } else {
                                            Log.e("app", "Failed to findOne: ", task.getException());
                                        }
                                    }
                                });
                            }else{
                                //a toi de jouer
                                getPlateauAttente(task.getResult());

                                getCreationPartie(IdCreationPartie).addOnCompleteListener(new OnCompleteListener<CreationPartie>() {
                                    @Override
                                    public void onComplete(@NonNull Task<CreationPartie> task) {

                                        if (!task.getResult().get_id().equals(IdCreationPartie)) {
                                            Log.e("app", "Aucunne creation partie de trouvé a corriger absolument");
                                        } else if (task.isSuccessful()) {
                                            ObjectId joueur1 = task.getResult().getIdCreateur();
                                            ObjectId joueur2 = task.getResult().getIdJoueur();
                                            partie.watchWithFilter(new BsonDocument("fullDocument.joueur", new BsonObjectId(joueur2)))
                                                    .addOnCompleteListener(task2 -> {
                                                        AsyncChangeStream<Partie, ChangeEvent<Partie>> changeStream = task2.getResult();
                                                        changeStream.addChangeEventListener((BsonValue documentId, ChangeEvent<Partie> event) -> {

                                                            principalJeu(IdCreationPartie,TourDeQui,monID);


                                                        });
                                                    });

                                        } else {
                                            Log.e("app", "Failed to findOne: ", task.getException());
                                        }
                                    }
                                });



                            }


                        });
                    }else {
                        Intent intent = new Intent(Jeu.this, gagner.class);
                        startActivity(intent);
                    }
                } else {
                    Log.e("app", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void getPlateauJoueur(Partie partieEncours){

                    petitSwitch(partieEncours.getCase1(),bouton1,croix1,rond1);
                    petitSwitch(partieEncours.getCase2(),bouton2,croix2,rond2);
                    petitSwitch(partieEncours.getCase3(),bouton3,croix3,rond3);
                    petitSwitch(partieEncours.getCase4(),bouton4,croix4,rond4);
                    petitSwitch(partieEncours.getCase5(),bouton5,croix5,rond5);
                    petitSwitch(partieEncours.getCase6(),bouton6,croix6,rond6);
                    petitSwitch(partieEncours.getCase7(),bouton7,croix7,rond7);
                    petitSwitch(partieEncours.getCase8(),bouton8,croix8,rond8);
                    petitSwitch(partieEncours.getCase9(),bouton9,croix9,rond9);

    }

    public void getPlateauAttente(Partie partieEncours){

                    petitSwitch2(partieEncours.getCase1(),bouton1,croix1,rond1);
                    petitSwitch2(partieEncours.getCase2(),bouton2,croix2,rond2);
                    petitSwitch2(partieEncours.getCase3(),bouton3,croix3,rond3);
                    petitSwitch2(partieEncours.getCase4(),bouton4,croix4,rond4);
                    petitSwitch2(partieEncours.getCase5(),bouton5,croix5,rond5);
                    petitSwitch2(partieEncours.getCase6(),bouton6,croix6,rond6);
                    petitSwitch2(partieEncours.getCase7(),bouton7,croix7,rond7);
                    petitSwitch2(partieEncours.getCase8(),bouton8,croix8,rond8);
                    petitSwitch2(partieEncours.getCase9(),bouton9,croix9,rond9);


    }

    private Task<Partie> getPartie(ObjectId objectId){
        Document doc = new Document();
        doc.append("idCreationPartie",objectId);

        return partie.findOne(doc);
    }

    private void petitSwitch(int value, Button button, ImageView image1,ImageView image2){
        switch (value){
            case 0:
                button.setVisibility(View.VISIBLE);
                image1.setVisibility(View.INVISIBLE);
                image2.setVisibility(View.INVISIBLE);
                break;
            case 1:
                button.setVisibility(View.INVISIBLE);
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.INVISIBLE);
                break;
            case 2:
                button.setVisibility(View.INVISIBLE);
                image1.setVisibility(View.INVISIBLE);
                image2.setVisibility(View.VISIBLE);
                break;
            default:
                button.setVisibility(View.INVISIBLE);
                image1.setVisibility(View.INVISIBLE);
                image2.setVisibility(View.INVISIBLE);
                break;

        }
    }

    private Task<JoueurLigne> getJoueur(ObjectId objectId){
        Document doc = new Document();
        doc.append("_id",objectId);

        return joueurLigne.findOne(doc);
    }

    private void petitSwitch2(int value, Button button, ImageView image1,ImageView image2){
        switch (value){
            case 1:
                button.setVisibility(View.INVISIBLE);
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.INVISIBLE);
                break;
            case 2:
                button.setVisibility(View.INVISIBLE);
                image1.setVisibility(View.INVISIBLE);
                image2.setVisibility(View.VISIBLE);
                break;
            case 0:
            default:
                button.setVisibility(View.INVISIBLE);
                image1.setVisibility(View.INVISIBLE);
                image2.setVisibility(View.INVISIBLE);
                break;

        }
    }
    private Task<CreationPartie> getCreationPartie(ObjectId objectId){
        Document doc = new Document();
        doc.append("_id",objectId);

        return creationPartie.findOne(doc);
    }

    private void cliqueSurBouton(Button bouton,ObjectId idJoueur,ObjectId IdCreationPartie){
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCreationPartie(IdCreationPartie).addOnCompleteListener(new OnCompleteListener<CreationPartie>() {
                    @Override
                    public void onComplete(@NonNull Task<CreationPartie> task) {

                        if (!task.getResult().get_id().equals(IdCreationPartie)) {
                            Log.e("app", "Aucunne creation partie de trouvé a corriger absolument");
                        } else if (task.isSuccessful()) {
                            ObjectId joueur1 = task.getResult().getIdCreateur();
                            ObjectId joueur2 = task.getResult().getIdJoueur();

                            if(idJoueur.equals(joueur1)){
                                //CROIXXX
                                String stringCase = "";
                                Log.d("app", bouton.getText().toString());
                                switch (bouton.getText().toString()){
                                    case "bouton1":
                                        stringCase = "case1";
                                        break;
                                    case "bouton2":
                                        stringCase = "case2";
                                        break;
                                    case "bouton3":
                                        stringCase = "case3";
                                        break;
                                    case "bouton4":
                                        stringCase = "case4";
                                        break;
                                    case "bouton5":
                                        stringCase = "case5";
                                        break;
                                    case "bouton6":
                                        stringCase = "case6";
                                        break;
                                    case "bouton7":
                                        stringCase = "case7";
                                        break;
                                    case "bouton8":
                                        stringCase = "case8";
                                        break;
                                    case "bouton9":
                                        stringCase = "case9";
                                        break;
                                    default:
                                        stringCase = "";
                                        break;
                                }
                                updatePartie(IdCreationPartie,stringCase,1,joueur2);
                            }else if(idJoueur.equals(joueur2)){
                                //RONDDDD
                            }else{
                                Log.e("app", "GROSSE ERREUR AU NIVEAU DU CLIQUE DU JOUEUR ");
                            }

                        } else {
                            Log.e("app", "Failed to findOne: ", task.getException());
                        }
                    }
                });
            }
        });
    }

    public void updatePartie(ObjectId idCreationPartie, String stringCase, int valueCase,ObjectId newJoueur){
        Document filterDoc = new Document().append("idCreationPartie", idCreationPartie);
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append(stringCase, valueCase)
                        .append("joueur", newJoueur)
        );
        Log.e("app",stringCase+"   "+valueCase+ " joueur :" + newJoueur);
        final Task<RemoteUpdateResult> updateTask =
                partie.updateOne(filterDoc, updateDoc);
        updateTask.addOnSuccessListener(item -> {
            Log.e("app", "coup jouer ");
        });
    }
    public boolean gagner(Partie partieEncours){
        boolean verif = false;
        if(partieEncours.getCase1() != 0 || partieEncours.getCase2() != 0 || partieEncours.getCase3() != 0){
            if(partieEncours.getCase1() == partieEncours.getCase2() && partieEncours.getCase1() == partieEncours.getCase3()){
                verif = true;
            }
        }
        if(partieEncours.getCase4() != 0 || partieEncours.getCase5() != 0 || partieEncours.getCase6() != 0){
            if(partieEncours.getCase4() == partieEncours.getCase5() && partieEncours.getCase4() == partieEncours.getCase6()){
                verif = true;
            }
        }

        if(partieEncours.getCase7() != 0 || partieEncours.getCase8() != 0 || partieEncours.getCase9() != 0){
            if(partieEncours.getCase7() == partieEncours.getCase8() && partieEncours.getCase7() == partieEncours.getCase9()){
                verif = true;
            }
        }
        if(partieEncours.getCase1() != 0 || partieEncours.getCase4() != 0 || partieEncours.getCase7() != 0){
            if(partieEncours.getCase1() == partieEncours.getCase4() && partieEncours.getCase1() == partieEncours.getCase7()){
                verif = true;
            }
        }
        if(partieEncours.getCase2() != 0 || partieEncours.getCase5() != 0 || partieEncours.getCase8() != 0){
            if(partieEncours.getCase2() == partieEncours.getCase5() && partieEncours.getCase2() == partieEncours.getCase8()){
                verif = true;
            }
        }
        if(partieEncours.getCase3() != 0 || partieEncours.getCase6() != 0 || partieEncours.getCase9() != 0){
            if(partieEncours.getCase3() == partieEncours.getCase6() && partieEncours.getCase3() == partieEncours.getCase9()){
                verif = true;
            }
        }
        if(partieEncours.getCase1() != 0 || partieEncours.getCase5() != 0 || partieEncours.getCase9() != 0){
            if(partieEncours.getCase1() == partieEncours.getCase5() && partieEncours.getCase1() == partieEncours.getCase9()){
                verif = true;
            }
        }
        if(partieEncours.getCase3() != 0 || partieEncours.getCase5() != 0 || partieEncours.getCase7() != 0){
            if(partieEncours.getCase3() == partieEncours.getCase5() && partieEncours.getCase3() == partieEncours.getCase7()){
                verif = true;
            }
        }


        return verif;
    }
}
