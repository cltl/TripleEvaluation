package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kybotoutput.parser.KybotOutputSaxParser;
import vu.tripleevaluation.objects.Triple;

import java.io.File;
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
public class KybotOutputToTriples {


    static public void main (String [] args) {
        String kybotOutputFile = args[0];
        String kafFilePath = args[1];
        int threshold = Integer.parseInt(args[2]);
        File kafFile = new File (kafFilePath);
        System.out.println("kybotOutputFile = " + kybotOutputFile);
        System.out.println("kafFile = " + kafFilePath);
        ArrayList<Triple> triples = new ArrayList<Triple>();
        KafSaxParser kafParser = new KafSaxParser ();
        kafParser.parseFile(kafFilePath);
        KybotOutputSaxParser factParser = new KybotOutputSaxParser ();
        factParser.useShortName = false; /// required to able to find term references in the original KAF
        factParser.setKafFileName(kafFile.getName()); /// required to restrict triples to the kaf file
        factParser.parseFile(kybotOutputFile, threshold);
        triples = KyotoTripleConversion.convertKybotEventToTriples(kafParser, factParser.idFacts);
        try {
            String outputFile = kybotOutputFile+"."+threshold+".trp";
            FileOutputStream fos = new FileOutputStream (outputFile);
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
}
