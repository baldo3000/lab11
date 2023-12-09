package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a classic implementation.
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * @param nthreads
     *                 the number of threads
     */
    public MultiThreadedSumMatrix(final int nthreads) {
        this.nthread = nthreads;
    }

    private static class Worker extends Thread {
        private final double[] array;
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param array
         *                 the array to sum
         * @param startpos
         *                 the initial position for this worker
         * @param nelem
         *                 the no. of elems to sum up for this worker
         */
        Worker(final double[] array, final int startpos, final int nelem) {
            super();
            this.array = array; // NOPMD
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < this.array.length && i < startpos + nelem; i++) {
                this.res += this.array[i];
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }

    }

    /**
     * @param matrix
     *               the matrix to sum
     */
    @Override
    public double sum(final double[][] matrix) {
        final int nrows = matrix.length;
        final int ncols = matrix[0].length;
        final double[] numbers = new double[ncols * nrows];
        int pos = 0;
        for (final var row : matrix) {
            for (final var elem : row) {
                numbers[pos] = elem;
                pos++;
            }
        }
        final int size = numbers.length % this.nthread + numbers.length / this.nthread;
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < numbers.length; start += size) {
            workers.add(new Worker(numbers, start, size));
        }
        for (final Worker w : workers) {
            w.start();
        }
        long sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
}
