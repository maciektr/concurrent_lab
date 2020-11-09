package main.agh.lab5;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.*;
import javax.swing.JFrame;


public class Mandelbrot extends JFrame {
    private final OptionsParser options;
    private final BufferedImage wholeBufferedImage;

    public Mandelbrot(OptionsParser options) {
        super(options.title);
        this.options = options;
        setBounds(100, 100, options.imageWidth, options.imageHeight);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        wholeBufferedImage = new BufferedImage(options.imageWidth, options.imageHeight, BufferedImage.TYPE_INT_RGB);

        long startTime = new Date().getTime();
        runRender();
        long endTime = new Date().getTime();
        System.out.println("Render time: " + (endTime - startTime) + " (milliseconds)");
    }

    private void runRender(){
        ExecutorService executor = Executors.newFixedThreadPool(options.threadsCount);
        ArrayList<Future<Void>> futures = new ArrayList<>();
        for(int i = 0; i < options.sectionCount; i++){
            Future<Void> future = executor.submit(new ImageSectionRenderer(wholeBufferedImage, i, options));
            futures.add(future);
        }
        try {
            for(Future<Void> f : futures)
                f.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.drawImage(wholeBufferedImage, 0, 0, this);
    }
}


class ImageSectionRenderer implements Callable<Void>{
    BufferedImage bufferedImage;
    private final int maxIterations;
    private final int section;
    private final double zoom;
    private final int height;
    private final int width;
    private final OptionsParser options;

    public ImageSectionRenderer(BufferedImage bufferedImage, int section, OptionsParser options){
        this.bufferedImage = bufferedImage;
        this.width = options.imageWidth;
        this.height = options.sectionHeight();
        this.section = section;
        this.maxIterations = options.maxIterations;
        this.zoom = options.zoom;
        this.options = options;
    }

    private void imageSectionRender(){
        double zx, zy, cX, cY, tmp;
        for (int y = section * height; y < (section + 1) * height; y++) {
            for (int x = 0; x < width; x++) {
                zx = zy = 0;
                cX = (x - options.paddingLeft) / zoom;
                cY = (y - options.paddingTop) / zoom;
                int iter = maxIterations;
                while (zx * zx + zy * zy < 4 && iter > 0) {
                    tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    iter--;
                }
                bufferedImage.setRGB(x, y, iter | (iter << 8));
            }
        }
    }

    @Override
    public Void call() {
        imageSectionRender();
        return null;
    }
}
