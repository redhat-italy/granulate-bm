package it.redhat.granulate.bm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Singleton
public class UserService {

    private int first_id = 1;

    TimeAggregator createTimer = new TimeAggregator();
    TimeAggregator deleteTimer = new TimeAggregator();
    TimeAggregator getTimer = new TimeAggregator();
    TimeAggregator updateTimer = new TimeAggregator();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> list() {
        double time = System.currentTimeMillis();
        List<User> res = User.listAll();
        getTimer.put(System.currentTimeMillis()-time);

        return res;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public User get(@PathParam("id") int id) {
        return User.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(User user) {
        double time = System.currentTimeMillis();
        user.persist();
        createTimer.put(System.currentTimeMillis()-time);

        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(User user) {
        int id = first_id;

        double time = System.currentTimeMillis();

        User u = User.findById(id);

        if(u==null)
            return Response.status(Response.Status.NOT_FOUND).build();

        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        u.setUsername(user.getUsername());

        u.persist();

        updateTimer.put(System.currentTimeMillis()-time);

        return Response.ok().build();

    }

    @DELETE
    @Transactional
    public Response delete() {

        int id = first_id;
        first_id += 1;

        double time = System.currentTimeMillis();

        User user = User.findById(id);

        if(user==null)
            return Response.status(Response.Status.NOT_FOUND).build();

        user.delete();

        deleteTimer.put(System.currentTimeMillis()-time);

        return Response.ok().build();
    }

    @GET
    @Path("/times")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, TimeAggregator.TimeStats> getTimes() {
        Map<String, TimeAggregator.TimeStats> res = new HashMap<>();

        res.put("Create", createTimer.getStats());
        res.put("Update", updateTimer.getStats());
        res.put("Get", getTimer.getStats());
        res.put("Delete", deleteTimer.getStats());

        return res;
    }
}