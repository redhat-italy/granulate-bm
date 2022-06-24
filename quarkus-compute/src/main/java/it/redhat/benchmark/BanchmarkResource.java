package it.redhat.benchmark;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

@Path("/benchmark")
public class BanchmarkResource {

    private static final Logger LOG = Logger.getLogger(BanchmarkResource.class);

    @Inject
    Vertx vertx;

    @Inject
    MatrixResource matrixResource;

    WorkerExecutor executor;

    @PostConstruct
    void init() {
        this.executor = vertx.createSharedWorkerExecutor("workers", 10, 60000, TimeUnit.MILLISECONDS);
    }
    

    @POST
    @Path("/float")
    public Response executeFloatBenchmark() {
        this.executor.<String>executeBlocking(promise -> {
            matrixResource.correlateFloatMatrix();
            promise.complete("Float Benchmark completed");
        }, asyncResult -> {
            LOG.info(asyncResult.result()); 
        });

        LOG.info("Float Benchmark scheduled");

        return Response.ok("Scheduled").build();
    }

    @POST
    @Path("/float/{rows}/{cols}")
    public Response initFloatMatrix(@PathParam int rows, @PathParam int cols) {
        this.executor.<String>executeBlocking(promise -> {
            matrixResource.initFloatMatrix(rows, cols);
            promise.complete("Float Matrix initialized");
        }, asyncResult -> {
            LOG.info(asyncResult.result()); 
        });

        return Response.ok("Scheduled").build();
    }

    @GET
    @Path("/float")
    @Produces(MediaType.APPLICATION_JSON)
    public TimeStats getFloatTimeStats() {
        return matrixResource.getFloatMatrixTimeStats();
    }
}
