package it.redhat.granulate.bm;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;

@Path("/users")
@Singleton
public class UserService {

    TimeAggregator createTimer = new TimeAggregator();
    TimeAggregator deleteTimer = new TimeAggregator();
    TimeAggregator getTimer = new TimeAggregator();
    TimeAggregator updateTimer = new TimeAggregator();

    private int first_id = 1;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<User>> list() {
        double time = System.currentTimeMillis();
        Uni<List<User>> res = User.listAll();

        return res.onItem().invoke(item -> getTimer.put(System.currentTimeMillis()-time));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Uni<User> get(int id) {
        return User.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(User user) {
        double time = System.currentTimeMillis();

        Uni<User> result = Panache.withTransaction(user::persist)
        .replaceWith(user)
        .ifNoItem()
        .after(Duration.ofMillis(10000))
        .fail()
        .onFailure()
        .transform(t -> new IllegalStateException(t));

        return result
        .onItem().invoke(item -> createTimer.put(System.currentTimeMillis()-time))
        .onItem().transform(u -> URI.create("/users" + u.getId()))
        .onItem().transform(uri -> Response.created(uri))
        .onItem().transform(Response.ResponseBuilder::build);
    }

    public Uni<User> findUserById(int id) {
        return User.findById(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> update(User user) {

        int id = first_id;

        double time = System.currentTimeMillis();

        Uni<Integer> result = Panache.withTransaction(() -> 
                            User.update("username=?1, password=?2, email=?3 where id=?4",
                                user.getUsername(), user.getPassword(), user.getEmail(), id)
                        )
                        .onFailure().recoverWithNull();
        

        return result.onItem().invoke(item -> updateTimer.put(System.currentTimeMillis()-time))
        .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
        .onItem().ifNull().continueWith(Response.ok().status(Response.Status.NOT_FOUND)::build);

    }

    @DELETE
    public Uni<Response> delete() {
        int id = first_id;
        first_id += 1;

        double time = System.currentTimeMillis();

        Uni<Boolean> result = Panache.withTransaction(() -> User.deleteById(id));


        return result.onItem().invoke(item -> deleteTimer.put(System.currentTimeMillis()-time))
        .onItem().transform(entity -> 
            !entity 
                ? Response.serverError().status(Response.Status.NOT_FOUND).build() 
                : Response.ok().status(Response.Status.OK).build());
    }

    @GET
    @Path("/times")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Map<String, TimeAggregator.TimeStats>> getTimes() {

        Uni<Map<String, TimeAggregator.TimeStats>> res = Uni.createFrom()
                    .item(new HashMap<String, TimeAggregator.TimeStats>())
                    .onItem().transform(item -> {
                        item.put("Create", createTimer.getStats());
                        item.put("Update", updateTimer.getStats());
                        item.put("Get", getTimer.getStats());
                        item.put("Delete", deleteTimer.getStats());

                        return item;
                    });

        return res;
    }
}