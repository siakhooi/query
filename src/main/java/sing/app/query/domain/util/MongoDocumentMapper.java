package sing.app.query.domain.util;

import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDocumentMapper {

    private MongoDocumentMapper() {
    }

    public static Map<String, Object> toMap(Document doc) {
        if (doc == null) {
            return Map.of();
        }

        Map<String, Object> row = new HashMap<>();
        doc.forEach((key, value) -> {
            if (value instanceof ObjectId) {
                row.put(key, value.toString());
            } else if (value instanceof Document) {
                row.put(key, toMap((Document) value));
            } else {
                row.put(key, value);
            }
        });
        return row;
    }
}
