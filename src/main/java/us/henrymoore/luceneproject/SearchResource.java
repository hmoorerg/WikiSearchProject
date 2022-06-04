package us.henrymoore.luceneproject;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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

    @Path("TopPages/{query}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getTopPages(@PathParam("query") String query, @QueryParam("count") @DefaultValue("10") int count){
        log.info("Got query : "+query);
        return luceneManager.getTopResults(query, count);
    }

    // Disable until the LuceneManager part of this code is finished
//    @Path("TopGeoPages")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<String> getTopGeoPages(String query){
//        log.info("Got query : "+query);
//        return luceneManager.getTopGeoResults(query);
//    }

}