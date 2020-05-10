package fr.cyriaque.tictactoe.database;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public class Partie {

    public static final String TICTACTOE_DATABASE = "TicTacToe";
    public static final String TICTACTOE_PARTIE_COLLECTION = "Partie";

    private final ObjectId _id;
    private final ObjectId idCreationPartie;
    private final int Case1;
    private final int Case2;
    private final int Case3;
    private final int Case4;
    private final int Case5;
    private final int Case6;
    private final int Case7;
    private final int Case8;
    private final int Case9;
    private final ObjectId joueur;

    public Partie(ObjectId _id, ObjectId idCreationPartie, int case1, int case2, int case3, int case4, int case5, int case6, int case7, int case8, int case9, ObjectId joueur) {
        this._id = _id;
        this.idCreationPartie = idCreationPartie;
        Case1 = case1;
        Case2 = case2;
        Case3 = case3;
        Case4 = case4;
        Case5 = case5;
        Case6 = case6;
        Case7 = case7;
        Case8 = case8;
        Case9 = case9;
        this.joueur = joueur;
    }

    public ObjectId get_Id() {
        return _id;
    }

    public ObjectId getIdCreationPartie() {
        return idCreationPartie;
    }

    public int getCase1() {
        return Case1;
    }

    public int getCase2() {
        return Case2;
    }

    public int getCase3() {
        return Case3;
    }

    public int getCase4() {
        return Case4;
    }

    public int getCase5() {
        return Case5;
    }

    public int getCase6() {
        return Case6;
    }

    public int getCase7() {
        return Case7;
    }

    public int getCase8() {
        return Case8;
    }

    public int getCase9() {
        return Case9;
    }

    public ObjectId getJoueur() {
        return joueur;
    }

    static BsonDocument toBsonDocument(final Partie partie) {
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Partie.Fields.ID, new BsonObjectId(partie.get_Id()));
        asDoc.put(Fields.IDCREATIONPARTIE, new BsonObjectId(partie.getIdCreationPartie()));
        asDoc.put(Fields.CASE1, new BsonInt32(partie.getCase1()));
        asDoc.put(Fields.CASE2, new BsonInt32(partie.getCase2()));
        asDoc.put(Fields.CASE3, new BsonInt32(partie.getCase3()));
        asDoc.put(Fields.CASE4, new BsonInt32(partie.getCase4()));
        asDoc.put(Fields.CASE5, new BsonInt32(partie.getCase5()));
        asDoc.put(Fields.CASE6, new BsonInt32(partie.getCase6()));
        asDoc.put(Fields.CASE7, new BsonInt32(partie.getCase7()));
        asDoc.put(Fields.CASE8, new BsonInt32(partie.getCase8()));
        asDoc.put(Fields.CASE9, new BsonInt32(partie.getCase9()));
        asDoc.put(Fields.JOUEUR, new BsonObjectId(partie.getJoueur()));
        return asDoc;
    }

    static Partie fromBsonDocument(final BsonDocument doc) {
        return new Partie(
                doc.getObjectId(Partie.Fields.ID).getValue(),
                doc.getObjectId(Partie.Fields.IDCREATIONPARTIE).getValue(),
                doc.getInt32(Partie.Fields.CASE1).getValue(),
                doc.getInt32(Partie.Fields.CASE2).getValue(),
                doc.getInt32(Partie.Fields.CASE3).getValue(),
                doc.getInt32(Partie.Fields.CASE4).getValue(),
                doc.getInt32(Partie.Fields.CASE5).getValue(),
                doc.getInt32(Partie.Fields.CASE6).getValue(),
                doc.getInt32(Partie.Fields.CASE7).getValue(),
                doc.getInt32(Partie.Fields.CASE8).getValue(),
                doc.getInt32(Partie.Fields.CASE9).getValue(),
                doc.getObjectId(Fields.JOUEUR).getValue()

        );
    }


    static final class Fields {
        static final String ID = "_id";
        static final String IDCREATIONPARTIE = "idCreationPartie";
        static final String CASE1 = "case1";
        static final String CASE2 = "case2";
        static final String CASE3 = "case3";
        static final String CASE4 = "case4";
        static final String CASE5 = "case5";
        static final String CASE6 = "case6";
        static final String CASE7 = "case7";
        static final String CASE8 = "case8";
        static final String CASE9 = "case9";
        static final String JOUEUR = "joueur";
    }

    public static final Codec<Partie> codec = new Codec<Partie>() {


        @Override
        public void encode(BsonWriter writer, Partie value, EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<Partie> getEncoderClass() {
            return Partie.class;
        }

        @Override
        public Partie decode(BsonReader reader, DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return Partie.fromBsonDocument(document);
        }
    };
}
