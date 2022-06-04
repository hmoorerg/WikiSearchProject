package us.henrymoore.luceneproject;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Path("/search")
public class SearchResource {

    @Inject LuceneManager luceneManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        log.info("Called the hello endpoint");
        return "Hello RESTEasy (using the new library)";
    }

    @Path("TopPages/{query}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getTopPages(@PathParam("query") String query){
        log.info("Got query : "+query);
        return luceneManager.getTopResults(query);
    }

    @Path("TopGeoPages")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getTopGeoPages(String query){
        log.info("Got query : "+query);
        return luceneManager.getTopGeoResults(query);
    }

}