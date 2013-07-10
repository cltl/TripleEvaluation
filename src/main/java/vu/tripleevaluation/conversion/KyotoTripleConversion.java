package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kybotoutput.objects.*;
import vu.tripleevaluation.objects.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kyoto
 * Date: 2/16/11
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class KyotoTripleConversion {

    public static ArrayList<Triple> convertKybotEventArraysToTriples (KafSaxParser kafParser, HashMap<String, ArrayList<KybotEvent>> facts) {
        ArrayList<Triple> triples = new ArrayList<Triple>();
        int nFacts = 0;
        int nUnResolvedFacts = 0;
        int nUnResolvedRoles = 0;
        Set keySet = facts.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            ArrayList<KybotEvent> factList = facts.get(key);
            for (int i = 0; i < factList.size(); i++) {
                KybotEvent fact = factList.get(i);
                nFacts++;
                ArrayList<String> eventIds = new ArrayList<String> ();

                if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                    eventIds = kafParser.TermToWord.get(fact.getTarget());
                }
                if (eventIds.size()>0) {
                    for (int j = 0; j < fact.getRoles().size(); j++) {
                        KybotRole role = fact.getRoles().get(j);
                        ArrayList<String> roleIds = new ArrayList<String>();
                        if (kafParser.TermToWord.containsKey(role.getTarget())) {
                            roleIds = kafParser.TermToWord.get(role.getTarget());
                        }
                        if (roleIds.size()>0) {
                            Triple triple = new Triple();
                            triple.setElementFirstComment(fact.getLemma());
                            triple.setTripleId(fact.getEventId());
                            triple.setElementFirstIds(eventIds);
                            triple.setRelation(role.getRtype());
                            triple.setElementSecondIds(roleIds);
                            triple.setProfileId(role.getProfileId());
                            triple.setElementSecondComment(role.getLemma());
                            triples.add(triple);
                        }
                        else {
                            nUnResolvedRoles++;
                            System.out.println("Could not resolve role.getTarget() to word forms in KAF file= " + role.getTarget());
                            System.out.println("role.toXmlString() = " + role.toXmlString());
                        }
                    }
                    for (int j = 0; j < fact.getLocs().size(); j++) {
                        ArrayList<String> locIds = new ArrayList<String>();
                        KybotLocation kybotLocation = fact.getLocs().get(j);
                        for (int k = 0; k < kybotLocation.getSpans().size(); k++) {
                            String s = kybotLocation.getSpans().get(k);
                            if (kafParser.TermToWord.containsKey(s)) {
                                locIds = kafParser.TermToWord.get(s);
                            }
                        }
                        String lemma = "";
                        for (int k = 0; k < locIds.size(); k++) {
                            String s = locIds.get(k);
                            lemma += kafParser.getWordForm(s).getWf()+" ";
                        }
                        if (locIds.size()>0) {
                            Triple triple = new Triple();
                            triple.setTripleId(fact.getEventId());
                            triple.setElementFirstComment(fact.getLemma());
                            triple.setElementFirstIds(eventIds);
                            triple.setRelation("LOCATION");
                            triple.setElementSecondIds(locIds);
                            triple.setElementSecondComment(lemma.trim());
                            triples.add(triple);
                        }
                    }
                    for (int j = 0; j < fact.getCountries().size(); j++) {
                        ArrayList<String> countryIds = new ArrayList<String>();
                        KybotCountry kybotCountry = fact.getCountries().get(j);
                        for (int k = 0; k < kybotCountry.getSpans().size(); k++) {
                            String s = kybotCountry.getSpans().get(k);
                            if (kafParser.TermToWord.containsKey(s)) {
                                countryIds = kafParser.TermToWord.get(s);
                            }
                        }
                        String lemma = "";
                        for (int k = 0; k < countryIds.size(); k++) {
                            String s = countryIds.get(k);
                            lemma += kafParser.getWordForm(s).getWf()+" ";
                        }
                        if (countryIds.size()>0) {
                            Triple triple = new Triple();
                            triple.setTripleId(fact.getEventId());
                            triple.setElementFirstComment(fact.getLemma());
                            triple.setElementFirstIds(eventIds);
                            triple.setRelation("LOCATION");
                            triple.setElementSecondIds(countryIds);
                            triple.setElementSecondComment(lemma.trim());
                            triples.add(triple);
                        }
                    }
                    for (int j = 0; j < fact.getDates().size(); j++) {
                        ArrayList<String> dateIds = new ArrayList<String>();
                        KybotDate kybotDate = fact.getDates().get(j);
                        for (int k = 0; k < kybotDate.getSpans().size(); k++) {
                            String s = kybotDate.getSpans().get(k);
                            if (kafParser.TermToWord.containsKey(s)) {
                                dateIds = kafParser.TermToWord.get(s);
                            }
                        }
                        String lemma = "";
                        for (int k = 0; k < dateIds.size(); k++) {
                            String s =  dateIds.get(k);
                            lemma += kafParser.getWordForm(s).getWf()+" ";
                        }
                        if (dateIds.size()>0) {
                            Triple triple = new Triple();
                            triple.setTripleId(fact.getEventId());
                            triple.setElementFirstComment(fact.getLemma());
                            triple.setElementFirstIds(eventIds);
                            triple.setRelation("TIME");
                            triple.setElementSecondIds(dateIds);
                            triple.setElementSecondComment(lemma.trim());
                            triples.add(triple);
                        }
                    }
                }
                else {
                    nUnResolvedFacts++;
                    System.out.println("Could not resolve fact.getTarget() to word forms in KAF file= " + fact.getTarget());
                    System.out.println("fact.toXmlString() = " + fact.toXmlString());
                }
            }
        }
        System.out.println("Nr. of facts = " + nFacts);
        System.out.println("Nr. of unresolved facts = "+ nUnResolvedFacts);
        System.out.println("Nr. of unresolved roles = "+ nUnResolvedRoles);
        System.out.println("Nr. of triples = " + triples.size());
        return triples;
    }
    public static ArrayList<Triple> convertKybotEventToTriples (KafSaxParser kafParser, HashMap<String, KybotEvent> facts) {
        ArrayList<Triple> triples = new ArrayList<Triple>();
        int nFacts = 0;
        int nUnResolvedFacts = 0;
        int nUnResolvedRoles = 0;
        Set keySet = facts.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            KybotEvent fact = facts.get(key);
            nFacts++;
            ArrayList<String> eventIds = new ArrayList<String> ();

            if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                eventIds = kafParser.TermToWord.get(fact.getTarget());
            }
            if (eventIds.size()>0) {
                for (int j = 0; j < fact.getRoles().size(); j++) {
                    KybotRole role = fact.getRoles().get(j);
                    ArrayList<String> roleIds = new ArrayList<String>();
                    if (kafParser.TermToWord.containsKey(role.getTarget())) {
                        roleIds = kafParser.TermToWord.get(role.getTarget());
                    }
                    if (roleIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstIds(eventIds);
                        triple.setRelation(role.getRtype());
                        triple.setElementSecondIds(roleIds);
                        triple.setProfileId(role.getProfileId());
                        triple.setElementSecondComment(role.getLemma());
                        triples.add(triple);
                    }
                    else {
                        nUnResolvedRoles++;
                        System.out.println("Could not resolve role.getTarget() to word forms in KAF file= " + role.getTarget());
                       // System.out.println("role.toXmlString() = " + role.toXmlString());
                    }
                }
                for (int j = 0; j < fact.getLocs().size(); j++) {
                    ArrayList<String> locIds = new ArrayList<String>();
                    KybotLocation kybotLocation = fact.getLocs().get(j);
                    for (int k = 0; k < kybotLocation.getSpans().size(); k++) {
                        String s = kybotLocation.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            locIds = kafParser.TermToWord.get(s);
                        }
                    }
                    String lemma = "";
                    for (int k = 0; k < locIds.size(); k++) {
                        String s = locIds.get(k);
                        lemma += kafParser.getWordForm(s).getWf()+" ";
                    }
                    if (locIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setElementFirstIds(eventIds);
                        triple.setRelation("LOCATION");
                        triple.setElementSecondIds(locIds);
                        triple.setElementSecondComment(lemma.trim());
                        triples.add(triple);
                    }
                }
                for (int j = 0; j < fact.getCountries().size(); j++) {
                    ArrayList<String> countryIds = new ArrayList<String>();
                    KybotCountry kybotCountry = fact.getCountries().get(j);
                    for (int k = 0; k < kybotCountry.getSpans().size(); k++) {
                        String s = kybotCountry.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            countryIds = kafParser.TermToWord.get(s);
                        }
                    }
                    String lemma = "";
                    for (int k = 0; k < countryIds.size(); k++) {
                        String s = countryIds.get(k);
                        lemma += kafParser.getWordForm(s).getWf()+" ";
                    }
                    if (countryIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setElementFirstIds(eventIds);
                        triple.setRelation("LOCATION");
                        triple.setElementSecondIds(countryIds);
                        triple.setElementSecondComment(lemma.trim());
                        triples.add(triple);
                    }
                }
                for (int j = 0; j < fact.getDates().size(); j++) {
                    ArrayList<String> dateIds = new ArrayList<String>();
                    KybotDate kybotDate = fact.getDates().get(j);
                    for (int k = 0; k < kybotDate.getSpans().size(); k++) {
                        String s = kybotDate.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            dateIds = kafParser.TermToWord.get(s);
                        }
                    }
                    String lemma = "";
                    for (int k = 0; k < dateIds.size(); k++) {
                        String s =  dateIds.get(k);
                        lemma += kafParser.getWordForm(s).getWf()+" ";
                    }
                    if (dateIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setElementFirstIds(eventIds);
                        triple.setRelation("TIME");
                        triple.setElementSecondIds(dateIds);
                        triple.setElementSecondComment(lemma.trim());
                        triples.add(triple);
                    }
                }
            }
            else {
                nUnResolvedFacts++;
                System.out.println("Could not resolve fact.getTarget() to word forms in KAF file= " + fact.getTarget());
              //  System.out.println("fact.toXmlString() = " + fact.toXmlString());
            }
        }
        System.out.println("Nr. of facts = " + nFacts);
        System.out.println("Nr. of unresolved facts = "+ nUnResolvedFacts);
        System.out.println("Nr. of unresolved roles = "+ nUnResolvedRoles);
        System.out.println("Nr. of triples = " + triples.size());
        return triples;
    }


}
