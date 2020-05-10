package fr.cyriaque.tictactoe.database;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public class CreationPartie {

    public static final String TICTACTOE_DATABASE = "TicTacToe";
    public static final String TICTACTOE_CREATIONPARTIE_COLLECTION = "CreationPartie";

    private final ObjectId _id;
    private final String code;
    private final ObjectId idCreateur;
    private final ObjectId idJoueur;
    private final boolean valide;

    public CreationPartie(ObjectId id, String code, ObjectId idCreateur, ObjectId idJoueur, boolean valide) {
        _id = id;
        this.code = code;
        this.idCreateur = idCreateur;
        this.idJoueur = idJoueur;
        this.valide = valide;
    }

    public static String getTictactoeDatabase() {
        return TICTACTOE_DATABASE;
    }

    public static String getTictactoeCreationpartieCollection() {
        return TICTACTOE_CREATIONPARTIE_COLLECTION;
    }

    public ObjectId get_id() {
        return _id;
    }

    public String getCode() {
        return code;
    }

    public ObjectId getIdCreateur() {
        return idCreateur;
    }

    public ObjectId getIdJoueur() {
        return idJoueur;
    }

    public boolean isValide() {
        return valide;
    }

    static BsonDocument toBsonDocument(final CreationPartie creationPartie) {
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(CreationPartie.Fields.ID, new BsonObjectId(creationPartie.get_id()));
        asDoc.put(CreationPartie.Fields.CODE, new BsonString(creationPartie.getCode()));
        asDoc.put(CreationPartie.Fields.IDCREATEUR, new BsonObjectId(creationPartie.getIdCreateur()));
        asDoc.put(CreationPartie.Fields.IDJOUEUR, new BsonObjectId(creationPartie.getIdJoueur()));
        asDoc.put(CreationPartie.Fields.VALIDE, new BsonBoolean(creationPartie.isValide()));
        return asDoc;
    }

    static CreationPartie fromBsonDocument(final BsonDocument doc) {
        return new CreationPartie(
                doc.getObjectId(CreationPartie.Fields.ID).getValue(),
                doc.getString(CreationPartie.Fields.CODE).getValue(),
                doc.getObjectId(CreationPartie.Fields.IDCREATEUR).getValue(),
                doc.getObjectId(CreationPartie.Fields.IDJOUEUR).getValue(),
                doc.getBoolean(CreationPartie.Fields.VALIDE).getValue()
                );
    }

    static final class Fields {
        static final String ID = "_id";
        static final String CODE = "code";
        static final String IDCREATEUR = "idCreateur";
        static final String IDJOUEUR = "idJoueur";
        static final String VALIDE = "valide";
    }


    public static final Codec<CreationPartie> codec = new Codec<CreationPartie>() {


        @Override
        public void encode(BsonWriter writer, CreationPartie value, EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<CreationPartie> getEncoderClass() {
            return CreationPartie.class;
        }

        @Override
        public CreationPartie decode(BsonReader reader, DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };

}
