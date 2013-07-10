package vu.tripleevaluation.conversion;

import eu.kyotoproject.kaf.*;
import vu.tripleevaluation.objects.Triple;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 12/11/12
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class KafLayerToTriple {

   static public ArrayList<String> convertTermSpanToTokenSpan (KafSaxParser kafSaxParser, ArrayList<String> span) {
        ArrayList<String> tokenSpan = new ArrayList<String>();
        for (int i = 0; i < span.size(); i++) {
            String s = span.get(i);
            ArrayList<String> tokens = kafSaxParser.TermToWord.get(s);
            for (int j = 0; j < tokens.size(); j++) {
                String t = tokens.get(j);
                tokenSpan.add(t);
            }

        }
        return tokenSpan;
    }

    static public ArrayList<String> convertCorefTargetsToTokenSpan (KafSaxParser kafSaxParser, ArrayList<CorefTarget> span) {
        ArrayList<String> tokenSpan = new ArrayList<String>();
        for (int i = 0; i < span.size(); i++) {
            CorefTarget corefTarget = span.get(i);
            ArrayList<String> tokens = kafSaxParser.TermToWord.get(corefTarget.getId());
            for (int j = 0; j < tokens.size(); j++) {
                String t = tokens.get(j);
                tokenSpan.add(t);
            }

        }
        return tokenSpan;
    }

    static public ArrayList<Triple> extractSentimentTriplesFromTerms (KafSaxParser kafSaxParser) {
        ArrayList<Triple> TripleArrayList = new ArrayList<Triple>();
        for (int i = 0; i < kafSaxParser.kafTermList.size(); i++) {
            KafTerm kafTerm = kafSaxParser.kafTermList.get(i);
            KafTermSentiment kafTermSentiment = kafTerm.getKafTermSentiment();
            if (kafTermSentiment.hasValue()) {
                String words = "";
                ArrayList<String> tokens = kafSaxParser.TermToWord.get(kafTerm.getTid());
                for (int j = 0; j < tokens.size(); j++) {
                    String s = tokens.get(j);
                    words += kafSaxParser.wordFormMap.get(s).getWf()+" ";
                }
                Triple Triple = new Triple();
                Triple.setTripleId(kafTerm.getTid());
                Triple.setKaflayer("term");
                Triple.setRelation(kafTermSentiment.toTripleLabel());
                Triple.setElementFirstIds(tokens);
                Triple.setElementFirstComment(words.trim());
                TripleArrayList.add(Triple);
            }
        }
        return TripleArrayList;
    }



    static public ArrayList<Triple> extractTriplesFromOpinion (KafSaxParser kafSaxParser, KafOpinion kafOpinion) {
        ArrayList<Triple> TripleArrayList = new ArrayList<Triple>();
        //  String comment = eu.kyotoproject.util.AddTokensAsCommentsToSpans.getTokenStringFromTermIds(kafSaxParser, kafOpinion.getSpansOpinionExpression());
        ArrayList<String> tokens = convertTermSpanToTokenSpan(kafSaxParser, kafOpinion.getSpansOpinionExpression());
        String words = "";
       // System.out.println("tokens.size() = " + tokens.size());
        for (int j = 0; j < tokens.size(); j++) {
            String s = tokens.get(j);
            //System.out.println("s = " + s);
            if (kafSaxParser.wordFormMap.containsKey(s)) {
                words += kafSaxParser.wordFormMap.get(s).getWf()+" ";
            }
            else {
                System.out.println("Cannot find word token = " + s);
            }
        }
        if (kafOpinion.getSpansOpinionHolder().size()>0) {
            Triple Triple = new Triple();
            Triple.setTripleId(kafOpinion.getOpinionId());
            Triple.setKaflayer("opinion");
            Triple.setRelation(kafOpinion.getOpinionSentiment().toTripleLabel());
            Triple.setElementFirstIds(tokens);
            Triple.setElementFirstComment(words.trim());
            ArrayList<String> holderTokens = convertTermSpanToTokenSpan(kafSaxParser, kafOpinion.getSpansOpinionHolder());
            String holderWords = "";
            for (int j = 0; j < holderTokens.size(); j++) {
                String s = holderTokens.get(j);
                if (kafSaxParser.wordFormMap.containsKey(s)) {
                    holderWords += kafSaxParser.wordFormMap.get(s).getWf()+" ";
                }
                else {
                    System.out.println("Cannot find word token = " + s);
                }
            }
            Triple.setElementSecondComment(holderWords.trim());
            Triple.setElementSecondIds(holderTokens);
            Triple.setElementSecondLabel("holder" + ":" + kafOpinion.getOpinionHolderType());
            TripleArrayList.add(Triple);
        }
        if (kafOpinion.getSpansOpinionTarget().size()>0) {
            Triple Triple = new Triple();
            Triple.setTripleId(kafOpinion.getOpinionId());
            Triple.setKaflayer("opinion");
            Triple.setRelation(kafOpinion.getOpinionSentiment().toTripleLabel());
            Triple.setElementFirstIds(tokens);
            Triple.setElementFirstComment(words.trim());
            ArrayList<String> targetTokens = convertTermSpanToTokenSpan(kafSaxParser, kafOpinion.getSpansOpinionTarget());
            String targetWords = "";
            for (int j = 0; j < targetTokens.size(); j++) {
                String s = targetTokens.get(j);
                if (kafSaxParser.wordFormMap.containsKey(s)) {
                    targetWords += kafSaxParser.wordFormMap.get(s).getWf()+" ";
                }
                else {
                    System.out.println("Cannot find word token = " + s);
                }
            }
            Triple.setElementSecondComment(targetWords.trim());
            Triple.setElementSecondIds(targetTokens);
            Triple.setElementSecondLabel("target");
            TripleArrayList.add(Triple);
        }
        if ((kafOpinion.getSpansOpinionTarget().size()==0) &&
            (kafOpinion.getSpansOpinionHolder().size()==0)){
            Triple Triple = new Triple();
            Triple.setTripleId(kafOpinion.getOpinionId());
            Triple.setKaflayer("opinion");
            Triple.setRelation(kafOpinion.getOpinionSentiment().toTripleLabel());
            Triple.setElementFirstIds(tokens);
            Triple.setElementFirstComment(words.trim());
            ArrayList<String> targetTokens = convertTermSpanToTokenSpan(kafSaxParser, kafOpinion.getSpansOpinionTarget());
            String targetWords = "";
            for (int j = 0; j < targetTokens.size(); j++) {
                String s = targetTokens.get(j);
                if (kafSaxParser.wordFormMap.containsKey(s)) {
                    targetWords += kafSaxParser.wordFormMap.get(s).getWf()+" ";
                }
                else {
                    System.out.println("Cannot find word token = " + s);
                }
            }
            TripleArrayList.add(Triple);
        }

        return TripleArrayList;
    }

    static public ArrayList<Triple> extractTriplesFromOpinions (KafSaxParser kafSaxParser) {
        ArrayList<Triple> TripleArrayList = new ArrayList<Triple>();
        //System.out.println("kafSaxParser = " + kafSaxParser.kafOpinionArrayList.size());

        for (int i = 0; i < kafSaxParser.kafOpinionArrayList.size(); i++) {
            KafOpinion kafOpinion = kafSaxParser.kafOpinionArrayList.get(i);
            String overlap = "";
            if (kafOpinion.getOverlap_ents().length()>0) {
                overlap += kafOpinion.getOverlap_ents();
            }
            if (kafOpinion.getOverlap_props().length()>0) {
                overlap += " "+kafOpinion.getOverlap_props();
            }
            overlap = overlap.trim();
            ArrayList<Triple> opinionTripleArrayList = extractTriplesFromOpinion(kafSaxParser, kafOpinion);
            for (int j = 0; j < opinionTripleArrayList.size(); j++) {
                Triple Triple = opinionTripleArrayList.get(j);
                Triple.setProfileId(overlap);
                TripleArrayList.add(Triple);
            }
        }
        return TripleArrayList;
    }


    static public ArrayList<Triple> extractTriplesFromIntersectingOpinions (KafSaxParser kafSaxParser) {
        ArrayList<Triple> TripleArrayList = new ArrayList<Triple>();
       // System.out.println("kafSaxParser = " + kafSaxParser.kafOpinionArrayList.size());
        for (int i = 0; i < kafSaxParser.kafOpinionArrayList.size(); i++) {
            KafOpinion kafOpinion = kafSaxParser.kafOpinionArrayList.get(i);
            String overlap = "";
            if (kafOpinion.getOverlap_ents().length()>0) {
                overlap += kafOpinion.getOverlap_ents();
            }
            if (kafOpinion.getOverlap_props().length()>0) {
                overlap += " "+kafOpinion.getOverlap_props();
            }
            overlap = overlap.trim();
            if (overlap.length()>0)  {
                ArrayList<Triple> opinionTripleArrayList = extractTriplesFromOpinion(kafSaxParser, kafOpinion);
                for (int j = 0; j < opinionTripleArrayList.size(); j++) {
                    Triple Triple = opinionTripleArrayList.get(j);
                    Triple.setProfileId(overlap);
                    TripleArrayList.add(Triple);
                }
            }
        }
        return TripleArrayList;
    }

    static public ArrayList<Triple> extractTriplesFromProperties (KafSaxParser kafSaxParser) {
        ArrayList<Triple> TripleArrayList = new ArrayList<Triple>();
        for (int i = 0; i < kafSaxParser.kafPropertyArrayList.size(); i++) {
            KafProperty kafProperty = kafSaxParser.kafPropertyArrayList.get(i);
            ArrayList<ArrayList<CorefTarget>> setsOfSpans = kafProperty.getSetsOfSpans();
            for (int j = 0; j < setsOfSpans.size(); j++) {
                ArrayList<CorefTarget> corefTargets = setsOfSpans.get(j);
                ArrayList<String> tokens = convertCorefTargetsToTokenSpan(kafSaxParser, corefTargets);
              //  System.out.println("tokens.size() = " + tokens.size());
                String words = "";
                for (int w = 0; w < tokens.size(); w++) {
                    String s = tokens.get(w);
                    if (kafSaxParser.wordFormMap.containsKey(s)) {
                        words += kafSaxParser.wordFormMap.get(s).getWf()+" ";
                    }
                    else {
                        System.out.println("Cannot find word token = " + s);
                    }
                }
                Triple Triple = new Triple();
                Triple.setTripleId(kafProperty.getId());
                Triple.setKaflayer("property");
                Triple.setRelation(kafProperty.getType());
                Triple.setElementFirstIds(tokens);
                Triple.setElementFirstComment(words.trim());
                TripleArrayList.add(Triple);
            }

        }
        return TripleArrayList;
    }

    static public ArrayList<Triple> extractTriplesFromEntities (KafSaxParser kafSaxParser) {
        ArrayList<Triple> TripleArrayList = new ArrayList<Triple>();
        for (int i = 0; i < kafSaxParser.kafEntityArrayList.size(); i++) {
            KafEntity kafEntity = kafSaxParser.kafEntityArrayList.get(i);
            ArrayList<ArrayList<CorefTarget>> setsOfSpans = kafEntity.getSetsOfSpans();
            for (int j = 0; j < setsOfSpans.size(); j++) {
                ArrayList<CorefTarget> corefTargets = setsOfSpans.get(j);
                ArrayList<String> tokens = convertCorefTargetsToTokenSpan(kafSaxParser, corefTargets);
              //  System.out.println("tokens.size() = " + tokens.size());
                String words = "";
                for (int w = 0; w < tokens.size(); w++) {
                    String s = tokens.get(w);
                    if (kafSaxParser.wordFormMap.containsKey(s)) {
                        words += kafSaxParser.wordFormMap.get(s).getWf()+" ";
                    }
                    else {
                        System.out.println("Cannot find word token = " + s);
                    }
                }
                Triple Triple = new Triple();
                Triple.setTripleId(kafEntity.getId());
                Triple.setKaflayer("entity");
                Triple.setRelation(kafEntity.getType());
                Triple.setElementFirstIds(tokens);
                Triple.setElementFirstComment(words.trim());
                TripleArrayList.add(Triple);
            }

        }
        return TripleArrayList;
    }
}
