package vu.tripleevaluation.util;

import vu.tripleevaluation.io.TripleSaxParser;
import vu.tripleevaluation.objects.Triple;

import java.io.*;
import java.util.*;

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
public class Util {

    static public ArrayList ReadFileToArrayList(String fileName) {
        ArrayList lineList = new ArrayList();
        if (new File (fileName).exists() ) {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    if (inputLine.trim().length()>0) {
                        lineList.add(inputLine.trim());
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lineList;
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

    /*
    Compare two Triples.
    Ids of events and participants should match, relation is ignored
     */
    static public boolean compareTriplesExactId(Triple goldTriple, Triple systemTriple) {
        boolean match = false;
        if (goldTriple.getElementFirstIds().size()!= systemTriple.getElementFirstIds().size()) {
            return false;
        }
        if (goldTriple.getElementSecondIds().size()!= systemTriple.getElementSecondIds().size()) {
            return false;
        }
        ArrayList<String> differenceEventIds = new ArrayList<String>(goldTriple.getElementFirstIds());
        differenceEventIds.removeAll(systemTriple.getElementFirstIds());
        ArrayList<String> differenceParticipantIds = new ArrayList<String>(goldTriple.getElementSecondIds());
        differenceParticipantIds.removeAll(systemTriple.getElementSecondIds());
/*
        System.out.println("goldTriple.getElementFirstIds() = " + goldTriple.getElementFirstIds());
        System.out.println("systemTriple.getElementFirstIds() = " + systemTriple.getElementFirstIds());
        System.out.println("goldTriple.getElementSecondIds() = " + goldTriple.getElementSecondIds());
        System.out.println("systemTriple.getElementSecondIds() = " + systemTriple.getElementSecondIds());
*/
        if (goldTriple.getElementFirstIds().size()==0 && systemTriple.getElementFirstIds().size()==0) {
            match = true;
        }
        else {
            if (differenceEventIds.size()==0) {
                //System.out.println("intersectionEventIds = " + intersectionEventIds);
                match = true;
            }
        }
        if (match) {
            match = false;
            if (goldTriple.getElementSecondIds().size()==0 && systemTriple.getElementSecondIds().size()==0) {
                match = true;
            }
            else {
                if (differenceParticipantIds.size()==0) {
                //    System.out.println("differenceParticipantIds = " + differenceParticipantIds);
                    match = true;
                }
            }
        }
        //if (match) System.out.println("MATCH"); else System.out.println("MISMATCH");
        return match;

    }

    /*
   Compare two Triples.
   Partial match of both event and participant is sufficient.
   Relation is ignored
    */
    static public boolean compareTriplesPartialId(Triple goldTriple, Triple systemTriple) {
        boolean match = false;
        ArrayList<String> intersectionEventIds = new ArrayList<String>(goldTriple.getElementFirstIds());
        intersectionEventIds.retainAll(systemTriple.getElementFirstIds());
        ArrayList<String> intersectionParticipantIds = new ArrayList<String>(goldTriple.getElementSecondIds());
        intersectionParticipantIds.retainAll(systemTriple.getElementSecondIds());
/*
        System.out.println("goldTriple.getElementFirstIds() = " + goldTriple.getElementFirstIds());
        System.out.println("systemTriple.getElementFirstIds() = " + systemTriple.getElementFirstIds());
        System.out.println("goldTriple.getElementSecondIds() = " + goldTriple.getElementSecondIds());
        System.out.println("systemTriple.getElementSecondIds() = " + systemTriple.getElementSecondIds());
*/
        if (goldTriple.getElementFirstIds().size()==0 && systemTriple.getElementFirstIds().size()==0) {
                match = true;
        }
        else {
            if (intersectionEventIds.size()>0) {
                //System.out.println("intersectionEventIds = " + intersectionEventIds);
                match = true;
            }
        }
        if (match) {
            match = false;
            if (goldTriple.getElementSecondIds().size()==0 && systemTriple.getElementSecondIds().size()==0) {
                match = true;
            }
            else {
                if (intersectionParticipantIds.size()>0) {
                    //System.out.println("intersectionParticipantIds = " + intersectionParticipantIds);
                    match = true;
                }
            }
        }
        //if (match) System.out.println("MATCH"); else System.out.println("MISMATCH");
        return match;
    }

   /*
   Compare Triple FirstElement
   Partial match of both event and participant is sufficient.
   If there is a label it is matched as well
   */
    static public boolean intersectFirstElementsPartialId(Triple goldTriple, Triple systemTriple) {
        boolean match = false;
        ArrayList<String> intersectionEventIds = new ArrayList<String>(goldTriple.getElementFirstIds());
        intersectionEventIds.retainAll(systemTriple.getElementFirstIds());
        if (goldTriple.getElementFirstIds().size()==0 && systemTriple.getElementFirstIds().size()==0) {
                match = true;
        }
        else {
            if (intersectionEventIds.size()>0) {
                //System.out.println("intersectionEventIds = " + intersectionEventIds);
                match = true;
            }
        }
        if (!goldTriple.getElementFirstLabel().isEmpty()) {
            match = goldTriple.getElementFirstLabel().equals(systemTriple.getElementFirstLabel());
        }
        return match;
    }

   /*
   Compare Triple SecondElement
   Partial match of both event and participant is sufficient.
   If there is a label it is matched as well.
   */
    static public boolean intersectSecondElementsPartialId(Triple goldTriple, Triple systemTriple) {
        boolean match = false;
        ArrayList<String> intersectionEventIds = new ArrayList<String>(goldTriple.getElementSecondIds());
        intersectionEventIds.retainAll(systemTriple.getElementSecondIds());
        if (goldTriple.getElementSecondIds().size()==0 && systemTriple.getElementSecondIds().size()==0) {
                match = true;
        }
        else {
            if (intersectionEventIds.size()>0) {
                //System.out.println("intersectionEventIds = " + intersectionEventIds);
                match = true;
            }
        }
        if (!goldTriple.getElementSecondLabel().isEmpty()) {
            match = goldTriple.getElementSecondLabel().equals(systemTriple.getElementSecondLabel());
        }
        return match;
    }

   /*
   Compare Triple FirstElement
   Partial match of both event and participant is sufficient.
   */
    static public boolean intersectFirstElementsPartialIdNoLabel(Triple goldTriple, Triple systemTriple) {
        boolean match = false;
        ArrayList<String> intersectionEventIds = new ArrayList<String>(goldTriple.getElementFirstIds());
        intersectionEventIds.retainAll(systemTriple.getElementFirstIds());
        if (goldTriple.getElementFirstIds().size()==0 && systemTriple.getElementFirstIds().size()==0) {
                match = true;
        }
        else {
            if (intersectionEventIds.size()>0) {
                //System.out.println("intersectionEventIds = " + intersectionEventIds);
                match = true;
            }
        }
        return match;
    }

   /*
   Compare Triple SecondElement
   Partial match of both event and participant is sufficient.
   */
    static public boolean intersectSecondElementsPartialIdNoLabel(Triple goldTriple, Triple systemTriple) {
        boolean match = false;
        ArrayList<String> intersectionEventIds = new ArrayList<String>(goldTriple.getElementSecondIds());
        intersectionEventIds.retainAll(systemTriple.getElementSecondIds());
        if (goldTriple.getElementSecondIds().size()==0 && systemTriple.getElementSecondIds().size()==0) {
                match = true;
        }
        else {
            if (intersectionEventIds.size()>0) {
                //System.out.println("intersectionEventIds = " + intersectionEventIds);
                match = true;
            }
        }
        return match;
    }


    /*
    Compare two Triples.
    Ids of events and participant should exactly match and the relation should match
     */
    static public boolean compareTriplesExactIdExactRelation(Triple goldTriple, Triple systemTriple, boolean participantMatch) {
        boolean match = false;
        if (goldTriple.getRelation().equalsIgnoreCase(systemTriple.getRelation())) {
            match = compareTriplesExactId(goldTriple, systemTriple);
        }
        else if (!participantMatch) {
            if (systemTriple.getRelation().equals("participant")) {
                if (goldTriple.getRelation().equals("patient")
                    || goldTriple.getRelation().equals("done-by")
                    || goldTriple.getRelation().equals("destination-of")
                    || goldTriple.getRelation().equals("use-of")
                    || goldTriple.getRelation().equals("instrument")
                    || goldTriple.getRelation().equals("source-of")
                    || goldTriple.getRelation().equals("product-of")
                    || goldTriple.getRelation().equals("path-of")
                        ) {
                    match = compareTriplesExactId(goldTriple, systemTriple);
                }
            }
        }
        return match;
    }


    /*
    Compare two Triples.
    Partial match of both event and participant is sufficient.
    Relation should match
     */

    static public boolean compareTriplesPartialIdExactRelation(Triple goldTriple, Triple systemTriple, boolean participantMatch) {
        boolean match = false;
        if (goldTriple.getRelation().equalsIgnoreCase(systemTriple.getRelation())) {
            match = compareTriplesPartialId(goldTriple, systemTriple);
        }
        else if (!participantMatch) {
            //// special case, if this boolean is set to true then we ignore participant specific relations
            if (systemTriple.getRelation().equals("participant")) {
                if (goldTriple.getRelation().equals("patient")
                    || goldTriple.getRelation().equals("done-by")
                    || goldTriple.getRelation().equals("destination-of")
                    || goldTriple.getRelation().equals("use-of")
                    || goldTriple.getRelation().equals("instrument")
                    || goldTriple.getRelation().equals("source-of")
                    || goldTriple.getRelation().equals("product-of")
                    || goldTriple.getRelation().equals("path-of")
                        ) {
                    match = compareTriplesExactId(goldTriple, systemTriple);
                }
            }
        }
        return match;
    }

    /*
    Check is Triple is present.
    Partial Id match of event and participant is sufficient
    Relation is ignored
     */
    public static boolean hasTriplePartialId(Triple trp, ArrayList<Triple> list) {
        for (int i = 0; i < list.size(); i++) {
            Triple Triple = list.get(i);
            if (compareTriplesPartialId(trp, Triple)) {
                return true;
            }
        }
        return false;
    }

    /*
    Check is Triple is present.
    Partial Id match of event and participant is sufficient
    Relation should match exactly
     */

    public static boolean hasTriplePartialIdExactRelation(Triple trp, ArrayList<Triple> list) {
        for (int i = 0; i < list.size(); i++) {
            Triple Triple = list.get(i);
            if (compareTriplesPartialIdExactRelation(trp, Triple, false)) {
                return true;
            }
        }
        return false;
    }

    /*
    Check if the Triple is in the list.
    - all the event ids & all the participant & the relation should match
     */
    public static boolean hasTripleExactIdExactRelation(Triple trp, ArrayList<Triple> list) {
        for (int i = 0; i < list.size(); i++) {
            Triple Triple = list.get(i);
            if (compareTriplesExactIdExactRelation(trp, Triple, false)) {
               return true;
            }
        }
        return false;
    }

    /*
    Check is Triple is present.
    Exact Id match of event and participant is required
    Relation is ignored
     */
    public static boolean hasTripleExactId(Triple trp, ArrayList<Triple> list) {
        boolean match = false;
        for (int i = 0; i < list.size(); i++) {
            Triple Triple = list.get(i);
            if (compareTriplesExactId(trp, Triple)) {
                return true;
            }
        }
        return false;
    }


    static public ArrayList<Triple> copyTriples (ArrayList<Triple> Triples) {
        ArrayList<Triple> copy = new ArrayList<Triple>();
        for (int i = 0; i < Triples.size(); i++) {
            Triple Triple = Triples.get(i);
            Triple cp = new Triple(Triple);
            copy.add(cp);
        }
        return copy;
    }

    static public void removeTriple (Triple trp, ArrayList<Triple> list) {
        int matchIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            matchIndex= -1;
            Triple Triple = list.get(i);
            if (Triple.getTripleId().equals(trp.getTripleId())) {
                matchIndex = i;
                for (int j = 0; j < Triple.getElementFirstIds().size(); j++) {
                    String s = Triple.getElementFirstIds().get(j);
                    if (!trp.getElementFirstIds().contains(s)) {
                       matchIndex= -1;
                       break;
                    }
                }
                for (int j = 0; j < Triple.getElementSecondIds().size(); j++) {
                    String s = Triple.getElementSecondIds().get(j);
                    if (!trp.getElementSecondIds().contains(s)) {
                       matchIndex= -1;
                       break;
                    }
                }
            }
            else {}
            if (matchIndex>-1) {
                list.remove(matchIndex);
                break;
            }
        }
    }


    public static TreeSet sortTriplesForRelation (ArrayList<Triple> Triples) {
        TreeSet sorter = new TreeSet(
                new Comparator() {
                    public int compare(Object a, Object b) {
                        Triple itemA = (Triple) a;
                        Triple itemB = (Triple) b;
                        if (itemA.getRelation().compareTo(itemB.getRelation())>0) {
                            return -1;
                        }
                        else if (itemA.getRelation().equals(itemB.getRelation())) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
                    }
                }
        );
        for (int i = 0; i < Triples.size(); i++) {
            Triple trp = Triples.get(i);
            sorter.add(trp);
        }
        return sorter;
    }

    static public boolean intersect (ArrayList<String> ids1, ArrayList<String> ids2) {
        for (int i = 0; i < ids1.size(); i++) {
            String s = ids1.get(i);
            if (ids2.contains(s)) {
                return true;
            }
        }
        return false;
    }

    static public String compareElements (TripleSaxParser goldParser, TripleSaxParser systemParser) {
        String str = "";
        int nMatchedFirst = 0;
        int nNotMatchedFirst = 0;
        int nMatchedSecond = 0;
        int nNotMatchedSecond = 0;
        int nFirstGold = 0;
        int nFirstSystem = 0;
        int nSecondGold = 0;
        int nSecondSystem = 0;
        HashMap<String, Integer> firstLabelMatches = new HashMap<String, Integer>();
        HashMap<String, Integer> secondLabelMatches = new HashMap<String, Integer>();
        HashMap<String, Integer> firstLabelMisMatches = new HashMap<String, Integer>();
        HashMap<String, Integer> secondLabelMisMatches = new HashMap<String, Integer>();
        ArrayList<String> elementsFirstGold = new ArrayList<String>();
        ArrayList<String> elementsFirstSystem = new ArrayList<String>();
        ArrayList<String> elementsSecondGold = new ArrayList<String>();
        ArrayList<String> elementsSecondSystem = new ArrayList<String>();

        /// we loop over the GS to determine the GS-elements that were matched
        for (int i = 0; i < goldParser.data.size(); i++) {
            Triple TripleG = goldParser.data.get(i);
            if (TripleG.getElementFirstIds().size()>0) {
                if (!intersect(elementsFirstGold, TripleG.getElementFirstIds())) {
                    elementsFirstGold.addAll(TripleG.getElementFirstIds());
                    nFirstGold++;
                    for (int j = 0; j < systemParser.data.size(); j++) {
                        Triple TripleS = systemParser.data.get(j);
                        if (Util.intersectFirstElementsPartialId(TripleG, TripleS)) {
                            nMatchedFirst++;
                            String label = TripleG.getElementFirstLabel();
                            if (firstLabelMatches.containsKey(label)) {
                                Integer cnt = firstLabelMatches.get(label);
                                cnt++;
                                firstLabelMatches.put(label, cnt);
                            }
                            else {
                                firstLabelMatches.put(label, 1);
                            }
                            // if we do not break, multiple matches are allowed
                             break;
                        }
                        else if (Util.intersectFirstElementsPartialIdNoLabel(TripleG, TripleS)) {
                            /// only labels mismatch but IDs match
                            String labelPair = TripleG.getElementFirstLabel()+":"+TripleS.getElementFirstLabel();
                            if (firstLabelMisMatches.containsKey(labelPair)) {
                                Integer cnt = firstLabelMisMatches.get(labelPair);
                                cnt++;
                                firstLabelMisMatches.put(labelPair, cnt);
                            }
                            else {
                                firstLabelMisMatches.put(labelPair, 1);
                            }
                        }
                    }
                }
            }
            if (TripleG.getElementSecondIds().size()>0) {
                if (!intersect(elementsSecondGold, TripleG.getElementSecondIds())) {
                    elementsSecondGold.addAll(TripleG.getElementSecondIds());
                    nSecondGold++;
                    for (int j = 0; j < systemParser.data.size(); j++) {
                        Triple TripleS = systemParser.data.get(j);
                        if (Util.intersectSecondElementsPartialId(TripleG, TripleS)) {
                            nMatchedSecond++;
                            String label = TripleG.getElementSecondLabel();
                            if (secondLabelMatches.containsKey(label)) {
                                Integer cnt = secondLabelMatches.get(label);
                                cnt++;
                                secondLabelMatches.put(label, cnt);
                            }
                            else {
                                secondLabelMatches.put(label, 1);
                            }
                            // if we do not break, multiple matches are allowed
                            break;
                        }
                        else if (Util.intersectSecondElementsPartialIdNoLabel(TripleG, TripleS)) {
                            /// only labels mismatch but IDs match
                            String labelPair = TripleG.getElementSecondLabel()+":"+TripleS.getElementSecondLabel();
                            if (secondLabelMisMatches.containsKey(labelPair)) {
                                Integer cnt = secondLabelMisMatches.get(labelPair);
                                cnt++;
                                secondLabelMisMatches.put(labelPair, cnt);
                            }
                            else {
                                secondLabelMisMatches.put(labelPair, 1);
                            }
                        }
                    }
                }
            }
        }
        /// we loop over the system triples to find the elements that are not in the GS
        for (int i = 0; i < systemParser.data.size(); i++) {
            Triple TripleS = systemParser.data.get(i);
            if (TripleS.getElementFirstIds().size()>0) {
                if (!intersect(elementsFirstSystem, TripleS.getElementFirstIds())) {
                    elementsFirstSystem.addAll(TripleS.getElementFirstIds());
                    nFirstSystem++;
                    boolean matchedFirst = false;
                    for (int j = 0; j < goldParser.data.size(); j++) {
                        Triple TripleG = goldParser.data.get(j);
                        if (Util.intersectFirstElementsPartialId(TripleG, TripleS)) {
                            matchedFirst = true;
                            break;
                        }
                    }
                    if (!matchedFirst) {
                        nNotMatchedFirst++;
                    }
                }
            }
            if (TripleS.getElementSecondIds().size()>0) {
                if (!intersect(elementsSecondSystem, TripleS.getElementSecondIds())) {
                    elementsSecondSystem.addAll(TripleS.getElementSecondIds());
                    nSecondSystem++;
                    boolean matchedSecond = false;
                    for (int j = 0; j < goldParser.data.size(); j++) {
                        Triple TripleG = goldParser.data.get(j);
                        if (Util.intersectSecondElementsPartialId(TripleG, TripleS)) {
                            matchedSecond = true;
                            break;
                        }
                    }
                    if (!matchedSecond) {
                        nNotMatchedSecond++;
                    }
                }
            }
        }
        str += "\n";
        str += "\tNumber of non-intersecting first elements represented in gold standard Triples\t"+nFirstGold+"\n";
        str += "\tNumber of non-intersecting first elements represented in system Triples\t"+nFirstSystem+"\n";
        str += "\tNumber of correct first elements represented in system Triples\t"+nMatchedFirst+"\n";
        str += "\tRecall of first elements\t"+(double)nMatchedFirst/(double)nFirstGold+"\n";
        str += "\tPrecision of first elements\t"+(double)nMatchedFirst/(double)(nMatchedFirst+nNotMatchedFirst)+"\n";
        str += "\n";
        str += "\tLabel matches first elements:\n";
        Integer total = 0;
        Set keySet = firstLabelMatches.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Integer cnt = firstLabelMatches.get(key);
            total+= cnt;
            str += "\t"+key+"\t"+cnt+"\n";
        }
        str += "\tTotal\t"+total+"\n";
        str += "\n";
        str += "\tLabel mismatches first elements:\n";
        total = 0;
        keySet = firstLabelMisMatches.keySet();
        keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Integer cnt = firstLabelMisMatches.get(key);
            total+= cnt;
            str += "\t"+key+"\t"+cnt+"\n";
        }
        str += "\tTotal\t"+total+"\n";
        str += "\n";
        str += "\n";
        str += "\tNumber of non-intersecting second elements represented in gold standard Triples\t"+nSecondGold+"\n";
        str += "\tNumber of non-intersecting second elements represented in system Triples\t"+nSecondSystem+"\n";
        str += "\tNumber of correct second elements represented in system Triples\t"+(systemParser.nUniqueElementsSecondInData())+"\n";
        str += "\tRecall of second elements\t"+(double)nMatchedSecond/(double)nSecondGold+"\n";
        str += "\tPrecision of second elements\t"+(double)nMatchedSecond/(double)(nMatchedSecond+nNotMatchedSecond)+"\n";
        str += "\n";
        str += "\tLabel matches second elements:\n";
        total = 0;
        keySet = secondLabelMatches.keySet();
        keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Integer cnt = secondLabelMatches.get(key);
            total+= cnt;
            str += "\t"+key+"\t"+cnt+"\n";
        }
        str += "\tTotal\t"+total+"\n";
        str += "\n";
        str += "\tLabel mismatches second elements:\n";
        total = 0;
        keySet = secondLabelMisMatches.keySet();
        keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Integer cnt = secondLabelMisMatches.get(key);
            total+= cnt;
            str += "\t"+key+"\t"+cnt+"\n";
        }
        str += "\tTotal\t"+total+"\n";
        str += "\n";
        str += "\n";
        return str;
    }


}
