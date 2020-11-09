package main.agh.lab5;


public class OptionsParser{
    final int threadsCount;
    final int imageWidth;
    final int imageHeight;
    final int sectionCount;
    final int maxIterations;
    final double zoom;
    final int paddingLeft;
    final int paddingTop;

    final String title;

    public OptionsParser(){
        threadsCount = 8;
        imageWidth = 850;
        imageHeight = 800;
        title = "Mandelbrot Set";
        sectionCount = threadsCount * 10;
        maxIterations = 600;
        paddingLeft = 570;
        paddingTop = 420;
        zoom = 280;
    }

    public OptionsParser(String []args){
        this();
    }

    public int sectionHeight(){
        return imageHeight / sectionCount;
    }
}
