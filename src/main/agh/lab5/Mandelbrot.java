package main.agh.lab5;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
        runRender();
    }

    private void runRender(){
        ExecutorService executor = Executors.newFixedThreadPool(options.threadsCount);
        ArrayList<Future<Void>> futures = new ArrayList<>();
        for(int i = 0; i < options.sectionCount; i++){
            Future<Void> future = executor.submit(new ImageSectionRenderer(wholeBufferedImage, options.imageWidth, options.sectionHeight(), i));
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
    private static final int MAX_ITER = 570;
    private static final double ZOOM = 150;
    BufferedImage bufferedImage;
    int width, height, section;

    public ImageSectionRenderer(BufferedImage bufferedImage, int width, int sectionHeight, int section){
        this.bufferedImage = bufferedImage;
        this.width = width;
        this.height = sectionHeight;
        this.section = section;
    }

    private void imageSectionRender(){
        double zx, zy, cX, cY, tmp;
        for (int y = section * height; y < (section + 1) * height; y++) {
            for (int x = 0; x < width; x++) {
                zx = zy = 0;
                cX = (x - 400) / ZOOM;
                cY = (y - 300) / ZOOM;
                int iter = MAX_ITER;
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
