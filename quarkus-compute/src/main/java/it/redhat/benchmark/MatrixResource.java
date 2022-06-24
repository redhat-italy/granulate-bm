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


    private FloatMatrix FloatMatrix;
    private List<Double> floatMatrixTimes;

    public void initFloatMatrix(int rows, int cols) {
        this.FloatMatrix = new FloatMatrix(rows, cols);
        this.FloatMatrix.initRandomData();

        this.floatMatrixTimes = new ArrayList<>();

        LOG.info("Float Matrix " + rows + "x" + cols + " initialized with random values");
    }

    public void correlateFloatMatrix() {
        double startTime = System.currentTimeMillis();
        this.FloatMatrix.computeCorrelationMatrix();
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
