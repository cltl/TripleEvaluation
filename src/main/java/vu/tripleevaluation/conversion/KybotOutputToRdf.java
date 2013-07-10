package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kybotoutput.parser.KybotOutputSaxParser;
import vu.tripleevaluation.objects.RdfObject;
import vu.tripleevaluation.objects.Triple;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
public class KybotOutputToRdf {
   static String rdfBase = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
           "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
           "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n" +
           "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" \n" +
           "xmlns:ocw=\"http://simile.mit.edu/2004/01/ontologies/ocw#\" \n" +
           "xmlns:str=\"http://simile.mit.edu/2004/01/xslt/common\" \n" +
           "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
           "xmlns:lom=\"http://www.imsproject.org/rdf/imsmd_rootv1p2#\" \n" +
           "xmlns:lom-life=\"http://www.imsproject.org/rdf/imsmd_lifecyclev1p2#\" \n" +
           "xmlns:lom-edu=\"http://www.imsproject.org/rdf/imsmd_educationalv1p2#\" \n" +
           "xmlns:lom-tech=\"http://www.imsproject.org/rdf/imsmd_technicalv1p2#\" \n" +
           "xmlns:lom-gen=\"http://www.imsproject.org/rdf/imsmd_generalv1p2#\" \n" +
           "xmlns:dcterms=\"http://purl.org/dc/terms/\" \n" +
           "xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" \n" +
           "xmlns:person=\"http://simile.mit.edu/2003/10/ontologies/person#\" \n" +
           "xmlns:vc=\"http://www.w3.org/2001/vcard-rdf/3.0#\"\n" +
           "xmlns:kyoto=\"http://www.kyoto-project.eu/facts#\"";

   static public void main (String [] args) {
        String kybotOutputFile = args[0];
        String kafFile = args[1];
        int threshold = Integer.parseInt(args[2]);
        System.out.println("kybotOutputFile = " + kybotOutputFile);
        System.out.println("kafFile = " + kafFile);
        ArrayList<Triple> triples = new ArrayList<Triple>();
        KafSaxParser kafParser = new KafSaxParser ();
        kafParser.parseFile(kafFile);
        KybotOutputSaxParser factParser = new KybotOutputSaxParser ();
        factParser.useShortName = false; /// required to able to find term references in the original KAF
        factParser.parseFile(kybotOutputFile, threshold);
        triples = KyotoTripleConversion.convertKybotEventToTriples(kafParser, factParser.idFacts);
        try {
            String outputFile = kybotOutputFile+".trp";
            FileOutputStream fos = new FileOutputStream (outputFile);
            String str = rdfBase;
            fos.write(str.getBytes());
            for (int i = 0; i < triples.size(); i++) {
                Triple triple = triples.get(i);
                RdfObject rdf = new RdfObject(triple);
                str = rdf.toXmlString();
                fos.write(str.getBytes());
            }
            str = "</triples>\n";
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
