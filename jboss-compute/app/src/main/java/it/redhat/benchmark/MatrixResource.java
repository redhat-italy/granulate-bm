package it.redhat.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.resteasy.logging.Logger;

import it.redhat.benchmark.matrix.FloatMatrix;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatrixResource {

    private static final Logger LOG = Logger.getLogger(MatrixResource.class);


    private FloatMatrix floatMatrix;
    private List<Double> floatMatrixTimes;

    public void initFloatMatrix() {
        this.floatMatrix = new FloatMatrix();
        this.floatMatrix.initRandomData();

        this.floatMatrixTimes = new ArrayList<>();

        LOG.info("Float Matrix " + FloatMatrix.N_ROWS + "x" + FloatMatrix.N_COLS + " initialized with random values");
    }

    public void correlateFloatMatrix() {
        double startTime = System.currentTimeMillis();
        this.floatMatrix.computeCorrelationMatrix();
        double time = (System.currentTimeMillis() - startTime)/1000;

        this.floatMatrixTimes.add(time);
        LOG.info("Float Matrix benchmark completed with time " + time);
    }

    public TimeStats getFloatMatrixTimeStats() {
        TimeStats res = new TimeStats();

        if(this.floatMatrixTimes == null || this.floatMatrixTimes.size() == 0) {
            res.avg=0;
            res.min=0;
            res.max=0;
        } else {
            res.min = Collections.min(this.floatMatrixTimes);
            res.max = Collections.max(this.floatMatrixTimes);
            res.avg = this.floatMatrixTimes.stream()
                        .reduce(0.0, (x, y) -> x+y) 
                        / this.floatMatrixTimes.size();
        }
    
        return res;
    }
}
