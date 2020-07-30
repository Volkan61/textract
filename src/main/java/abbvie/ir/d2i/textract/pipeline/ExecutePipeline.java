package abbvie.ir.d2i.textract.pipeline;

import abbvie.ir.d2i.textract.model.TextLine;
import abbvie.ir.d2i.textract.pipeline.clustering.TextLinesClustering;
import abbvie.ir.d2i.textract.pipeline.extraction.TextExtractionFromLocalPdf;
import abbvie.ir.d2i.textract.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class ExecutePipeline implements CommandLineRunner {

    @Autowired
    TextExtractionFromLocalPdf textExtractionFromLocalPdf;

    @Autowired
    TextLinesClustering textLinesClustering;


    @Value("${extraction.page_min}")
    private int page_min;


    @Override
    public void run(String... args) throws Exception {
        try {
            //Generate searchable PDF from local pdf
            final String homeDirectory = Utils.getHomeDirectory();
            final List<List<TextLine>> extractedTextLines = textExtractionFromLocalPdf.run(homeDirectory + File.separator + args[0]);

            //Iterate over pages
            for (int i = 0; i < page_min; i++) {
                List<List<TextLine>> cluster = textLinesClustering.getCluster(extractedTextLines.get(i));
                textLinesClustering.saveCluster(cluster,homeDirectory+File.separator+args[0]+"_Clusters"+".txt");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*
            ArrayList<TextLine> arraylist= new ArrayList<TextLine>();
            try
            {
                FileInputStream fis = new FileInputStream(homeDirectory+File.separator+"Output.txt");
                ObjectInputStream ois = new ObjectInputStream(fis);
                arraylist = (ArrayList) ois.readObject();
                ois.close();
                fis.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
                return;
            }catch(ClassNotFoundException c){
                System.out.println("Class not found");
                c.printStackTrace();
                return;
            }
 */
