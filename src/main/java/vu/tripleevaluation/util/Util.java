package vu.tripleevaluation.util;

import vu.tripleevaluation.objects.Triple;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

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



}
