package it.redhat.benchmark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    public static int QUERY_SIZE = 10000;
    private int first_id = 1;

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    TimeAggregator createTimer = new TimeAggregator();
    TimeAggregator deleteTimer = new TimeAggregator();
    TimeAggregator getTimer = new TimeAggregator();
    TimeAggregator updateTimer = new TimeAggregator();

    @GET
    public List<User> list() {
        double time = System.currentTimeMillis();
        List<User> res = em.createNamedQuery("getAllUsers", User.class).getResultList();
        getTimer.put(System.currentTimeMillis()-time);

        return res;
    }

    @GET
    @Path("/{id}")
    public User get(int id) {
        return em.find(User.class, id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(User user) {
        double time = System.currentTimeMillis();
        em.persist(user);
        createTimer.put(System.currentTimeMillis()-time);

        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(User user) {

        int id = first_id;

        double time = System.currentTimeMillis();

        User u = em.find(User.class, id);

        if(u==null)
            return Response.status(Response.Status.NOT_FOUND).build();

        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        u.setUsername(user.getUsername());

        em.persist(user);

        updateTimer.put(System.currentTimeMillis()-time);

        return Response.ok().build();

    }

    @DELETE
    @Transactional
    public Response delete() {

        int id = first_id;
        first_id += 1;

        double time = System.currentTimeMillis();

        User user = em.find(User.class, id);

        if(user==null)
            return Response.status(Response.Status.NOT_FOUND).build();

        em.remove(em.contains(user) ? user : em.merge(user));

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