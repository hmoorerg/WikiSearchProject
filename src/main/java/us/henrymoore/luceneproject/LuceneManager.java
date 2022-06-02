package us.henrymoore.luceneproject;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
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
        for (var page : pages) {
            log.info("Inserting " + page.title + " page into Lucene");
        }

    }

    List<WikipediaPage> getTopResults(){
        return getTopResults(100);
    }
    List<WikipediaPage> getTopResults(int limit){
        var collection = mongoClient.getDatabase("Wikipedia").getCollection("Articles");
        var pages = collection.find(WikipediaPage.class).limit(limit).into(new ArrayList<WikipediaPage>());

        return pages;
    }

    public List<WikipediaPage> getTopGeoResults(int i) {
        var collection = mongoClient.getDatabase("Wikipedia").getCollection("Articles");
        var pages = collection.find(ne("Coordinates", null),WikipediaPage.class).limit(limit).into(new ArrayList<WikipediaPage>());

        return pages;
    }
}
