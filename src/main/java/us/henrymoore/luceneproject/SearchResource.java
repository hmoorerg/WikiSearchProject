package us.henrymoore.luceneproject;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Path("/search")
public class SearchResource {

    @Inject LuceneManager luceneManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "This is the main search url, please use a more specific URL for the type of search that you want to make";
    }

    @Path("TopPages")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SearchResult> getTopPages(@QueryParam("query") String query, @QueryParam("count") @DefaultValue("10") int count){
        log.info("Got query : "+query);

        Comparator<SearchResult> compareByScore = new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return Float.compare(o1.score,o2.score);
            };
        };

        var output = luceneManager.getTopResults(query, count);
        output.sort(compareByScore);
        return output;
    }

}