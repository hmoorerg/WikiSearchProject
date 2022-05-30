package us.henrymoore.luceneproject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy (using the new library)";
    }

    @Path("people")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPeople(){
        return new ArrayList<Person>(){{
            add(Person.builder()
                    .name("Guy")
                    .build());
        }};
    }
}