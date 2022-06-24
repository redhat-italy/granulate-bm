package it.redhat.granulate.bm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TimeAggregator {

    public static class TimeStats {
        public double min;
        public double max;
        public double median;
        public double mean;
        public double _10_percentile;
        public double _25_percentile;
        public double _75_percentile;
        public double _90_percentile;
    }

    private Deque<Double> times = new ConcurrentLinkedDeque<>();
    public static final int WINDOW = 10000;
    
    public void put(double time) {
        if (times.size() >= WINDOW)
            times.pollFirst();
        times.addLast(time);
    }

    public TimeStats getStats() {
        TimeStats res = new TimeStats();

        if(times.size() > 0) {
            ArrayList<Double> sortedTimes = new ArrayList<>(times);
            Collections.sort(sortedTimes);
    
            res.min = sortedTimes.get(0);
            res.max = sortedTimes.get(sortedTimes.size()-1);
            res.mean = sortedTimes.stream().reduce(0.0, (a,b) -> a+b)/times.size();
    
            res._10_percentile = percentile(10, sortedTimes);
            res._25_percentile = percentile(25, sortedTimes);
            res.median = percentile(50, sortedTimes);
            res._75_percentile = percentile(75, sortedTimes);
            res._90_percentile = percentile(90, sortedTimes);        
        }

        return res;
    }

    private double percentile(int p, List<Double> data) {
        if(p<0 || p>100 || times.isEmpty()){
            return 0;
        }
        
        return data.get((int) Math.floor(p/100.0*(data.size()-1)));
    }
}
