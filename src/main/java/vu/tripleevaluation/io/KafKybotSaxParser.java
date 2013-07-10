package vu.tripleevaluation.io;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import vu.tripleevaluation.objects.Triple;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 6/1/13
 * Time: 6:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafKybotSaxParser extends DefaultHandler {
    private String elementFirstName = "";
    private String elementSecondName = "";
    private String value = "";
    private ArrayList<Triple> triples;
    private Triple triple = new Triple();

    public KafKybotSaxParser() {
        init();
    }


    public void init () {
        triples = new ArrayList<Triple>();
    }

    public String getElementSecondName() {
        return elementSecondName;
    }

    public void setElementSecondName(String elementSecondName) {
        this.elementSecondName = elementSecondName;
    }

    public String getElementFirstName() {
        return elementFirstName;
    }

    public void setElementFirstName(String elementFirstName) {
        this.elementFirstName = elementFirstName;
    }

    public ArrayList<Triple> getTriples() {
        return triples;
    }

    public void setTriples(ArrayList<Triple> triples) {
        this.triples = triples;
    }

    public boolean parseFile(String filePath)
    {
        return parseFile(new File(filePath));
    }

    public boolean parseFile(File file)
    {
        try
        {
            InputSource inp = new InputSource (new FileReader(file));
            return parse(inp);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean parse(InputSource source)
    {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(source, this);
            return true;
        } catch (FactoryConfigurationError factoryConfigurationError) {
            factoryConfigurationError.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            //  System.out.println("filePath = " + filePath);
            //   System.out.println("XML PARSER ERROR:");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    /*
    <tuple id="7" profile="source-2" sentenceId="s4">
      <!--0bb0ddf81eb203e36e4cf1bf50daa9bc03b5ba0b 73 When shown the transfers , Ibrahim told Reuters it was above board and he only made them to Nicon to pay it back capital it had sunk into Air Nigeria , on which it had made a huge loss .-->
      <communication-event concept="eng-30-01009240-v" confidence="0.282154" lemma="tell" mention="t106" pos="VBD" reference="Kyoto#verb_communication"/>
      <source dep="nsubj" lemma="Ibrahim" mention="t105" pos="NNP" role="agent"/>
    </tuple>

        this.tripleId = "";
        this.profileId = "";
        this.relation = "";
        this.kaflayer = "";
        this.eventLabel = "";
        this.eventIds = new ArrayList<String>();
        this.eventComment = "";
        this.participantLabel = "";
        this.participantIds = new ArrayList<String>();
        this.participantComment = "";
     */
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {
        //System.out.println("qName = " + qName);
        value = "";

            if (qName.equals("tuple")) {
                triple = new Triple();
                //  System.out.println("eventCount = " + eventCount);
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getQName(i);
                    if (name.equalsIgnoreCase("id")) {
                        triple.setTripleId(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("profile")) {
                        triple.setProfileId(attributes.getValue(i).trim());
                    }
                }
            }
            else  if (qName.equals(elementFirstName)) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getQName(i);
                    if (name.equalsIgnoreCase("role")) {
                        triple.setRelation(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("tokens")) {
                        String tokens = (attributes.getValue(i).trim());
                        String [] tokenIds = tokens.split(";");
                        for (int j = 0; j < tokenIds.length; j++) {
                            String tokenId = tokenIds[j];
                            triple.addElementFirstIds(tokenId);
                        }
                    }
                    else if (name.equalsIgnoreCase("lemma")) {
                        triple.setElementFirstComment(attributes.getValue(i).trim());
                    }
                }
            }
            else if (qName.equalsIgnoreCase(elementSecondName)) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getQName(i);
                    if (name.equalsIgnoreCase("role")) {
                        triple.setRelation(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("tokens")) {
                        String tokens = (attributes.getValue(i).trim());
                        String [] tokenIds = tokens.split(";");
                        for (int j = 0; j < tokenIds.length; j++) {
                            String tokenId = tokenIds[j];
                            triple.addElementSecondIds(tokenId);
                        }
                    }
                    else if (name.equalsIgnoreCase("lemma")) {
                        triple.setElementSecondComment(attributes.getValue(i).trim());
                    }
                }
            }

    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {
                if (qName.equals("tuple")) {
                    triples.add(triple);
                }
    }
    ///    <senseAlt><sense sensecode="01124768-a" confidence="0.454265"/><sense sensecode="02080577-a" confidence="0.545735"/></senseAlt></term>

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }



    public void serializeTriples(String pathToFile) {
        try {
            FileOutputStream fos = new FileOutputStream(pathToFile);
            String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
            str += "<triples>\n";
            fos.write(str.getBytes());
            for (int i = 0; i < triples.size(); i++) {
                Triple triple = triples.get(i);
                str = triple.toString();
                fos.write(str.getBytes());
            }
            str = "</triples>\n";
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static public void main (String[] args) {
        String pathToFile = "/Tools/kafkybot.v.0.1/scripts/snapshot-1.tpl";
        String eventElementName = "communication-event";
        String participantElementName = "source";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--tuple-file") && (args.length>(i+1))) {
                pathToFile = args[i+1];
            }
            else if (arg.equals("--event") && (args.length>(i+1))) {
                eventElementName = args[i+1];
            }
            else if (arg.equals("--participant") && (args.length>(i+1))) {
                participantElementName = args[i+1];
            }
        }
        KafKybotSaxParser kafKybotSaxParser = new KafKybotSaxParser();
        kafKybotSaxParser.setElementFirstName(eventElementName);
        kafKybotSaxParser.setElementSecondName(participantElementName);
        kafKybotSaxParser.parseFile(pathToFile);
        kafKybotSaxParser.serializeTriples(pathToFile+".trp");
    }

}
