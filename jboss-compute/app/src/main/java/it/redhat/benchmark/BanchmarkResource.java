package it.redhat.benchmark;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.logging.Logger;

import javax.inject.Inject;

@ApplicationScoped
@Path("/benchmarks")
public class BanchmarkResource {

    private static final Logger LOG = Logger.getLogger(BanchmarkResource.class);

    @Inject
    MatrixResource matrixResource;

    @PostConstruct
    void init() {
        this.matrixResource.initFloatMatrix();
    }
    

    @POST
    @Path("/float")
    public Response executeFloatBenchmark() {

        matrixResource.correlateFloatMatrix();
        LOG.info("Float Benchmark completed"); 

        return Response.ok("Done").build();
    }

    @GET
    @Path("/float")
    @Produces(MediaType.APPLICATION_JSON)
    public TimeStats getFloatTimeStats() {
        return matrixResource.getFloatMatrixTimeStats();
    }
}
