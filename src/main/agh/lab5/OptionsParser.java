package main.agh.lab5;


public class OptionsParser{
    final int threadsCount;
    final int imageWidth;
    final int imageHeight;
    final int sectionCount;

    final String title;

    public OptionsParser(){
        threadsCount = 4;
        imageWidth = 800;
        imageHeight = 600;
        title = "Mandelbrot Set";
        sectionCount = threadsCount * 10;
    }

    public OptionsParser(int threadsCount, int imageWidth, int imageHeight, int sectionCount){
        this.threadsCount = threadsCount;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        title = "Mandelbrot Set";
        this.sectionCount = sectionCount;
    }

    public OptionsParser(String []args){
        this();
    }

    public int sectionHeight(){
        return imageHeight / sectionCount;
    }
}
