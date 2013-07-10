package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafWordForm;
import eu.kyotoproject.kybotoutput.objects.KybotEvent;
import eu.kyotoproject.kybotoutput.objects.KybotRole;
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
public class SemHisKyotoConversion {

    static final String RTYPE_PREFIX = "action";

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


    static public ArrayList<KybotEvent> convertKybotEventToTriplesSentenceAsEvent (KafSaxParser kafParser, HashMap<String, KybotEvent> facts) {
        ArrayList<Triple> triples = new ArrayList<Triple>();
        ArrayList<KybotEvent> kybotEvents = new ArrayList<KybotEvent> ();
        HashMap<String, ArrayList<KybotEvent>> sentenceToFactMap = new HashMap<String, ArrayList<KybotEvent>> ();
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
                        if (sentenceToFactMap.containsKey(sentenceId)) {
                            ArrayList<KybotEvent> sentenceTriples = sentenceToFactMap.get(sentenceId);
                            sentenceTriples.add(fact);
                            sentenceToFactMap.put(sentenceId, sentenceTriples);
                        }
                        else {
                            ArrayList<KybotEvent> sentenceTriples = new ArrayList<KybotEvent>();
                            sentenceTriples.add(fact);
                            sentenceToFactMap.put(sentenceId, sentenceTriples);
                        }
                    }
                }
            }
            else {
                nUnResolvedFacts++;
            }
        }
        System.out.println("sentenceToFactMap = " + sentenceToFactMap.size());
        keySet = sentenceToFactMap.keySet();
        keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String sentence = getSentence(kafParser, key);
            //System.out.println("key = " + key);
            ArrayList<KybotEvent> factList = sentenceToFactMap.get(key);
            ArrayList<KybotEvent> events = new ArrayList<KybotEvent>();
            ArrayList<KybotEvent> participants = new ArrayList<KybotEvent>();
            for (int i = 0; i < factList.size(); i++) {
                KybotEvent fact = factList.get(i);
                if (fact.getRtype().toLowerCase().startsWith(RTYPE_PREFIX)) {
                    //System.out.println("Action fact.toXmlString() = " + fact.toXmlString());
                    events.add(fact);
                }
                else {
                    participants.add(fact);
                    //System.out.println("Participant fact.toXmlString() = " + fact.toXmlString());
                }
            }
            if (events.size()==0) {
                System.out.println("NO EVENTS DETECTED FOR SENTENCE "+key+" USING RTYPE PREFIX: "+RTYPE_PREFIX);
                if (participants.size()>0) {
                    /// create a dummy event for this sentence
                    KybotEvent event = new KybotEvent();
                    event.setLemma("NO EVENT");
                    /// We get the first word of the sentence as a target
                    ArrayList<String> sentenceTokens = kafParser.SentenceToWord.get(key);
                    if (sentenceTokens.size()>0) {
                        event.setTarget(sentenceTokens.get(0));
                    }
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
                            KybotRole role = new KybotRole();
                            role.setEventId(kybotEvent.getEventId());
                            role.setLemma(participant.getLemma());
                            role.setSynsetScore(participant.getSynsetScore());
                            role.setPos(participant.getPos());
                            role.setProfileId(participant.getProfileId());
                            role.setRoleId("r" + j);
                            role.setTarget(kybotEvent.getTarget());
                            role.setSynsetId(participant.getSynsetId());
                            role.setRtype(participant.getRtype());
                            kybotEvent.addRoles(role);
                        }
                    }
                    else {
                      //  System.out.println("Sole kybotEvent = " + kybotEvent.toXmlString());
                    }
                    kybotEvents.add(kybotEvent);
                }
            }
            else {
            }
        }
        System.out.println("Nr. of input facts = " + facts.size());
        System.out.println("Nr. of unresolved facts = "+ nUnResolvedFacts);
        System.out.println("Nr. of output facts = " + triples.size());
        return kybotEvents;
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
