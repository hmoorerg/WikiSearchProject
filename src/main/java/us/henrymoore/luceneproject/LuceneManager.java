package us.henrymoore.luceneproject;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import static com.mongodb.client.model.Filters.ne;

@Slf4j
@ApplicationScoped
public class LuceneManager {

    @Inject
    MongoClient mongoClient;


    // Lucene main variables
    private Directory _directory;
    private Analyzer analyzer = new StandardAnalyzer();//initialize analyzer

    private MongoCollection<org.bson.Document> getCollection() {
        return mongoClient.getDatabase("Wikipedia").getCollection("Articles");
    }

    @PostConstruct
    void Initialize() {
         log.info("Inserting records from MongoDB");
        String a = "";
        var collection = getCollection();
        var pages = collection.find(WikipediaPage.class);

        try {

            Path indexPath = Files.createTempDirectory("tempIndex");//get path to index
            _directory = FSDirectory.open(indexPath);//
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter iwriter = new IndexWriter(_directory, config);

            for (var page : pages) {
                Document doc = new Document();
                doc.add(new Field("Id", page.Id.toString(), TextField.TYPE_STORED));
                doc.add(new Field("Title", page.title, TextField.TYPE_STORED));
                doc.add(new Field("Url", page.url, TextField.TYPE_STORED));
                // Store the last modified date (created using these docs: https://lucene.apache.org/core/3_0_3/api/core/org/apache/lucene/document/NumericField.html)

                //Convert the localDateTime to a simple date
                var modifiedDateTime = page.lastModifiedDate;
                // convert it to a Date type
                Date lastModifiedDate = java.util.Date.from(modifiedDateTime.atZone(ZoneId.systemDefault()).toInstant());
                String lastModifiedDateString =  DateTools.dateToString(lastModifiedDate, DateTools.Resolution.DAY);

                doc.add(new Field("Last Modified",lastModifiedDateString,TextField.TYPE_STORED));
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
            }
            iwriter.close();
            //new test
            log.info("Finished inserting values into Lucene from MongoDB");

        } catch (IOException e) {
            e.printStackTrace();
             log.error("Error creating index");
        }

    }

    List<SearchResult> getTopResults(String query) {
        return getTopResults(query,10);
    }

    List<SearchResult> getTopResults(String queryString, int limit) {



        var pages = new ArrayList<SearchResult>();

        // End early if the query string is empty
        if (queryString.isEmpty()) return pages;

        // Adapt this to search more fields later
        try {
            QueryParser parser = new QueryParser("Title", analyzer);
            Query query = parser.parse(queryString);

            IndexReader indexReader = DirectoryReader.open(_directory);
            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs topDocs = searcher.search(query, limit);
            for (var doc : topDocs.scoreDocs) {
                Document document = searcher.doc(doc.doc);

                var collection = getCollection();
                var wikiPage = collection.find(Filters.eq("_id", new ObjectId(document.get("Id"))), WikipediaPage.class).first();

                var result = new SearchResult();
                result.page = wikiPage;
                result.score = doc.score;
                pages.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pages;
    }

    public List<String> getTopGeoResults(String query) { return getTopGeoResults(query,10); }

    public List<String> getTopGeoResults(String query, int limit) {
        var pages = new ArrayList<String>();
        pages.add("UNIMPLEMENTED");

        // Adapt this to search more fields later
//
//        try {
//            Query queryObj = new QueryParser("Title", analyzer).parse(query);
//            IndexReader indexReader = DirectoryReader.open(_directory);
//            IndexSearcher searcher = new IndexSearcher(indexReader);
//            TopDocs topDocs = searcher.search(queryObj, limit);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        return pages;
    }
}
