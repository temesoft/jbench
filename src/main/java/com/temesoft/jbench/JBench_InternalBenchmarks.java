package com.temesoft.jbench;

/**
 * Empty as a benchmark of the actual benchmark execution runner itself
 */
public final class JBench_InternalBenchmarks {
    @JBench(maxIterations = 100000000)
    public final void empty() { /* keep empty and public */ }

    @JBench
    public final void sleepOneSec() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
