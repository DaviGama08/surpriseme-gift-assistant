package pt.isec.gps2526_g42.surprise_me.ui.res;

import javafx.scene.shape.SVGPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class IconManager {
    private IconManager() { }

    // Loads SVGPath from file in ".../res/icons"
    public static SVGPath loadSvgIcon(String fileName) {
        SVGPath svgPath = new SVGPath();
        try (InputStream is = IconManager.class.getResourceAsStream("icons/" + fileName)) {
            if (is == null)
                throw new IllegalArgumentException("Icon not found: " + fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList paths = doc.getElementsByTagName("path");
            StringBuilder combined = new StringBuilder();
            for (int i = 0; i < paths.getLength(); i++) {
                Element pathElement = (Element) paths.item(i);
                String d = pathElement.getAttribute("d");
                if (!d.isEmpty()) {
                    combined.append(d).append(" ");
                }
            }
            svgPath.setContent(combined.toString().trim());

        } catch (Exception e) {
            e.printStackTrace();
            svgPath.setContent("");
        }
        return svgPath;
    }
}
