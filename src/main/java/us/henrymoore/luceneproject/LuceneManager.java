package us.henrymoore.luceneproject;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static com.mongodb.client.model.Filters.ne;

@Slf4j
@ApplicationScoped
public class LuceneManager {

    @Inject
    MongoClient mongoClient;

    @Inject
    @ConfigProperty(name = "lucene.project.mongo.db_name", defaultValue = "Wikipedia")
    String mongoDatabase;

    @PostConstruct
    void Initialize() {
         log.info("Inserting records from MongoDB");
        String a = "";
        var collection = mongoClient.getDatabase("Wikipedia").getCollection("Articles");
        var pages = collection.find(WikipediaPage.class);

        try {
            Analyzer analyzer = new StandardAnalyzer();//initialize analyzer
            Path indexPath = Files.createTempDirectory("tempIndex");//get path to index
            Directory directory = FSDirectory.open(indexPath);//
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter iwriter = new IndexWriter(directory, config);

            for (var page : pages) {
                Document doc = new Document();
                doc.add(new Field("Title", page.title, TextField.TYPE_STORED));
                doc.add(new Field("Url", page.url, TextField.TYPE_STORED));
                // Store the last modified date (created using these docs: https://lucene.apache.org/core/3_0_3/api/core/org/apache/lucene/document/NumericField.html)

                //Convert the localDateTime to a simple date
                var modifiedDateTime = page.lastModifiedDate;
                // convert it to a Date type
                Date lastModifiedDate = java.util.Date.from(modifiedDateTime.atZone(ZoneId.systemDefault()).toInstant());

                doc.add(new Field("Last Modified", DateTools.dateToString(lastModifiedDate, DateTools.Resolution.DAY),TextField.TYPE_STORED));
                if (page.coordinates != null)
                {
                    doc.add(new Field("Latitude", page.coordinates.latitude, TextField.TYPE_STORED));
                    doc.add(new Field("Longitude", page.coordinates.longitude, TextField.TYPE_STORED));
                }
                doc.add(new IntPoint("Number of References", page.numberOfReferences));
                for (String subheader : page.subHeaders) {
                    doc.add(new Field("Subheaders", subheader, TextField.TYPE_STORED));
                }
                for (String category : page.categories) {
                    doc.add(new Field("Categories", category, TextField.TYPE_STORED));
                }
                for (String link : page.links) {
                    doc.add(new Field("Links", link, TextField.TYPE_STORED));
                }
                iwriter.addDocument(doc);
                 log.info("Inserted " + page.title + " page into Lucene");
            }
            iwriter.close();
            //new test

        } catch (IOException e) {
            e.printStackTrace();
             log.error("Error creating index");
        }

    }

    List<WikipediaPage> getTopResults() {
        return getTopResults(100);
    }

    List<WikipediaPage> getTopResults(int limit) {
        var collection = mongoClient.getDatabase("Wikipedia").getCollection("Articles");
        var pages = collection.find(WikipediaPage.class).limit(limit).into(new ArrayList<WikipediaPage>());

        return pages;
    }

    public List<WikipediaPage> getTopGeoResults(int limit) {
        var collection = mongoClient.getDatabase("Wikipedia").getCollection("Articles");
        var pages = collection.find(ne("Coordinates", null), WikipediaPage.class).limit(limit).into(new ArrayList<WikipediaPage>());

        return pages;
    }
}
