package fr.cyriaque.tictactoe.database;

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

public class JoueurLigne {

    public static final String TICTACTOE_DATABASE = "TicTacToe";
    public static final String TICTACTOE_JOUEURLIGNE_COLLECTION = "JoueurLigne";

    private ObjectId _id;
    private String pseudo;

    public JoueurLigne(ObjectId id, String pseudo) {
        this._id = id;
        this.pseudo = pseudo;
    }

    public ObjectId get_id() {
        return _id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    static BsonDocument toBsonDocument(final JoueurLigne joueurLigne) {
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Fields.ID, new BsonObjectId(joueurLigne.get_id()));
        asDoc.put(Fields.PSEUDO, new BsonString(joueurLigne.getPseudo()));
        return asDoc;
    }

    static JoueurLigne fromBsonDocument(final BsonDocument doc) {
        return new JoueurLigne(
                doc.getObjectId(Fields.ID).getValue(),
                doc.getString(Fields.PSEUDO).getValue()
        );
    }


    static final class Fields {
        static final String ID = "_id";
        static final String PSEUDO = "pseudo";
    }

    public static final Codec<JoueurLigne> codec = new Codec<JoueurLigne>() {


        @Override
        public void encode(BsonWriter writer, JoueurLigne value, EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<JoueurLigne> getEncoderClass() {
            return JoueurLigne.class;
        }

        @Override
        public JoueurLigne decode(BsonReader reader, DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };
}
