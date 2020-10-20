package main.agh.lab2;


public class Main {
    public static void main(String []args){
        Counter syncObj = new Counter();
        Thread thread1 = new Thread(new MyThread(syncObj, 0));
        Thread thread2 = new Thread(new MyThread(syncObj, 1));
        Thread thread3 = new Thread(new MyThread(syncObj, 2));
        thread1.start();
        thread2.start();
        thread3.start();
    }
}

class MyThread implements Runnable {
    private Counter cond;
    private int my_number;

    public MyThread(Counter cond, int number){
        this.cond = cond;
        this.my_number = number;
    }

    @Override
    public void run() {
        int i = 10;
        synchronized(this.cond){
            while(i-- > 0) {
                try {
                    while (this.cond.getValue() % 3 != this.my_number) {
                        this.cond.wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getStackTrace());
                }
                System.out.println(this.my_number + 1);
                this.cond.inc();
                this.cond.notifyAll();
            }
        }
    }
}

class Counter {
    private int state;

    public Counter(){
        this.state = 0;
    }

    public Counter(int start){
        this.state = start;
    }

    public void inc(){
        this.state++;
    }

    public int getValue(){
        return this.state;
    }
}
