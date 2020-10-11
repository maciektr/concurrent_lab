package com.company;

public class ExampleThread extends Thread{
    private Counter counter;
    private boolean up;

    public ExampleThread(Counter c, boolean up) {
        this.counter = c;
        this.up = up;
    }

    @Override
    public void run() {
        for(int i = 0; i < 1e9; i++)
            if(this.up)
                this.counter.inc();
            else
                this.counter.dec();

    }

    public static void main(String[] args) {
        Counter c = new Counter();
        ExampleThread m1 = new ExampleThread(c, true);
        ExampleThread m2 = new ExampleThread(c, false);
        Thread thread1 = new Thread(m1);
        Thread thread2 = new Thread(m2);
        thread1.start();
        thread2.start();
        System.out.println(c.getState());
    }
}

class Counter {
    private int state;

    public Counter(){
        this.state = 0;
    }

    public void inc(){
        this.state++;
    }

    public void dec(){
        this.state--;
    }

    public int getState(){
        return this.state;
    }
}
