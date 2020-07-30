package abbvie.ir.d2i.textract.utils;

import abbvie.ir.d2i.textract.Application;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {

    public static String getHomeDirectory() {
        final File dir = new ApplicationHome(Application.class).getDir();
        return dir.getAbsolutePath();
    }

    public static void writeToFile(String content, String path) throws IOException {
        Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.APPEND);
    }

    public static boolean pretty_similar(double x1, double x2, double tolerance) {
        double distance =  Math.abs(x1-x2);
        return distance < tolerance;
    }

    public static double distance(double x, double y, double x1, double y1) {
        double dx =  Math.max(Math.max(x1-x,x-x1),0);
        double dy =  Math.max(Math.max(y1-y,y-y1),0);
        return Math.sqrt(dx*dx + dy*dy);
    }


    public static double[] calculate_center(double top, double left, double width, double height) {
        double x =  left;
        double y =  top;

        double x1 =  left+width;
        double y1 =  top+height;

        double xCenter = (x+x1)/2;
        double yCenter = (y+y1)/2;

        double[] center = new double[2];
        center[0]=xCenter;
        center[1]=yCenter;

        return center;
    }



}
