package abbvie.ir.d2i.textract.pipeline.extraction;

import abbvie.ir.d2i.textract.model.PDFDocument;
import abbvie.ir.d2i.textract.model.TextLine;
import abbvie.ir.d2i.textract.utils.Utils;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


@Component
public class TextExtractionFromLocalPdf {

    @Value("${extraction.image.dpi}")
    private int dpi;

    @Value("${extraction.page_min}")
    private int page_min;


    private List<TextLine> extractText(ByteBuffer imageBytes){

        AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

        DetectDocumentTextRequest request = new DetectDocumentTextRequest()
                .withDocument(new Document()
                       .withBytes(imageBytes));
        DetectDocumentTextResult result = client.detectDocumentText(request);

        List<TextLine> lines = new ArrayList<TextLine>();
        List<Block> blocks = result.getBlocks();
        BoundingBox boundingBox = null;
        for (Block block : blocks) {
            if ((block.getBlockType()).equals("LINE")) {
                boundingBox = block.getGeometry().getBoundingBox();
                lines.add(new TextLine(boundingBox.getLeft(),
                        boundingBox.getTop(),
                        boundingBox.getWidth(),
                        boundingBox.getHeight(),
                        block.getText()));
            }
        }

        return lines;
    }

    public List<List<TextLine>> run(String documentName) throws IOException {


        PDFDocument pdfDocument = new PDFDocument();

        List<List<TextLine>> pages = new ArrayList<>();
        BufferedImage image = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteBuffer imageBytes = null;

        //Load pdf document and process each page as image
        PDDocument inputDocument = PDDocument.load(new File(documentName));
        PDFRenderer pdfRenderer = new PDFRenderer(inputDocument);

        int until_page_min = Math.min(page_min, inputDocument.getNumberOfPages());

        for (int page = 0; page < until_page_min; ++page) {

            List<TextLine> textLines_page = new ArrayList<>();

            //Render image
            image = pdfRenderer.renderImageWithDPI(page, dpi, org.apache.pdfbox.rendering.ImageType.RGB);

            //Get image bytes
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIOUtil.writeImage(image, "jpeg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            imageBytes = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

            //Extract text

            textLines_page = extractText(imageBytes);
            pages.add(textLines_page);

            System.out.println("Processed page index: " + page);
        }

        inputDocument.close();

        return pages;

    }
}
