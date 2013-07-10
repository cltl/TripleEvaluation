package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafWordForm;
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
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemHisTripleConversion {

    static public ArrayList<Triple> convertKybotEventArraysToTriplesAgata (KafSaxParser kafParser, HashMap<String, ArrayList<KybotEvent>> facts) {
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
                ArrayList<String> mergedIds = new ArrayList<String> ();
                ArrayList<String> eventIds = new ArrayList<String> ();
                ArrayList<String> roleIds = new ArrayList<String>();
                ArrayList<String> locIds = new ArrayList<String>();
                ArrayList<String> countryIds = new ArrayList<String>();
                ArrayList<String> dateIds = new ArrayList<String>();

                String rType = "";
                ///// We first collect all the IDs

                /// for the event
                if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                    eventIds = kafParser.TermToWord.get(fact.getTarget());
                }
                /// for the roles
                for (int j = 0; j < fact.getRoles().size(); j++) {
                    KybotRole role = fact.getRoles().get(j);
                    rType = role.getRtype();
                    if (kafParser.TermToWord.containsKey(role.getTarget())) {
                        roleIds = kafParser.TermToWord.get(role.getTarget());
                    }
                }

                /// for the places
                for (int j = 0; j < fact.getLocs().size(); j++) {
                    KybotLocation kybotLocation = fact.getLocs().get(j);
                    for (int k = 0; k < kybotLocation.getSpans().size(); k++) {
                        String s = kybotLocation.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            locIds = kafParser.TermToWord.get(s);
                        }
                    }
                }

                //// for the countries
                for (int j = 0; j < fact.getCountries().size(); j++) {
                    KybotCountry kybotCountry = fact.getCountries().get(j);
                    for (int k = 0; k < kybotCountry.getSpans().size(); k++) {
                        String s = kybotCountry.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            countryIds = kafParser.TermToWord.get(s);
                        }
                    }
                }

                /// for the dates
                for (int j = 0; j < fact.getDates().size(); j++) {
                    KybotDate kybotDate = fact.getDates().get(j);
                    for (int k = 0; k < kybotDate.getSpans().size(); k++) {
                        String s = kybotDate.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            dateIds = kafParser.TermToWord.get(s);
                        }
                    }
                }

                //// we merge all ids to represent the event scope
                mergedIds.addAll(eventIds);
                mergedIds.addAll(roleIds);
                mergedIds.addAll(locIds);
                mergedIds.addAll(countryIds);
                mergedIds.addAll(dateIds);


                if (mergedIds.size()>0) {

                   ////// The event itself becomes a triple with the relation expressed by the roles
                    Triple eTriple = new Triple();
                    eTriple.setElementFirstComment(fact.getLemma());
                    eTriple.setTripleId(fact.getEventId());
                    eTriple.setElementFirstIds(mergedIds);
                    eTriple.setRelation(rType);
                    eTriple.setElementSecondIds(eventIds);
                    eTriple.setProfileId(fact.getProfileId());
                    eTriple.setElementSecondComment(fact.getLemma());
                    triples.add(eTriple);

                    //// for each role we create a separate triple
                    for (int j = 0; j < fact.getRoles().size(); j++) {
                        KybotRole role = fact.getRoles().get(j);
                        if (roleIds.size()>0) {
                            Triple triple = new Triple();
                            triple.setElementFirstComment(fact.getLemma());
                            triple.setTripleId(fact.getEventId());
                            triple.setElementFirstIds(mergedIds);
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
                    /// for each place we create a separate triple
                    String lemma = "";
                    for (int k = 0; k < locIds.size(); k++) {
                        String s = locIds.get(k);
                        lemma += kafParser.getWordForm(s).getWf()+" ";
                    }
                    if (locIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setElementFirstIds(mergedIds);
                        triple.setRelation("LOCATION");
                        triple.setElementSecondIds(locIds);
                        triple.setElementSecondComment(lemma.trim());
                        triples.add(triple);
                    }

                    //// for each place we create a separate triple
                    lemma = "";
                    for (int k = 0; k < countryIds.size(); k++) {
                        String s = countryIds.get(k);
                        lemma += kafParser.getWordForm(s).getWf()+" ";
                    }
                    if (countryIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setElementFirstIds(mergedIds);
                        triple.setRelation("LOCATION");
                        triple.setElementSecondIds(countryIds);
                        triple.setElementSecondComment(lemma.trim());
                        triples.add(triple);
                    }

                    //// for all dates we create a separate triple
                    lemma = "";
                    for (int k = 0; k < dateIds.size(); k++) {
                        String s =  dateIds.get(k);
                        lemma += kafParser.getWordForm(s).getWf()+" ";
                    }
                    if (dateIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setElementFirstIds(mergedIds);
                        triple.setRelation("TIME");
                        triple.setElementSecondIds(dateIds);
                        triple.setElementSecondComment(lemma.trim());
                        triples.add(triple);
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
    static public ArrayList<Triple> convertKybotEventToTriplesAgata (KafSaxParser kafParser, HashMap<String, KybotEvent> facts) {
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
            ArrayList<String> mergedIds = new ArrayList<String> ();
            ArrayList<String> eventIds = new ArrayList<String> ();
            ArrayList<String> roleIds = new ArrayList<String>();
            ArrayList<String> locIds = new ArrayList<String>();
            ArrayList<String> countryIds = new ArrayList<String>();
            ArrayList<String> dateIds = new ArrayList<String>();

            String rType = "";
            ///// We first collect all the IDs

            /// for the event
            if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                eventIds = kafParser.TermToWord.get(fact.getTarget());
            }
            /// for the roles
            for (int j = 0; j < fact.getRoles().size(); j++) {
                KybotRole role = fact.getRoles().get(j);
                rType = role.getRtype();
                if (kafParser.TermToWord.containsKey(role.getTarget())) {
                    roleIds = kafParser.TermToWord.get(role.getTarget());
                }
            }

            /// for the places
            for (int j = 0; j < fact.getLocs().size(); j++) {
                KybotLocation kybotLocation = fact.getLocs().get(j);
                for (int k = 0; k < kybotLocation.getSpans().size(); k++) {
                    String s = kybotLocation.getSpans().get(k);
                    if (kafParser.TermToWord.containsKey(s)) {
                        locIds = kafParser.TermToWord.get(s);
                    }
                }
            }

            //// for the countries
            for (int j = 0; j < fact.getCountries().size(); j++) {
                KybotCountry kybotCountry = fact.getCountries().get(j);
                for (int k = 0; k < kybotCountry.getSpans().size(); k++) {
                    String s = kybotCountry.getSpans().get(k);
                    if (kafParser.TermToWord.containsKey(s)) {
                        countryIds = kafParser.TermToWord.get(s);
                    }
                }
            }

            /// for the dates
            for (int j = 0; j < fact.getDates().size(); j++) {
                KybotDate kybotDate = fact.getDates().get(j);
                for (int k = 0; k < kybotDate.getSpans().size(); k++) {
                    String s = kybotDate.getSpans().get(k);
                    if (kafParser.TermToWord.containsKey(s)) {
                        dateIds = kafParser.TermToWord.get(s);
                    }
                }
            }

            //// we merge all ids to represent the event scope
            mergedIds.addAll(eventIds);
            mergedIds.addAll(roleIds);
            mergedIds.addAll(locIds);
            mergedIds.addAll(countryIds);
            mergedIds.addAll(dateIds);


            if (mergedIds.size()>0) {

               ////// The event itself becomes a triple with the relation expressed by the roles
                Triple eTriple = new Triple();
                eTriple.setElementFirstComment(fact.getLemma());
                eTriple.setTripleId(fact.getEventId());
                eTriple.setElementFirstIds(mergedIds);
                eTriple.setRelation(rType);
                eTriple.setElementSecondIds(eventIds);
                eTriple.setProfileId(fact.getProfileId());
                eTriple.setElementSecondComment(fact.getLemma());
                triples.add(eTriple);

                //// for each role we create a separate triple
                for (int j = 0; j < fact.getRoles().size(); j++) {
                    KybotRole role = fact.getRoles().get(j);
                    if (roleIds.size()>0) {
                        Triple triple = new Triple();
                        triple.setElementFirstComment(fact.getLemma());
                        triple.setTripleId(fact.getEventId());
                        triple.setElementFirstIds(mergedIds);
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
                /// for each place we create a separate triple
                String lemma = "";
                for (int k = 0; k < locIds.size(); k++) {
                    String s = locIds.get(k);
                    lemma += kafParser.getWordForm(s).getWf()+" ";
                }
                if (locIds.size()>0) {
                    Triple triple = new Triple();
                    triple.setTripleId(fact.getEventId());
                    triple.setElementFirstComment(fact.getLemma());
                    triple.setElementFirstIds(mergedIds);
                    triple.setRelation("LOCATION");
                    triple.setElementSecondIds(locIds);
                    triple.setElementSecondComment(lemma.trim());
                    triples.add(triple);
                }

                //// for each place we create a separate triple
                lemma = "";
                for (int k = 0; k < countryIds.size(); k++) {
                    String s = countryIds.get(k);
                    lemma += kafParser.getWordForm(s).getWf()+" ";
                }
                if (countryIds.size()>0) {
                    Triple triple = new Triple();
                    triple.setTripleId(fact.getEventId());
                    triple.setElementFirstComment(fact.getLemma());
                    triple.setElementFirstIds(mergedIds);
                    triple.setRelation("LOCATION");
                    triple.setElementSecondIds(countryIds);
                    triple.setElementSecondComment(lemma.trim());
                    triples.add(triple);
                }

                //// for all dates we create a separate triple
                lemma = "";
                for (int k = 0; k < dateIds.size(); k++) {
                    String s =  dateIds.get(k);
                    lemma += kafParser.getWordForm(s).getWf()+" ";
                }
                if (dateIds.size()>0) {
                    Triple triple = new Triple();
                    triple.setTripleId(fact.getEventId());
                    triple.setElementFirstComment(fact.getLemma());
                    triple.setElementFirstIds(mergedIds);
                    triple.setRelation("TIME");
                    triple.setElementSecondIds(dateIds);
                    triple.setElementSecondComment(lemma.trim());
                    triples.add(triple);
                }
            }
            else {
                nUnResolvedFacts++;
                System.out.println("Could not resolve fact.getTarget() to word forms in KAF file= " + fact.getTarget());
                System.out.println("fact.toXmlString() = " + fact.toXmlString());
            }
        }
        System.out.println("Nr. of facts = " + nFacts);
        System.out.println("Nr. of unresolved facts = "+ nUnResolvedFacts);
        System.out.println("Nr. of unresolved roles = "+ nUnResolvedRoles);
        System.out.println("Nr. of triples = " + triples.size());
        return triples;
    }

    ////// Using this version we take all the sentences in which the events, the participants and the time and place tokens occur
    ////// and use the full sentence to represent the event.
    ////// Since the output of the kybots in this case is non-relational, we take all the elements: event+role to become one participant for the sentence.

/*
  <doc name="dbxml:///srebrenica5hist.dbxml/506.ner.ont.kaf" shortname="506.ner.ont.kaf">
    <event eid="e1" target="t3.9" atype="AdjPnounEnAdjPnoun" mtype="Bosnisch" lemma="Servi&#xC3;&#xAB;r" pos="N.noun" m2type="Bosnisch" n2type="moslim" rtype="Participant" synset="d_n-37913-n" rank="1.0" profile_id="Participant_adj_Pnoun_en_adj_Pnoun"/>
    <event eid="e2" target="t2.19" atype="AdjPnoun" mtype="Bosnisch-Servisch" lemma="soldaat" pos="N.noun" rtype="Participant" synset="d_n-14042-n" rank="0.58857" profile_id="Participant_adj_Pnoun"/>
    <event eid="e3" target="t2.38" atype="AdjPnoun" mtype="gedood" lemma="vader" pos="N.noun" rtype="Participant" synset="d_n-25871-n" rank="0.269053" profile_id="Participant_adj_Pnoun"/>
    <event eid="e4" target="t3.13" atype="AdjPnoun" mtype="Bosnisch" lemma="moslim" pos="N.noun" rtype="Participant" synset="d_n-23964-n" rank="1.0" profile_id="Participant_adj_Pnoun"/>
    <event eid="e5" target="t5.7" atype="AdjPnoun" mtype="Nederlands" lemma="bataljon" pos="N.noun" rtype="Participant" synset="" rank="" profile_id="Participant_adj_Pnoun"/>
    <event eid="e6" target="t6.12" atype="AdjPnoun" mtype="militair" lemma="steun" pos="N.noun" rtype="Participant" synset="d_n-31070-n" rank="0.1813" profile_id="Participant_adj_Pnoun"/>
    <event eid="e7" target="t7.13" atype="AdjPnoun" mtype="Nederlands" lemma="militair" pos="N.noun" rtype="Participant" synset="d_n-12658-n" rank="1.0" profile_id="Participant_adj_Pnoun"/>
    <event eid="e8" target="t7.30" atype="AdjPnoun" mtype="Bosnisch-Servisch" lemma="troep" pos="N.noun" rtype="Participant" synset="" rank="" profile_id="Participant_adj_Pnoun"/>
    <event eid="e9" target="t11.6" atype="AdjPnoun" mtype="Nederlands" lemma="soldaat" pos="N.noun" rtype="Participant" synset="d_n-14042-n" rank="0.589046" profile_id="Participant_adj_Pnoun"/>
    <event eid="e10" target="t3.46" atype="anyDetLnounBC" dtype="het" lemma="stad" pos="N.noun.DIM" rtype="Location" synset="" rank="" profile_id="location_anyDet_LnounBC"/>
    <event eid="e11" target="t14.24" atype="PinMonthLemma" ptype="in" lemma="juni" pos="N.noun" rtype="Time" synset="d_n-17490-n" rank="1.0" profile_id="time_Pin_MonthL"/>
    <event eid="e12" target="t2.25" atype="VerbOHexclDummyF" dummyftype="Nederlands" lemma="wegvoeren" pos="V.verb" rtype="Action" synset="" rank="" profile_id="action_verbOHexcl_dummyf"/>
    <event eid="e13" target="t2.43" atype="VerbOHexclDummyF" dummyftype="wegvoeren" lemma="uitbreken" pos="V.verb" rtype="Action" synset="" rank="" profile_id="action_verbOHexcl_dummyf"/>
    <event eid="e14" target="t3.41" atype="VerbOHexclDummyF" dummyftype="en" lemma="beschermen" pos="V.verb" rtype="Action" synset="" rank="" profile_id="action_verbOHexcl_dummyf"/>
    <event eid="e15" target="t7.34" atype="VerbOHexclDummyF" dummyftype="die" lemma="innemen" pos="V.verb" rtype="Action" synset="" rank="" profile_id="action_verbOHexcl_dummyf"/>
    <event eid="e16" target="t9.33" atype="VerbOHexclDummyF" dummyftype="een" lemma="vermoorden" pos="V.verb" rtype="Action" synset="" rank="" profile_id="action_verbOHexcl_dummyf"/>
    <event eid="e17" target="t14.16" atype="VerbOHexclDummyF" dummyftype="na" lemma="aftreden" pos="V.verb" rtype="Action" synset="" rank="" profile_id="action_verbOHexcl_dummyf"/>
  </doc>
 */


    static public ArrayList<Triple> convertKybotEventArraysToTriplesSentenceAsEvent (KafSaxParser kafParser, HashMap<String, ArrayList<KybotEvent>> facts) {
        ArrayList<Triple> triples = new ArrayList<Triple>();
        HashMap<String, ArrayList<KybotEvent>> sentenceToTripleMap = new HashMap<String, ArrayList<KybotEvent>> ();
        int nFacts = 0;
        int nUnResolvedFacts = 0;
        Set keySet = facts.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            ArrayList<KybotEvent> factList = facts.get(key);
            for (int i = 0; i < factList.size(); i++) {
                KybotEvent fact = factList.get(i);
                if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                    ArrayList<String> factTokens = kafParser.TermToWord.get(fact.getTarget());
                    for (int j = 0; j < factTokens.size(); j++) {
                        String factToken = factTokens.get(j);
                        KafWordForm wf = kafParser.wordFormMap.get(factToken);
                        if (wf!=null) {
                            String sentenceId = wf.getSent();
                            if (sentenceToTripleMap.containsKey(sentenceId)) {
                                ArrayList<KybotEvent> sentenceTriples = sentenceToTripleMap.get(sentenceId);
                                sentenceTriples.add(fact);
                                sentenceToTripleMap.put(sentenceId, sentenceTriples);
                            }
                            else {
                                ArrayList<KybotEvent> sentenceTriples = new ArrayList<KybotEvent>();
                                sentenceTriples.add(fact);
                                sentenceToTripleMap.put(sentenceId, sentenceTriples);
                            }
                        }
                    }
                }
                else {
                    nUnResolvedFacts++;
                }

            }
        }
        keySet = sentenceToTripleMap.keySet();
        keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String sentence = getSentence(kafParser, key);
            //System.out.println("key = " + key);
            ArrayList<KybotEvent> factList = sentenceToTripleMap.get(key);
            ArrayList<KybotEvent> events = new ArrayList<KybotEvent>();
            ArrayList<KybotEvent> participants = new ArrayList<KybotEvent>();
            for (int i = 0; i < factList.size(); i++) {
                KybotEvent fact = factList.get(i);
                if (fact.getRtype().toLowerCase().startsWith("action")) {
                    //System.out.println("Action fact.toXmlString() = " + fact.toXmlString());
                    events.add(fact);
                }
                else {
                    participants.add(fact);
                    //System.out.println("Participant fact.toXmlString() = " + fact.toXmlString());
                }
            }
            if (events.size()>0) {
                for (int i = 0; i < events.size(); i++) {
                    KybotEvent kybotEvent = events.get(i);
                    ArrayList<String> eventIds = new ArrayList<String> ();
                    if (kafParser.TermToWord.containsKey(kybotEvent.getTarget())) {
                        eventIds = kafParser.TermToWord.get(kybotEvent.getTarget());
                    }
                    if (participants.size()>0) {
                        for (int j = 0; j < participants.size(); j++) {
                            KybotEvent participant = participants.get(j);
                            ArrayList<String> participantIds = new ArrayList<String>();
                            if (kafParser.TermToWord.containsKey(participant.getTarget())) {
                                participantIds = kafParser.TermToWord.get(participant.getTarget());
                            }
                            Triple pTriple = new Triple();
                            pTriple.setElementFirstComment(kybotEvent.getLemma());
                            pTriple.setTripleId(key);
                            pTriple.setElementFirstIds(eventIds);
                            pTriple.setRelation(participant.getRtype());
                            pTriple.setElementSecondIds(participantIds);
                            pTriple.setElementSecondComment(participant.getLemma());
                            pTriple.setProfileId(kybotEvent.getProfileId()+"#"+participant.getProfileId());
                            triples.add(pTriple);
                        }
                    }
                    else {
                        ArrayList<String> sentenceIds = kafParser.SentenceToWord.get(key);
                        Triple pTriple = new Triple();
                        pTriple.setElementFirstComment(kybotEvent.getLemma());
                        pTriple.setTripleId(key);
                        pTriple.setElementFirstIds(eventIds);
                        pTriple.setRelation(kybotEvent.getRtype());
                        pTriple.setElementSecondIds(sentenceIds);
                        pTriple.setElementSecondComment(sentence);
                        pTriple.setProfileId(kybotEvent.getProfileId());
                        triples.add(pTriple);
                      //  System.out.println("Sole kybotEvent = " + kybotEvent.toXmlString());
                    }
                }
            }
            else {
                for (int j = 0; j < participants.size(); j++) {
                    KybotEvent participant = participants.get(j);
                    ArrayList<String> participantIds = new ArrayList<String>();
                    if (kafParser.TermToWord.containsKey(participant.getTarget())) {
                        participantIds = kafParser.TermToWord.get(participant.getTarget());
                    }
                    ArrayList<String> sentenceIds = kafParser.SentenceToWord.get(key);
                    Triple pTriple = new Triple();
                    pTriple.setElementFirstComment(sentence);
                    pTriple.setTripleId(key);
                    pTriple.setElementFirstIds(sentenceIds);
                    pTriple.setRelation(participant.getRtype());
                    pTriple.setElementSecondIds(participantIds);
                    pTriple.setElementSecondComment(participant.getLemma());
                    pTriple.setProfileId(participant.getProfileId());
                    triples.add(pTriple);
                }
            }
        }
        System.out.println("Nr. of facts = " + nFacts);
        System.out.println("Nr. of unresolved facts = "+ nUnResolvedFacts);
        System.out.println("Nr. of triples = " + triples.size());
        return triples;
    }
    static public ArrayList<Triple> convertKybotEventToTriplesSentenceAsEvent (KafSaxParser kafParser, HashMap<String, KybotEvent> facts) {
        ArrayList<Triple> triples = new ArrayList<Triple>();
        HashMap<String, ArrayList<KybotEvent>> sentenceToTripleMap = new HashMap<String, ArrayList<KybotEvent>> ();
        int nFacts = 0;
        int nUnResolvedFacts = 0;
        Set keySet = facts.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            KybotEvent fact = facts.get(key);
            if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                ArrayList<String> factTokens = kafParser.TermToWord.get(fact.getTarget());
                for (int j = 0; j < factTokens.size(); j++) {
                    String factToken = factTokens.get(j);
                    KafWordForm wf = kafParser.wordFormMap.get(factToken);
                    if (wf!=null) {
                        String sentenceId = wf.getSent();
                        if (sentenceToTripleMap.containsKey(sentenceId)) {
                            ArrayList<KybotEvent> sentenceTriples = sentenceToTripleMap.get(sentenceId);
                            sentenceTriples.add(fact);
                            sentenceToTripleMap.put(sentenceId, sentenceTriples);
                        }
                        else {
                            ArrayList<KybotEvent> sentenceTriples = new ArrayList<KybotEvent>();
                            sentenceTriples.add(fact);
                            sentenceToTripleMap.put(sentenceId, sentenceTriples);
                        }
                    }
                }
            }
            else {
                nUnResolvedFacts++;
            }
        }
        keySet = sentenceToTripleMap.keySet();
        keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String sentence = getSentence(kafParser, key);
            //System.out.println("key = " + key);
            ArrayList<KybotEvent> factList = sentenceToTripleMap.get(key);
            ArrayList<KybotEvent> events = new ArrayList<KybotEvent>();
            ArrayList<KybotEvent> participants = new ArrayList<KybotEvent>();
            for (int i = 0; i < factList.size(); i++) {
                KybotEvent fact = factList.get(i);
                if (fact.getRtype().toLowerCase().startsWith("action")) {
                    //System.out.println("Action fact.toXmlString() = " + fact.toXmlString());
                    events.add(fact);
                }
                else {
                    participants.add(fact);
                    //System.out.println("Participant fact.toXmlString() = " + fact.toXmlString());
                }
            }
            if (events.size()>0) {
                for (int i = 0; i < events.size(); i++) {
                    KybotEvent kybotEvent = events.get(i);
                    ArrayList<String> eventIds = new ArrayList<String> ();
                    if (kafParser.TermToWord.containsKey(kybotEvent.getTarget())) {
                        eventIds = kafParser.TermToWord.get(kybotEvent.getTarget());
                    }
                    if (participants.size()>0) {
                        for (int j = 0; j < participants.size(); j++) {
                            KybotEvent participant = participants.get(j);
                            ArrayList<String> participantIds = new ArrayList<String>();
                            if (kafParser.TermToWord.containsKey(participant.getTarget())) {
                                participantIds = kafParser.TermToWord.get(participant.getTarget());
                            }
                            Triple pTriple = new Triple();
                            pTriple.setElementFirstComment(kybotEvent.getLemma());
                            pTriple.setTripleId(key);
                            pTriple.setElementFirstIds(eventIds);
                            pTriple.setRelation(participant.getRtype());
                            pTriple.setElementSecondIds(participantIds);
                            pTriple.setElementSecondComment(participant.getLemma());
                            pTriple.setProfileId(kybotEvent.getProfileId()+"#"+participant.getProfileId());
                            triples.add(pTriple);
                        }
                    }
                    else {
                        ArrayList<String> sentenceIds = kafParser.SentenceToWord.get(key);
                        Triple pTriple = new Triple();
                        pTriple.setElementFirstComment(kybotEvent.getLemma());
                        pTriple.setTripleId(key);
                        pTriple.setElementFirstIds(eventIds);
                        pTriple.setRelation(kybotEvent.getRtype());
                        pTriple.setElementSecondIds(sentenceIds);
                        pTriple.setElementSecondComment(sentence);
                        pTriple.setProfileId(kybotEvent.getProfileId());
                        triples.add(pTriple);
                      //  System.out.println("Sole kybotEvent = " + kybotEvent.toXmlString());
                    }
                }
            }
            else {
                for (int j = 0; j < participants.size(); j++) {
                    KybotEvent participant = participants.get(j);
                    ArrayList<String> participantIds = new ArrayList<String>();
                    if (kafParser.TermToWord.containsKey(participant.getTarget())) {
                        participantIds = kafParser.TermToWord.get(participant.getTarget());
                    }
                    ArrayList<String> sentenceIds = kafParser.SentenceToWord.get(key);
                    Triple pTriple = new Triple();
                    pTriple.setElementFirstComment(sentence);
                    pTriple.setTripleId(key);
                    pTriple.setElementFirstIds(sentenceIds);
                    pTriple.setRelation(participant.getRtype());
                    pTriple.setElementSecondIds(participantIds);
                    pTriple.setElementSecondComment(participant.getLemma());
                    pTriple.setProfileId(participant.getProfileId());
                    triples.add(pTriple);
                }
            }
        }
        System.out.println("Nr. of facts = " + nFacts);
        System.out.println("Nr. of unresolved facts = "+ nUnResolvedFacts);
        System.out.println("Nr. of triples = " + triples.size());
        return triples;
    }

    ////// Using this version we take all the sentences in which the events, the participants and the time and place tokens occur
    ////// and use the full sentence to represent the event.
    ////// Since the output of the kybots in this case is non-relational, we take all the elements: event+role to become one participant for the sentence.
    static public ArrayList<Triple> convertKybotEventArraysToTriplesSentenceAsEvent_org (KafSaxParser kafParser,  HashMap<String, ArrayList<KybotEvent>> facts) {
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
                ArrayList<String> mergedIds = new ArrayList<String> ();
                ArrayList<String> eventIds = new ArrayList<String> ();
                ArrayList<String> roleIds = new ArrayList<String>();
                ArrayList<String> locIds = new ArrayList<String>();
                ArrayList<String> countryIds = new ArrayList<String>();
                ArrayList<String> dateIds = new ArrayList<String>();

                String rType = "";
                ///// We first collect all the IDs

                /// for the event
                if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                    eventIds = kafParser.TermToWord.get(fact.getTarget());
                }
                /// for the roles
                for (int j = 0; j < fact.getRoles().size(); j++) {
                    KybotRole role = fact.getRoles().get(j);
                    rType = role.getRtype();
                    if (kafParser.TermToWord.containsKey(role.getTarget())) {
                        roleIds = kafParser.TermToWord.get(role.getTarget());
                    }
                }

                /// for the places
                for (int j = 0; j < fact.getLocs().size(); j++) {
                    KybotLocation kybotLocation = fact.getLocs().get(j);
                    for (int k = 0; k < kybotLocation.getSpans().size(); k++) {
                        String s = kybotLocation.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            locIds = kafParser.TermToWord.get(s);
                        }
                    }
                }

                //// for the countries
                for (int j = 0; j < fact.getCountries().size(); j++) {
                    KybotCountry kybotCountry = fact.getCountries().get(j);
                    for (int k = 0; k < kybotCountry.getSpans().size(); k++) {
                        String s = kybotCountry.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            countryIds = kafParser.TermToWord.get(s);
                        }
                    }
                }

                /// for the dates
                for (int j = 0; j < fact.getDates().size(); j++) {
                    KybotDate kybotDate = fact.getDates().get(j);
                    for (int k = 0; k < kybotDate.getSpans().size(); k++) {
                        String s = kybotDate.getSpans().get(k);
                        if (kafParser.TermToWord.containsKey(s)) {
                            dateIds = kafParser.TermToWord.get(s);
                        }
                    }
                }

                //// we merge all ids to represent the event scope
                mergedIds.addAll(eventIds);
                mergedIds.addAll(roleIds);
/*
                mergedIds.addAll(locIds);
                mergedIds.addAll(countryIds);
                mergedIds.addAll(dateIds);
*/

                ///// mergedIds now contains all the tokens
                ///   we are going to expand it to all the tokens in the sentences that they occur in:

                mergedIds = getSentenceIds(kafParser, mergedIds);
                String sentence = getSentence(kafParser, eventIds);
/*
                System.out.println("Sentence:"+sentence);
                for (int j = 0; j < mergedIds.size(); j++) {
                    String s = mergedIds.get(j);
                    System.out.println("\tid:"+s);
                }
*/

                if (mergedIds.size()>0) {

                    //We merge the event ids and the partipant ids to become one single participant
                    ArrayList<String> eventAndRoleAsParticipant = new ArrayList<String>();
                    eventAndRoleAsParticipant.addAll(eventIds);
                    eventAndRoleAsParticipant.addAll(roleIds);
                   ////// The event itself becomes a triple with the relation expressed by the roles
                    Triple pTriple = new Triple();
                    pTriple.setElementFirstComment(sentence);
                    pTriple.setTripleId(fact.getEventId());
                    pTriple.setElementFirstIds(mergedIds);
                    pTriple.setRelation(rType);
                    pTriple.setElementSecondIds(eventAndRoleAsParticipant);
                    pTriple.setElementSecondComment(fact.getLemma() + ":");
                    pTriple.setProfileId(fact.getProfileId());

                    for (int j = 0; j < fact.getRoles().size(); j++) {
                        KybotRole role = fact.getRoles().get(j);
                        if (roleIds.size()>0) {
                            pTriple.extendParticipantComment(role.getLemma());
                        }
                        else {
                            nUnResolvedRoles++;
                            System.out.println("Could not resolve role.getTarget() to word forms in KAF file= " + role.getTarget());
                            System.out.println("role.toXmlString() = " + role.toXmlString());
                        }
                    }
                    triples.add(pTriple);
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
    ////// Using this version we take all the sentences in which the events, the participants and the time and place tokens occur
    ////// and use the full sentence to represent the event.
    ////// Since the output of the kybots in this case is non-relational, we take all the elements: event+role to become one participant for the sentence.
    static public ArrayList<Triple> convertKybotEventToTriplesSentenceAsEvent_org (KafSaxParser kafParser,  HashMap<String, KybotEvent> facts) {
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
            ArrayList<String> mergedIds = new ArrayList<String> ();
            ArrayList<String> eventIds = new ArrayList<String> ();
            ArrayList<String> roleIds = new ArrayList<String>();
            ArrayList<String> locIds = new ArrayList<String>();
            ArrayList<String> countryIds = new ArrayList<String>();
            ArrayList<String> dateIds = new ArrayList<String>();

            String rType = "";
            ///// We first collect all the IDs

            /// for the event
            if (kafParser.TermToWord.containsKey(fact.getTarget())) {
                eventIds = kafParser.TermToWord.get(fact.getTarget());
            }
            /// for the roles
            for (int j = 0; j < fact.getRoles().size(); j++) {
                KybotRole role = fact.getRoles().get(j);
                rType = role.getRtype();
                if (kafParser.TermToWord.containsKey(role.getTarget())) {
                    roleIds = kafParser.TermToWord.get(role.getTarget());
                }
            }

            /// for the places
            for (int j = 0; j < fact.getLocs().size(); j++) {
                KybotLocation kybotLocation = fact.getLocs().get(j);
                for (int k = 0; k < kybotLocation.getSpans().size(); k++) {
                    String s = kybotLocation.getSpans().get(k);
                    if (kafParser.TermToWord.containsKey(s)) {
                        locIds = kafParser.TermToWord.get(s);
                    }
                }
            }

            //// for the countries
            for (int j = 0; j < fact.getCountries().size(); j++) {
                KybotCountry kybotCountry = fact.getCountries().get(j);
                for (int k = 0; k < kybotCountry.getSpans().size(); k++) {
                    String s = kybotCountry.getSpans().get(k);
                    if (kafParser.TermToWord.containsKey(s)) {
                        countryIds = kafParser.TermToWord.get(s);
                    }
                }
            }

            /// for the dates
            for (int j = 0; j < fact.getDates().size(); j++) {
                KybotDate kybotDate = fact.getDates().get(j);
                for (int k = 0; k < kybotDate.getSpans().size(); k++) {
                    String s = kybotDate.getSpans().get(k);
                    if (kafParser.TermToWord.containsKey(s)) {
                        dateIds = kafParser.TermToWord.get(s);
                    }
                }
            }

            //// we merge all ids to represent the event scope
            mergedIds.addAll(eventIds);
            mergedIds.addAll(roleIds);
/*
            mergedIds.addAll(locIds);
            mergedIds.addAll(countryIds);
            mergedIds.addAll(dateIds);
*/

            ///// mergedIds now contains all the tokens
            ///   we are going to expand it to all the tokens in the sentences that they occur in:

            mergedIds = getSentenceIds(kafParser, mergedIds);
            String sentence = getSentence(kafParser, eventIds);
/*
            System.out.println("Sentence:"+sentence);
            for (int j = 0; j < mergedIds.size(); j++) {
                String s = mergedIds.get(j);
                System.out.println("\tid:"+s);
            }
*/

            if (mergedIds.size()>0) {

                //We merge the event ids and the partipant ids to become one single participant
                ArrayList<String> eventAndRoleAsParticipant = new ArrayList<String>();
                eventAndRoleAsParticipant.addAll(eventIds);
                eventAndRoleAsParticipant.addAll(roleIds);
               ////// The event itself becomes a triple with the relation expressed by the roles
                Triple pTriple = new Triple();
                pTriple.setElementFirstComment(sentence);
                pTriple.setTripleId(fact.getEventId());
                pTriple.setElementFirstIds(mergedIds);
                pTriple.setRelation(rType);
                pTriple.setElementSecondIds(eventAndRoleAsParticipant);
                pTriple.setElementSecondComment(fact.getLemma() + ":");
                pTriple.setProfileId(fact.getProfileId());

                for (int j = 0; j < fact.getRoles().size(); j++) {
                    KybotRole role = fact.getRoles().get(j);
                    if (roleIds.size()>0) {
                        pTriple.extendParticipantComment(role.getLemma());
                    }
                    else {
                        nUnResolvedRoles++;
                        System.out.println("Could not resolve role.getTarget() to word forms in KAF file= " + role.getTarget());
                        System.out.println("role.toXmlString() = " + role.toXmlString());
                    }
                }
                triples.add(pTriple);
            }
            else {
                nUnResolvedFacts++;
                System.out.println("Could not resolve fact.getTarget() to word forms in KAF file= " + fact.getTarget());
                System.out.println("fact.toXmlString() = " + fact.toXmlString());
            }
        }
        System.out.println("Nr. of facts = " + nFacts);
        System.out.println("Nr. of unresolved facts = "+ nUnResolvedFacts);
        System.out.println("Nr. of unresolved roles = "+ nUnResolvedRoles);
        System.out.println("Nr. of triples = " + triples.size());
        return triples;

    }

    static ArrayList<String> getSentenceIds (KafSaxParser kafParser, ArrayList<String> tokenIds) {
        ArrayList<String> ids = new ArrayList<String>();
        for (int i = 0; i < tokenIds.size(); i++) {
            String s = tokenIds.get(i);
            if (!ids.contains(s)) {
                ids.add(s);
            }
            KafWordForm wf = kafParser.wordFormMap.get(s);
            String sentenceId = wf.getSent();
            ArrayList<String> sentenceWfIds = kafParser.SentenceToWord.get(sentenceId);
            for (int j = 0; j < sentenceWfIds.size(); j++) {
                String s1 = sentenceWfIds.get(j);
                if (!ids.contains(s1)) {
                    ids.add(s1);
                }
            }
        }
        return ids;
    }

    static String getSentence (KafSaxParser kafParser, ArrayList<String> tokenIds) {
        String sentence = "";
        ArrayList<String> ids = new ArrayList<String>();
        for (int i = 0; i < tokenIds.size(); i++) {
            String s = tokenIds.get(i);
            KafWordForm wf = kafParser.wordFormMap.get(s);
            String sentenceId = wf.getSent();
            ArrayList<String> sentenceWfIds = kafParser.SentenceToWord.get(sentenceId);
            for (int j = 0; j < sentenceWfIds.size(); j++) {
                String s1 = sentenceWfIds.get(j);
                if (!ids.contains(s1)) {
                    ids.add(s1);
                    KafWordForm wfSentence = kafParser.wordFormMap.get(s1);
                    sentence += " "+wfSentence.getWf();
                }
            }
        }
        return sentence.trim();
    }

    static String getSentence (KafSaxParser kafParser, String sentenceId) {
        String sentence = "";
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> sentenceWfIds = kafParser.SentenceToWord.get(sentenceId);
        if (sentenceWfIds==null) {
            System.out.println("No word forms for sentenceId = " + sentenceId);
        }
        else {
            for (int j = 0; j < sentenceWfIds.size(); j++) {
                String s1 = sentenceWfIds.get(j);
                if (!ids.contains(s1)) {
                    ids.add(s1);
                    KafWordForm wfSentence = kafParser.wordFormMap.get(s1);
                    sentence += " "+wfSentence.getWf();
                }
            }
        }
        return sentence.trim();
    }


}
