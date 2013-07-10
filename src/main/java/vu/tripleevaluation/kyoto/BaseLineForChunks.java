package vu.tripleevaluation.kyoto;

import eu.kyotoproject.kaf.KafChunk;
import eu.kyotoproject.kaf.KafSaxParser;
import vu.tripleevaluation.objects.Triple;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

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
public class BaseLineForChunks {

    static public void main (String[] args) {
        String kafFile = args[0];
        String baseLineRelation = args[1];
        KafSaxParser kafParser = new KafSaxParser ();
        kafParser.parseFile(kafFile);
        Set keySet = kafParser.SentenceToWord.keySet();
        Iterator keys = keySet.iterator();
        ArrayList<Triple> triples = new ArrayList<Triple>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
        //    System.out.println("key = " + key);
            ArrayList<String> headIds = new ArrayList<String>();
            ArrayList<String> wfs = kafParser.SentenceToWord.get(key);
            for (int i = 0; i < wfs.size(); i++) {
                String wfId = wfs.get(i);
                //System.out.println("wfId = " + wfId);
                String termId = kafParser.WordFormToTerm.get(wfId);
                if (termId!=null) {
                    ArrayList<String> chunkIds = kafParser.TermToChunk.get(termId);
                    if (chunkIds!=null) {
                     //   System.out.println("termId = " + termId);
                        for (int j = 0; j < chunkIds.size(); j++) {
                            String chunkId = chunkIds.get(j);
                         //   System.out.println("chunkId = " + chunkId);
                            KafChunk chunk = kafParser.getChunks(chunkId);
                        //    System.out.println("chunk.getHead() = " + chunk.getHead());
                            if (chunk.getHead().equals(termId)) {
                               headIds.add(wfId);
                            }
                        }
                    }
                    else {
                     //   System.out.println("no chunk for termId = " + termId);
                    }
                }
            }
            if (headIds.size()>0) {
                for (int i = 0; i < headIds.size(); i++) {
                    String wfId = headIds.get(i);
                    for (int j = 0; j < headIds.size(); j++) {
                        if (j!=i) {
                            String wfId2 = headIds.get(j);
                            Triple triple = new Triple();
                            triple.addElementFirstIds(wfId);
                            triple.addElementSecondIds(wfId2);
                            triple.setRelation(baseLineRelation);
                            triples.add(triple);
                        }
                    }
                }
            }
        }
        try {
            System.out.println("triples.size() = " + triples.size());
            String outputFile = kafFile+".chunk-baseline.trp";
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
