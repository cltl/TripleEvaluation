package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kybotoutput.objects.KybotEvent;
import eu.kyotoproject.kybotoutput.parser.KybotOutputSaxParser;
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
public class KybotOutputToTriplesAgata {


    static public void main (String [] args) {
        String kybotOutputFile = args[0];
        String kafFile = args[1];
        int threshold = Integer.parseInt(args[2]);
        String format = args[3];
        System.out.println("kybotOutputFile = " + kybotOutputFile);
        System.out.println("kafFile = " + kafFile);
        System.out.println("threshold = " + threshold);
        System.out.println("format = " + format);
        KafSaxParser kafParser = new KafSaxParser ();
        kafParser.parseFile(kafFile);
        KybotOutputSaxParser factParser = new KybotOutputSaxParser ();
        factParser.useShortName = false; /// required to able to find term references in the original KAF
        factParser.parseFile(kybotOutputFile, threshold);

        if (format.equalsIgnoreCase("triples")) {
            try {
                ArrayList<Triple> triples = SemHisTripleConversion.convertKybotEventToTriplesSentenceAsEvent(kafParser, factParser.idFacts);
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
        else if (format.equalsIgnoreCase("kyoto")) {
            try {
                ArrayList<KybotEvent> events = SemHisKyotoConversion.convertKybotEventToTriplesSentenceAsEvent(kafParser, factParser.idFacts);
                String outputFile = kybotOutputFile+"."+threshold+".xml";
                FileOutputStream fos = new FileOutputStream (outputFile);
                String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
                str += "<kybotOut>\n" +
                        "  <doc shortname=\""+kybotOutputFile+"\">\n";
                fos.write(str.getBytes());
                for (int i = 0; i < events.size(); i++) {
                    KybotEvent kybotEvent = events.get(i);
                    str = kybotEvent.toXmlString();
                    fos.write(str.getBytes());
                }
                str = "</doc></kybotOut>\n";
                fos.write(str.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        else {
            System.out.println("unkown format:"+format+". Should be \"triples\" or \"kyoto\"!");
        }

    }
}