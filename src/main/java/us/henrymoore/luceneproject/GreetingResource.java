package us.henrymoore.luceneproject;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Path("/hello")
public class GreetingResource {

    @Inject LuceneManager luceneManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        log.info("Called the hello endpoint");
        return "Hello RESTEasy (using the new library)";
    }

    @Path("people")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPeople(){
        luceneManager.getTopResults();
        return new ArrayList<Person>(){{
            add(Person.builder()
                    .name("Guy")
                    .build());
        }};
    }

    @Path("Debug")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WikipediaPage> getTopPages(){
        return luceneManager.getTopResults(10);
    }

    @Path("Debug")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WikipediaPage> getTopGeoPages(){
        return luceneManager.getTopGeoResults(10);
    }

}