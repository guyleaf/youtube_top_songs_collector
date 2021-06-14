package org.guyleaf.youtube.Task;

public class Test implements Runnable{
    private int counter = 0;

    @Override
    public void run() {
        if (counter % 2 == 1) {
            Thread.currentThread().interrupt();
            counter++;
        }
        System.out.println(counter);
        counter++;
    }
}
