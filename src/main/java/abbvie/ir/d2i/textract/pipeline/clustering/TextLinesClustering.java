package abbvie.ir.d2i.textract.pipeline.clustering;

import abbvie.ir.d2i.textract.model.TextLine;
import abbvie.ir.d2i.textract.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;


@Component
public class TextLinesClustering {

    @Value("${clustering.distance_tolerance}")
    private double distance_tolerance;

    @Value("${clustering.left_tolerance}")
    private double left_tolerance;

    @Value("${clustering.width_tolerance}")
    private double width_tolerance_value;

    @Value("${clustering.distance_method}")
    private String distance_method;

    public List<List<TextLine>> getCluster(List<TextLine> textLines) {

        List<List<TextLine>> blocks = new ArrayList<>();


        while (!textLines.isEmpty()) {

            List<TextLine> block = new ArrayList<TextLine>();
            TextLine target_line = textLines.get(0);
            double same_left = target_line.left;


            Iterator<TextLine> iterator = textLines.iterator();

            while (iterator.hasNext()) {
                TextLine currentTextLine = iterator.next();

                double width_tolerance = Math.max(width_tolerance_value, Math.abs(target_line.width - currentTextLine.width) / 2);


                 double[] centerCoordinatesLastTextLineInABlock = new double[2];
                 double[] centerCoordinatesCurrentTextLine = new double[2];;
                 double distance = 0;

                if(!block.isEmpty()) {

                    switch(distance_method){
                        case "TOP2TOP":
                            distance =  currentTextLine.top - block.get(block.size() - 1).top;
                            break;
                        case "CENTER2CENTER":

                            centerCoordinatesLastTextLineInABlock = Utils.calculate_center(block.get(block.size() - 1).top, block.get(block.size() - 1).left, block.get(block.size() - 1).width, block.get(block.size() - 1).height);
                            centerCoordinatesCurrentTextLine = Utils.calculate_center(currentTextLine.top, currentTextLine.left, currentTextLine.width, currentTextLine.height);

                            distance = Utils.distance(
                                    centerCoordinatesLastTextLineInABlock[0],
                                    centerCoordinatesLastTextLineInABlock[1],
                                    centerCoordinatesCurrentTextLine[0],
                                    centerCoordinatesCurrentTextLine[1]
                            );
                            break;
                        default:
                            distance =  currentTextLine.top - block.get(block.size() - 1).top;
                            break;
                    }
                }


                final boolean b = Utils.pretty_similar(same_left, currentTextLine.left, left_tolerance);

                final boolean b1 = Utils.pretty_similar(target_line.width, currentTextLine.width, width_tolerance);

                final boolean b2 = distance < distance_tolerance;

                if (b&&b2) {
                    block.add(currentTextLine);
                    iterator.remove();
                }
            }
            blocks.add(block);
        }
        return blocks;
    }



    public void saveCluster(List<List<TextLine>> textLines, String path) {

        try{
           // FileOutputStream fos= new FileOutputStream(path);
            //ObjectOutputStream oos= new ObjectOutputStream(fos);


            File yourFile = new File(path);
            yourFile.createNewFile(); // if file already exists will do nothing
            FileOutputStream fos = new FileOutputStream(yourFile, true);
            OutputStreamWriter oos = new OutputStreamWriter(fos);

            int block_number=1;

            oos.write("New Page");
            oos.write("\r\n");
            oos.write("\r\n");

            for (List<TextLine> block:textLines) {
                oos.write("\r\n");
                oos.write("\r\n");
                oos.write("\r\n");
                oos.write("Block Number: "+block_number);
                oos.write("\r\n");

                for (TextLine textline:block) {
                    oos.write(textline.text);
                    oos.write("\r\n");
                }
                block_number++;
            }
            oos.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }


    }

}
