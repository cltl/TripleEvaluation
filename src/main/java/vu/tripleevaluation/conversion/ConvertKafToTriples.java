package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.KafSaxParser;
import vu.tripleevaluation.objects.Triple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 11/19/12
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertKafToTriples {

    static boolean opinions = false;
    static boolean entities = false;
    static boolean properties = false;
    static boolean srl = false;
    static boolean termsentiment = false;


    static public void main (String[] args) {
       String kafFolder = "";
       String kafFile = "";
       String extension = "";
       boolean intersectOnly = false;
       for (int i = 0; i < args.length; i++) {
           String arg = args[i];
           if (arg.equalsIgnoreCase("--kaf-file") && args.length-1>i) {
               kafFile = args[i+1];
           }
           else if (arg.equalsIgnoreCase("--kaf-folder") && args.length-1>i) {
               kafFolder = args[i+1];
           }
           else if (arg.equalsIgnoreCase("--extension") && args.length-1>i) {
               extension = args[i+1];
           }
           else if (arg.equalsIgnoreCase("--intersect")) {
               intersectOnly = true;
           }
           else if (arg.equalsIgnoreCase("--opinion")) {
               opinions = true;
           }
           else if (arg.equalsIgnoreCase("--entity")) {
               entities = true;
           }
           else if (arg.equalsIgnoreCase("--property")) {
               properties = true;
           }
           else if (arg.equalsIgnoreCase("--term-sentiment")) {
               termsentiment = true;
           }
           else if (arg.equalsIgnoreCase("--srl")) {
               srl = true;
           }
       }
       //System.out.println("kafFile = " + kafFile);
       //System.out.println("kafFolder = " + kafFolder);
       //System.out.println("opinions = " + opinions);
       if (!kafFolder.isEmpty()) {
          ArrayList<File> files = makeFlatFileList(kafFolder, extension);
           for (int i = 0; i < files.size(); i++) {
               File file = files.get(i);
             //  System.out.println("file = " + file);
               kafFileToTriples(file.getAbsolutePath(), intersectOnly);
           }
       }
       if (!kafFile.isEmpty()) {
           kafFileToTriples(kafFile, intersectOnly);
       }
   }

   static public ArrayList<File> makeFlatFileList(String inputPath, String extension) {
        ArrayList<File> acceptedFileList = new ArrayList<File>();
        File[] theFileList = null;
        File lF = new File(inputPath);
        if ((lF.canRead()) && lF.isDirectory()) {
            theFileList = lF.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                if (theFileList[i].isFile()) {
                    if (theFileList[i].getAbsolutePath().endsWith(extension)) {
                        acceptedFileList.add(theFileList[i]);
                    }
                }
            }
        }
        return acceptedFileList;
   }

   static public void kafFileToTriples(String kafFile, boolean intersect) {
        try {
           // System.out.println("kafFile = " + kafFile);
            FileOutputStream fos = new FileOutputStream(kafFile+".trp");
            String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
            str += "<triples>\n";
            fos.write(str.getBytes());
            KafSaxParser kafSaxParser = new KafSaxParser();
            kafSaxParser.parseFile(kafFile);
            //System.out.println("kafSaxParser.getKafOpinionArrayList().size() = " + kafSaxParser.getKafOpinionArrayList().size());
            ArrayList<Triple> Triples = new ArrayList<Triple>();

            if (termsentiment) {
                Triples = KafLayerToTriple.extractSentimentTriplesFromTerms(kafSaxParser);
                for (int i = 0; i < Triples.size(); i++) {
                    Triple Triple = Triples.get(i);
                    str = Triple.toString();
                    fos.write(str.getBytes());
                }
            }
            if (opinions) {
                if (intersect) {
                    Triples = KafLayerToTriple.extractTriplesFromIntersectingOpinions(kafSaxParser);
                    for (int i = 0; i < Triples.size(); i++) {
                        Triple Triple = Triples.get(i);
                        str = Triple.toString();
                        fos.write(str.getBytes());
                    }
                }
                else {
                    Triples = KafLayerToTriple.extractTriplesFromOpinions(kafSaxParser);
                    for (int i = 0; i < Triples.size(); i++) {
                        Triple Triple = Triples.get(i);
                        str = Triple.toString();
                        fos.write(str.getBytes());
                    }
                }
            }
            if (entities) {
                Triples = KafLayerToTriple.extractTriplesFromEntities(kafSaxParser);
                for (int i = 0; i < Triples.size(); i++) {
                    Triple Triple = Triples.get(i);
                    str = Triple.toString();
                    fos.write(str.getBytes());
                }
            }
            if (properties) {
                Triples = KafLayerToTriple.extractTriplesFromProperties(kafSaxParser);
                for (int i = 0; i < Triples.size(); i++) {
                    Triple Triple = Triples.get(i);
                    str = Triple.toString();
                    fos.write(str.getBytes());
                }
            }
/*
            if (srl) {
                Triples = KafLayerToTriple.extractTriplesFromSemanticRoles(kafSaxParser);
                for (int i = 0; i < Triples.size(); i++) {
                    Triple Triple = Triples.get(i);
                    str = Triple.toString();
                    fos.write(str.getBytes());
                }
            }
*/
            str = "</triples>\n";
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     *     public String toTripleLabel () {
     String str = "";
     str += polarity;
     str += "#";
     str += strength;
     str += "#";
     str += sentiment_semantic_type;
     str += "#";
     str += factual;
     str += "#";
     str += subjectivity;
     str += "#";
     str += sentiment_modifier;
     str += "#";
     str += sentiment_product_feature;
     return str;
     }
     */

}
