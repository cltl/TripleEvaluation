package vu.tripleevaluation.io;

import org.w3c.dom.*;
import vu.tripleevaluation.kyoto.SelectBestRoles;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
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
public class KybotProfileReader {

    /*
    <?xml version="1.0" encoding="utf-8"?>
<Kybot id="generic_kybot_Nabstract-region-OR-matter_Vaccomplishment_past_particle_main_clause">
 <variables>
   <var name="v1" type="term" pos="N" reftype="SubClassOf" reference="DOLCE-Lite.owl#region"/>
   <var name="v3" type="term" pos="V" reftype="SubClassOf" reference="DOLCE-Lite.owl#accomplishment | Kyoto#verb_change | Kyoto#verb_consumption | Kyoto#verb_motion | Kyoto#verb_competition | Kyoto#verb_weather | Kyoto#verb_possession | Kyoto#verb_creation | Kyoto#verb_contact" lemma="! can"/>
   <var name="v3" type="term" lemma="! have"/>
   <var name="v3" type="term" lemma="! do"/>
   <var name="v3" type="term" lemma="! be"/>
   <var name="v3" type="term" lemma="! hold"/>
   <var name="v3" type="term" lemma="! contain"/>
   <var name="v3" type="term" lemma="! include"/>
   <var name="v3" type="term" lemma="! will"/>
   <var name="v3" type="term" lemma="! continue"/>
   <var name="v3" type="term" lemma="! begin"/>
   <var name="v3" type="term" lemma="! start"/>
   <var name="v3" type="term" lemma="*ed"/>
 </variables>

<relations>
   <root span="v3"/>
   <rel span="v1" pivot="v3" direction="preceding" immediate="true"/>
</relations>

 <events>
   <event eid="" target="$v3/@tid" lemma="$v3/@lemma" pos="$v3/@pos"/>
   <role rid="" event="" target="$v1/@tid" lemma="$v1/@lemma" pos="$v1/@pos" rtype="patient"/>
 </events>
</Kybot>
     */

    static public Document readProfile (File fXmlFile) {
        Document doc = null;
        try {

           DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
           DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
           doc = dBuilder.parse(fXmlFile);
           doc.getDocumentElement().normalize();

/*
           System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
           NodeList nList = doc.getElementsByTagName("staff");
           System.out.println("-----------------------");

           for (int temp = 0; temp < nList.getLength(); temp++) {

              Node nNode = nList.item(temp);
              if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                 Element eElement = (Element) nNode;

                 System.out.println("First Name : "  + getTagValue("firstname",eElement));
                 System.out.println("Last Name : "  + getTagValue("lastname",eElement));
                 System.out.println("Nick Name : "  + getTagValue("nickname",eElement));
                 System.out.println("Salary : "  + getTagValue("salary",eElement));

               }
           }
*/
         } catch (Exception e) {
           e.printStackTrace();
         }
        return doc;
    }

    private static String getTagValue(String sTag, Element eElement){
       NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
       Node nValue = (Node) nlList.item(0);

       return nValue.getNodeValue();
    }

    static public void writeProfile (Document xmldoc, String folderpath, String fileName) {
        try {
           			// Serialisation through Tranform.
            FileOutputStream fos = new FileOutputStream (folderpath+"/"+fileName);
			DOMSource domSource = new DOMSource(xmldoc);
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", 4);
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");

			StreamResult streamResult = new StreamResult(fos);
			serializer.transform(domSource, streamResult);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }


    static public ArrayList<File> makeFlatFileList(String inputPath, String filter) {
        ArrayList<File> theFileList = new ArrayList<File>();
        File lF = new File(inputPath);
        if ((lF.canRead()) && lF.isDirectory()) {
            for (int i = 0; i < lF.listFiles().length; i++) {
                String newFilePath = lF.listFiles()[i].getAbsolutePath();
                if (lF.listFiles()[i].isFile()) {
                    if (newFilePath.endsWith(filter)) {
                        theFileList.add(lF.listFiles()[i]);
                    }
                }
            }
        }
        return theFileList;
    }

    static public void main (String[] args) {
        String folderPath = args[0];
        String profileFolderPath = args[1];
        String profileConfidenceFile = args[2];
        int confidenceThreshold = Integer.parseInt(args[3]);
        HashMap<String, Integer> profileMap = SelectBestRoles.readProfileScores(profileConfidenceFile);
        File folder = new File (folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (folder.exists()) {
            ArrayList<File> files = makeFlatFileList (profileFolderPath, ".xml");
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                Document doc = readProfile (file);
                NodeList nList = doc.getElementsByTagName("Kybot");
                String profileId = "";
                for (int j = 0; j < nList.getLength(); j++) {
                    Node node = nList.item(j);
                    NamedNodeMap nSubList = node.getAttributes();
                    if (nSubList!=null) {
                        for (int k = 0; k < nSubList.getLength(); k++) {
                            Node attNode = nSubList.item(k);
                            if (attNode.getNodeName().equals("id")) {
                                profileId = attNode.getTextContent();
                                if (profileMap.containsKey(profileId)) {
                                    Integer conf = profileMap.get(profileId);
                                    if (conf.intValue()>=confidenceThreshold) {
                                        String fileName = file.getName();
                                        writeProfile (doc, folderPath, fileName);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /*
            NodeList nList = doc.getElementsByTagName("LITERAL");
            for (int n = 0; n < nList.getLength(); n++) {
                Node node = nList.item(n);
                String synonym = node.getTextContent()+":";
                NamedNodeMap attributes = node.getAttributes();
                Node term = attributes.getNamedItem("sense");
                if (term!=null) {
                   synonym+= term.getTextContent();
                }
                senseInfo.addSynonyms(synonym);
            }
            nList = doc.getElementsByTagName("DEF");
            for (int n = 0; n < nList.getLength(); n++) {
                Node node = nList.item(n);
                senseInfo.setDef(node.getTextContent());
            }
            nList = doc.getElementsByTagName("USAGE");
            for (int n = 0; n < nList.getLength(); n++) {
                Node node = nList.item(n);
                senseInfo.setUsage(node.getTextContent());
            }
            nList = doc.getElementsByTagName("ILR");
            for (int j = 0; j < nList.getLength(); j++) {
                Node node = nList.item(j);
                NamedNodeMap nSubList = node.getAttributes();
                for (int k = 0; k < nSubList.getLength(); k++) {
                    Node subNode = nSubList.item(k);
                    if (subNode.getNodeName().equals("type")) {
                        String rel = subNode.getTextContent()+"="+node.getTextContent();
                        senseInfo.addRelations(rel);
                    }
                }
            }

     */

}
