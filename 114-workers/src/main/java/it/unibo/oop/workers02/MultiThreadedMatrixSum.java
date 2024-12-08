package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("CPD-START")
public class MultiThreadedMatrixSum implements SumMatrix {
    
    private final int nthread;

    /**
     * 
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedMatrixSum(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private int startpos_x;
        private int startpos_y;
        private final int nelem;
        private long res;
        
            /**
             * Build a new worker.
             * 
             * @param list
             *            the list to sum
             * @param startpos_x
             *            the initial x position for this worker
             * @param startpos_x
             *            the initial y position for this worker
             * @param nelem
             *            the no. of elems to sum up for this worker
             */
            Worker(final double[][] matrix, final int startpos_x, int startpos_y, final int nelem) {
                super();
                this.matrix = matrix;
                this.startpos_x = startpos_x;
                this.startpos_y = startpos_y;
                this.nelem = nelem;
            }
    
            @Override
            @SuppressWarnings("PMD.SystemPrintln")
            public void run() {
                System.out.println("Working from element " + (nelem * startpos_x + startpos_y + 1) + "to element" + (nelem * startpos_x + startpos_y + 1 + nelem));
                for (int i = 0; i < this.nelem; i++) {
                    if (startpos_y == matrix[0].length){
                        startpos_x++;
                        startpos_y = 0;
                    }
                this.res += this.matrix[startpos_x][startpos_y];
                startpos_y++;
                }
            }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public long getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(final double[][] matrix) {
        final int nlines = matrix.length;
        final int ncolums = matrix[0].length;
        final int matrixSize = nlines * ncolums;
        final int size = matrixSize / nthread;
        System.out.println(size);
        /*
         * Build a stream of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrixSize; start = start + size) {
            workers.add(new Worker(matrix, start % ncolums, start / ncolums, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

}
