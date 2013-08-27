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
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Piek Vossen
 * Date: aug-2010
 * Time: 6:34:44
 * To change this template use File | Settings | File Templates.
 * This file is part of KybotEvaluation.

 KybotEvaluation is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 KybotEvaluation is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with KybotEvaluation.  If not, see <http://www.gnu.org/licenses/>.
 */
public class TripleSaxParser extends DefaultHandler {
    private int averageElementFirstIdRange;
    private int averageElementSecondIdRange;
    Triple triple;
    String value = "";
    public ArrayList<Triple> data; /// triples in range
    public ArrayList<Triple> outdata;
    public ArrayList<String> tokenRange;
    public String labelFilterSecondElement;
    public String labelFilterFirstElement;
    public ArrayList<String> relationFilter;
    public HashMap <String, String> relationMap;
    public int outOfRangeCount;
    public int TripleCount;
    public String message = "";
    public boolean timeAndLocation = true;
    public String kaflayer = "";
    public String fileName = "";

    public TripleSaxParser() {
        init();
    }

    public TripleSaxParser(ArrayList<String> range) {
        init();
        tokenRange = range;
    }


    public void init() {
        data = new ArrayList<Triple>();
        outdata = new ArrayList<Triple>();
        tokenRange = new ArrayList<String>();
        relationFilter = new ArrayList<String>();
        relationMap = new HashMap<String, String>();
        triple = new Triple();
        averageElementFirstIdRange = 0;
        averageElementSecondIdRange = 0;
        fileName ="";
        outOfRangeCount = 0;
        TripleCount = 0;
        timeAndLocation = true;
        fileName = "";
        this.labelFilterFirstElement = "";
        this.labelFilterSecondElement = "";
    }


    ///// If a tokenrange is defined, we check if the events ids of the Triple are included in the list of token ranges
    boolean checkTokenRange (ArrayList<String> tokenRange, Triple triple) {
        if (tokenRange.size()==0) {
            return true;
        }
        else {
            for (int i = 0; i < triple.getElementFirstIds().size(); i++) {
                String s = triple.getElementFirstIds().get(i);
                //// a single tokenId match is sufficient
                if (tokenRange.contains(s)) {
                    return true;
                }
            }
         ///// If also the ElementSeconds need to be in that range, uncomment the next code
/*
            for (int i = 0; i < Triple.getElementSecondIds().size(); i++) {
                String s = Triple.getElementSecondIds().get(i);
                if (!tokenRange.contains(s)) {
                    return false;
                }
            }
*/
            return false;
        }
    }


    public boolean parseFile(String filePath)
    {
    	return parseFile(new File(filePath));
    }

    public boolean parseFile(File file)
    {
        fileName = file.getName();
        message = "\n\nParsing fileName:\t" + fileName+"\n";

        if (file.exists()) {
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
        else {
            System.out.println("Could not find the file = " + file.getAbsolutePath());
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
            if (data.size()>0) {
                averageElementSecondIdRange = averageElementSecondIdRange /data.size();
                averageElementFirstIdRange = averageElementFirstIdRange /data.size();
            }
            message += "TripleCount:\t" + TripleCount+"\n";
            message += "outOfRangeCount:\t" + outOfRangeCount+"\n";
            message += "inRangeCount:\t" + data.size()+"\n";
            //System.out.println("message = " + message);
            return true;
        } catch (FactoryConfigurationError factoryConfigurationError) {
            System.out.println("message = " + message);
            factoryConfigurationError.printStackTrace();
        } catch (ParserConfigurationException e) {
            System.out.println("message = " + message);
            e.printStackTrace();
        } catch (SAXException e) {
          //  System.out.println("filePath = " + filePath);
         //   System.out.println("XML PARSER ERROR:");
            System.out.println("message = " + message);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("message = " + message);
            e.printStackTrace();
        }
        return false;
    }

    public void startElement(String uri, String localName,
                         String qName, Attributes attributes)
        throws SAXException {
           value = "";
       // System.out.println("qName = " + qName);
           if ((qName.equalsIgnoreCase("triple")) ||
               (qName.equalsIgnoreCase("triplet"))) {
               triple = new Triple();
               for (int i = 0; i < attributes.getLength(); i++) {
                   String name = attributes.getQName(i);
                   if (name.equalsIgnoreCase("id")) {
                       triple.setTripleId(attributes.getValue(i).trim());
                   }
                   else if (name.equalsIgnoreCase("profile_id")) {
                       triple.setProfileId(attributes.getValue(i).trim());
                   }
                   else if (name.equalsIgnoreCase("profile_confidence")) {
                       try {
                           triple.setProfileConfidence(Integer.parseInt(attributes.getValue(i).trim()));
                       } catch (NumberFormatException e) {
                           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                       }
                   }
                   else if (name.equalsIgnoreCase("kaflayer")) {
                       triple.setKaflayer(attributes.getValue(i).trim());
                   }
                   else if (name.equalsIgnoreCase("relation")) {
                       triple.setRelation(attributes.getValue(i).trim());
                   }
               }
           }
           else if ((qName.equalsIgnoreCase("elementFirstIds")) ||
                    (qName.equalsIgnoreCase("eventids"))) {
               for (int i = 0; i < attributes.getLength(); i++) {
                   String name = attributes.getQName(i);
                   if (name.equalsIgnoreCase("comment")) {
                       triple.setElementFirstComment(attributes.getValue(i).trim());
                   }
                   else if (name.equalsIgnoreCase("label")) {
                       triple.setElementFirstLabel(attributes.getValue(i).trim());
                   }
               }
           }
           else if ((qName.equalsIgnoreCase("elementFirst")) ||
                    (qName.equalsIgnoreCase("event"))) {
               for (int i = 0; i < attributes.getLength(); i++) {
                   String name = attributes.getQName(i);
                   if (name.equalsIgnoreCase("id")) {
                       triple.addElementFirstIds(attributes.getValue(i).trim());
                   }
                   else if (name.equalsIgnoreCase("comment")) {
                       triple.setElementFirstComment(attributes.getValue(i).trim());
                   }
               }
           }
           else if ((qName.equalsIgnoreCase("elementSecondIds")) ||
                    (qName.equalsIgnoreCase("participantids"))) {
               for (int i = 0; i < attributes.getLength(); i++) {
                   String name = attributes.getQName(i);
                   if (name.equalsIgnoreCase("comment")) {
                       triple.setElementSecondComment(attributes.getValue(i).trim());
                   }
                   else if (name.equalsIgnoreCase("label")) {
                       triple.setElementSecondLabel(attributes.getValue(i).trim());
                   }
               }
           }
           else if ((qName.equalsIgnoreCase("elementSecond")) ||
                    (qName.equalsIgnoreCase("participant"))) {
               for (int i = 0; i < attributes.getLength(); i++) {
                   String name = attributes.getQName(i);
                   if (name.equalsIgnoreCase("id")) {
                       triple.addElementSecondIds(attributes.getValue(i).trim());
                   }
               }
           }
   }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
            if ((qName.equals("triple")) ||
                (qName.equals("triplet"))) {
                //// this condition removes Triples where eventds and participants have the same Id.
                    if ((!timeAndLocation) &&
                            (triple.getRelation().equals("TIME")) ||
                             triple.getRelation().equals("LOCATION")) {
                        ///// IF TIME AND LOCATION IS FALSE WE IGNORE THESE TripleS.
                    }
                    else {
                        if ((relationFilter.size()==0) || relationFilter.contains(triple.getRelation())) {
                           // System.out.println("relationFilter = " + relationFilter.size());
                            if ((labelFilterFirstElement.isEmpty() || triple.getElementFirstLabel().equals(labelFilterFirstElement))) {
                                if ((labelFilterSecondElement.isEmpty() || triple.getElementSecondLabel().equals(labelFilterSecondElement))) {
                                    TripleCount++;
                                    if (relationMap.containsKey(triple.getRelation())) {
                                        String mappedRelation = relationMap.get(triple.getRelation());
                                        triple.setRelation(mappedRelation);
                                    }
                                    //System.out.println("labelFilterFirstElement = " + labelFilterFirstElement);
                                    System.out.println("labelFilterSecondElement = " + labelFilterSecondElement);
                                  //  System.out.println("TripleCount = " + TripleCount);
                                    if (checkTokenRange(tokenRange, triple)) {
                                        averageElementSecondIdRange += triple.getElementSecondIds().size();
                                        averageElementFirstIdRange += triple.getElementFirstIds().size();
                                        if (triple.getProfileId().length()==0) {
                                            triple.setProfileId(triple.getRelation());
                                        }
                                        data.add(triple);
                                    }
                                    else {
                                        outOfRangeCount++;
                                        outdata.add(triple);
                                    }
                                }
                            }
                        }
                    }
            }
    }

    public int nUniqueElementsFirst() {
        return (countElementsFirst(data).size()+ countElementsFirst(outdata).size());
    }

    public int nUniqueElementsFirstInData() {
        return countElementsFirst(data).size();
    }

    public String printElementsFirstInData () {
        ArrayList<Triple> list = countElementsFirst(data);
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            Triple triple1 = list.get(i);
            for (int j = 0; j < triple1.getElementFirstIds().size(); j++) {
                String s = triple1.getElementFirstIds().get(j);
                str +=s+"\n";
            }
            //str +="\n";
        }
        return str;
    }

    public int nUniqueElementsFirstOutData () {
        return countElementsFirst(outdata).size();
    }

    public ArrayList<Triple> countElementsFirst(ArrayList<Triple> data) {
        ArrayList<Triple> uniqueElementsFirstTriples = new ArrayList<Triple>();
        for (int i = 0; i < data.size(); i++) {
            Triple Triple1 = data.get(i);
            if (Triple1.getElementFirstIds().size()>0) {
                boolean  matchedEvent = false;
                for (int j = 0; j < uniqueElementsFirstTriples.size(); j++) {
                    Triple Triple2 = uniqueElementsFirstTriples.get(j);
                    for (int k = 0; k < Triple2.getElementFirstIds().size(); k++) {
                        String s = Triple2.getElementFirstIds().get(k);
                        if (Triple1.getElementFirstIds().contains(s)) {
                            matchedEvent = true;
                            break;
                        }
                    }
                }
                if (!matchedEvent) {
                    uniqueElementsFirstTriples.add(Triple1);
                }
            }
        }
        return uniqueElementsFirstTriples;
    }

    public int nUniqueElementsSecond () {
        return (countElementsSecond(data).size()+countElementsSecond(outdata).size());
    }

    public int nUniqueElementsSecondInData () {
        return countElementsSecond(data).size();
    }

    public String printElementsSecondInData () {
        ArrayList<Triple> list = countElementsSecond(data);
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            Triple Triple1 = list.get(i);
            for (int j = 0; j < Triple1.getElementSecondIds().size(); j++) {
                String s = Triple1.getElementSecondIds().get(j);
                str +=s+"\n";
            }
            //str +="\n";
        }
        return str;
    }

    public int nUniqueElementsSecondOutData () {
        return countElementsSecond(outdata).size();
    }

    public ArrayList<Triple> countElementsSecond(ArrayList<Triple> data) {
        ArrayList<Triple> uniqueElementSecondTriples = new ArrayList<Triple>();
        for (int i = 0; i < data.size(); i++) {
            Triple Triple1 = data.get(i);
            if (Triple1.getElementSecondIds().size()>0) {
                boolean  matchedElementSecond = false;
                for (int j = 0; j < uniqueElementSecondTriples.size(); j++) {
                    Triple Triple2 = uniqueElementSecondTriples.get(j);
                    for (int k = 0; k < Triple2.getElementSecondIds().size(); k++) {
                        String s = Triple2.getElementSecondIds().get(k);
                        if (Triple1.getElementSecondIds().contains(s)) {
                            matchedElementSecond = true;
                            break;
                        }
                    }
                }
                if (!matchedElementSecond) {
                    uniqueElementSecondTriples.add(Triple1);
                }
            }
        }
        return uniqueElementSecondTriples;
    }

    public void removeTriplesWithElementFirstEqualElementSecond () {
        ArrayList<Triple> newData = new ArrayList<Triple>();
        for (int i = 0; i < data.size(); i++) {
            Triple Triple1 = data.get(i);
            if (!hasIdenticalIds(Triple1)) {
               newData.add(Triple1);
            }
            else {
                if (Triple1.getElementFirstIds().size()!=Triple1.getElementSecondIds().size()) {
                    newData.add(Triple1);
                }
                else if (Triple1.getElementFirstIds().size()>1) {
                    newData.add((Triple1));
                }
            }
        }
        data = newData;
    }

    public void removeTriplesWithElementFirstEqualElementSecond (ArrayList<Triple> data) {
        ArrayList<Triple> newData = new ArrayList<Triple>();
        for (int i = 0; i < data.size(); i++) {
            Triple Triple1 = data.get(i);
            if (!hasIdenticalIds(Triple1)) {
               newData.add(Triple1);
            }
            else {
                if (Triple1.getElementFirstIds().size()!=Triple1.getElementSecondIds().size()) {
                    newData.add(Triple1);
                }
                else if (Triple1.getElementFirstIds().size()>1) {
                    newData.add((Triple1));
                }
            }
        }
        data = newData;
    }

    public boolean hasIdenticalIds (Triple Triple1) {
        for (int j = 0; j < Triple1.getElementFirstIds().size(); j++) {
            String s = Triple1.getElementFirstIds().get(j);
            if (!Triple1.getElementSecondIds().contains(s)) {
               return false;
            }
        }
        return true;
    }

    public void removeDuplicateTriples () {

    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }

    public int getAverageElementFirstIdRange() {
        return averageElementFirstIdRange;
    }

    public void setAverageElementFirstIdRange(int averageElementFirstIdRange) {
        this.averageElementFirstIdRange = averageElementFirstIdRange;
    }

    public int getAverageElementSecondIdRange() {
        return averageElementSecondIdRange;
    }

    public void setAverageElementSecondIdRange(int averageElementSecondIdRange) {
        this.averageElementSecondIdRange = averageElementSecondIdRange;
    }


    public String readTokenRange (String tokenRangeFile) {
        String str = "";
        try {
          str += "tokenRangeFile:\t" + tokenRangeFile+"\n";
          FileInputStream fis = new FileInputStream(tokenRangeFile);
          InputStreamReader isr = new InputStreamReader(fis);
          BufferedReader in = new BufferedReader(isr);
          String inputLine;
          while ((inputLine = in.readLine()) != null) {
              String token = inputLine.trim();
              if (token.length()>0) {
                  if (!tokenRange.contains(token)) {
                      tokenRange.add(token);
                  }
              }
          }
          in.close();
          str += "tokenRange:\t" + tokenRange.size()+"\n";
        }
        catch (Exception eee) {
          String error = "\nException --"+eee.getMessage();
          System.out.println(error);
        }
        return str;
    }

    static public void main (String[] args) {
        String TripleFile = args[0];
        String tokenRangeFile = "";
        TripleSaxParser parser = new TripleSaxParser();
        try {
            String str ="";
             FileOutputStream fos = new FileOutputStream(TripleFile+".txt");
            if (args.length==2) {
                tokenRangeFile = args[1];
            }
            parser.parseFile(TripleFile);
            parser.readTokenRange(tokenRangeFile);
            for (int i = 0; i < parser.data.size(); i++) {
                Triple Triple = parser.data.get(i);
                str = Triple.getRelation()+"#";
                for (int j = 0; j < Triple.getElementFirstIds().size(); j++) {
                    String s = Triple.getElementFirstIds().get(j);
                    str += s+"#";
                }
                for (int j = 0; j < Triple.getElementSecondIds().size(); j++) {
                    String s = Triple.getElementSecondIds().get(j);
                    str += s+"#";
                }
                str += "\n";
                fos.write(str.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
