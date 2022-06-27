package it.redhat.benchmark.matrix;

import java.util.Random;

public class FloatMatrix {
    private final float[] data;
    private final int rows;
    private final int columns;

    public static final int N_ROWS = 1000;
    public static final int N_COLS = 3000;

    public FloatMatrix() {
        this(N_ROWS, N_COLS);
    }

    public FloatMatrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.data = new float[rows*columns];
    }

    public void initRandomData() {
        Random r = new Random();

        for(int row=0; row<rows; row++) {
            for(int col=0; col<columns; col++) {
                float value = (float) r.nextGaussian();
                this.set(row, col, value);
            }
        }
    }

    public void set(int row, int col, float value) {
        this.data[row*this.columns + col] = value;
    }

    public float get(int row, int col) {
        return this.data[row*this.columns + col];
    }

    private float correlation(int i, int j) {
        float res = 0;
        for(int k=0; k<this.columns; k++) {
            res += this.get(i, k)*this.get(j, k);
        }

        return res;
    }

    public void computeCorrelationMatrix() {
        FloatMatrix out = new FloatMatrix(this.rows, this.rows);

        for(int i=0; i<this.rows; i++) {
            for(int j=0; j<this.rows; j++) {
                out.set(i, j, this.correlation(i, j));
            }
        }
    }
}
