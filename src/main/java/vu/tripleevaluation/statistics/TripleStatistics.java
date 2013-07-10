package vu.tripleevaluation.statistics;

import vu.tripleevaluation.io.TripleSaxParser;
import vu.tripleevaluation.objects.Triple;

import java.util.ArrayList;
import java.util.HashMap;
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
public class TripleStatistics {

    static public String printStatistics (TripleSaxParser parser) {
        String str = parser.message;
        ArrayList<String> elementsFirst = new ArrayList<String>();
        HashMap<String, Integer> relations = new HashMap<String, Integer>();
        for (int i = 0; i < parser.data.size(); i++) {
            Triple Triple = parser.data.get(i);
            if (!elementsFirst.contains(Triple.getTripleId())) {
                elementsFirst.add(Triple.getTripleId());
            }
            if (relations.containsKey(Triple.getRelation())) {
                Integer cnt = relations.get(Triple.getRelation());
                cnt++;
                relations.put(Triple.getRelation(), cnt);
            }
            else {
                relations.put(Triple.getRelation(), new Integer(1));
            }
        }
        str += "Nr of Triple ids:\t"+elementsFirst.size()+"\n";
        str += "Nr of relations:\t"+relations.size()+"\n";
        str += "Total nr of triples:\t"+parser.data.size()+"\n\n";
        str += "Relations:\n";
        Set keySet = relations.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Integer cnt = relations.get(key);
            Double perc = 100*(double)cnt/(double)parser.data.size();
            str += key+"\t"+cnt.toString()+"\t"+perc.intValue()+"%\n";
        }
        return str;
    }

    static public void main (String[] args) {
       String file = args[0];
       TripleSaxParser parser = new TripleSaxParser();
       parser.parseFile(file);
       System.out.println(printStatistics(parser));
    }
}
