package it.redhat.benchmark;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Path("/benchmark")
public class BanchmarkResource {

    private static final Logger LOG = Logger.getLogger(BanchmarkResource.class);

    @Inject
    MatrixResource matrixResource;
    @Inject
    MeterRegistry registry;

    @PostConstruct
    void init() {
        this.matrixResource.initFloatMatrix();
    }
    
    @POST
    @Path("/exec")
    public String execute() {
        long timeInMillis = matrixResource.correlateFloatMatrix();
        registry.timer("correlation_timer", Tags.of("name", "timer")).record(Duration.ofMillis(timeInMillis));
        return new String("{\"exec\": \"success\", \"time\": " + timeInMillis + "}");
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
