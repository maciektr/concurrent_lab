package main.agh.lab5;


public class Main {
    public static void main(String []args) throws InterruptedException {
        OptionsParser options = new OptionsParser(args);
        Mandelbrot m = new Mandelbrot(options);
        m.setVisible(true);
    }
}