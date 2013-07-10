package vu.tripleevaluation.conversion;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import vu.tripleevaluation.objects.KafTripleElement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 11/19/12
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertKafToTriplesDom {

    static ArrayList<KafTripleElement> readConfigFile (String fileName) {
        ArrayList<KafTripleElement> kafTripleElementArrayList = new ArrayList<KafTripleElement>();
        if (new File(fileName).exists() ) {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    String [] fields = inputLine.split(";");
                    if (fields.length==4) {
                        KafTripleElement kafTripleElement = new KafTripleElement(fields[0], fields[1], fields[2], fields[3]);
                        kafTripleElementArrayList.add(kafTripleElement);
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return kafTripleElementArrayList;
    }

   /*
        String tripleId;
    String relation;
    ArrayList<String> eventIds;
    String eventLabel;
    String participantLabel;
    String eventComment;
    ArrayList<String> participantIds;
    String participantComment;
    String profileId;
     */


    static public void main (String[] args) {
        try {
            String kafFile = "";
            String configFile= "";
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equalsIgnoreCase("--kaf-file") && args.length-1>i) {
                    kafFile = args[i+1];
                }
                else if (arg.equalsIgnoreCase("--config") && args.length-1>i) {
                    configFile = args[i+1];
                }
            }
            System.out.println("kafFile = " + kafFile);
            System.out.println("configFile = " + configFile);
            FileOutputStream fos = new FileOutputStream(kafFile+".trp");
            ArrayList<KafTripleElement> kafTripleElementArrayList = readConfigFile(configFile);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(kafFile);
            doc.getDocumentElement().normalize();
            for (int i = 0; i < kafTripleElementArrayList.size(); i++) {
                KafTripleElement kafTripleElement = kafTripleElementArrayList.get(i);
                processKaf(doc, kafTripleElement, fos);

            }
            fos.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


/*        KafSaxParser kafSaxParser = new KafSaxParser();
        kafSaxParser.parseFile(kafFile);*/

    }


    static void processKaf (Document kafDoc, KafTripleElement elementKaf, FileOutputStream fos) throws IOException {
        NodeList kaf = kafDoc.getChildNodes();
        //// all the results per document;
        for (int i = 0; i < kaf.getLength(); i++) {
            Node node = (Node) kaf.item(i);
            System.out.println("element = " + elementKaf.getKafAttribute());
            Node layer = getSubNode(node, elementKaf.getKafLayerName());
            // System.out.println("layer.getNodeName() = " + layer.getNodeName());
            NodeList children = layer.getChildNodes();
            for (int j=0;i<children.getLength();j++) {
                Node child = children.item(j);
                if (child!=null) {
                   // System.out.println("child.getNodeName() = " + child.getNodeName());
                    NamedNodeMap nodeMap = child.getAttributes();
                    if (nodeMap!=null) {
                        for (int k = 0; k < nodeMap.getLength(); k++) {
                            Node attr = nodeMap.item(k);
                            System.out.println("nodeMap = " + attr.toString());
                            if (attr.getNodeName().equals(elementKaf.getKafAttribute())) {
                                System.out.println(attr.getNodeName()+" = "+attr.getNodeValue());
                            }
                        }
                    }
                }
            }


/*            NamedNodeMap attributes = node.getAttributes();
            Node fileNode = attributes.getNamedItem("name");
            if (fileNode!=null) {
                String fileName = fileNode.getNodeValue();
                NodeList semEvents = node.getChildNodes();
                for (int j = 0; j < semEvents.getLength(); j++) {
                    Node semEvent =  (Node) semEvents.item(j);
                    if (!semEvent.hasAttributes()) {
                        continue;
                    }
                    NamedNodeMap semEventAttributes = semEvent.getAttributes();
                    Node mentions = semEventAttributes.getNamedItem("mentions");
                }
            }*/
        }
    }


    static public Node getSubNode (Node node, String name) {
        NodeList children = node.getChildNodes();
        for (int i=0;i<children.getLength();i++) {
            Node child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase(name)) {
                //  System.out.println("child.getNodeName() = " + child.getNodeName());
                return child;
            }
            else {
                Node subnode = getSubNode (child, name);
                if (subnode!=null) {
                    return subnode;
                }
            }
        }
        return null;
    }
}
