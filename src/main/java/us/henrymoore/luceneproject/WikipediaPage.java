package us.henrymoore.luceneproject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@BsonDiscriminator
public class WikipediaPage {
    @BsonId
    ObjectId Id;
    @BsonProperty("Title")
    String title;
    @BsonProperty("Url")
    String url;
    @BsonProperty("Last Modified")
    LocalDateTime lastModifiedDate;
    @BsonProperty("Coordinates")
    Coordinates coordinates;
    @BsonProperty("Number of References")
    int numberOfReferences;
    @BsonProperty("Subheaders")
    List<String> subHeaders;
    @BsonProperty("Categories")
    List<String> categories;
    @BsonProperty("Links")
    List<String> links;
}
