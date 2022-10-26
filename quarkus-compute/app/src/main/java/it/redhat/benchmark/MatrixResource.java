package it.redhat.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import it.redhat.benchmark.matrix.FloatMatrix;

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

    public long correlateFloatMatrix() {
        long startTime = System.currentTimeMillis();
        this.floatMatrix.computeCorrelationMatrix();
        long time = (System.currentTimeMillis() - startTime);
        double timeInSecs = new Long(time).doubleValue()/1000;

        this.floatMatrixTimes.add(timeInSecs);
        LOG.info("Float Matrix benchmark completed with time " + timeInSecs);
        return time;
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
