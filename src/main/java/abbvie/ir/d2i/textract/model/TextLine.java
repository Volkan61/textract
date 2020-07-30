package abbvie.ir.d2i.textract.model;

import java.io.Serializable;

public class TextLine  implements Serializable {
    public double left;
    public double top;
    public double width;
    public double height;
    public String text;

    public TextLine(double left, double top, double width, double height, String text) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    @Override
    public String toString() {
        return "TextLine{" +
                "left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                ", text='" + text + '\'' +
                '}';
    }
}