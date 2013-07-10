package vu.tripleevaluation.statistics;

import vu.tripleevaluation.objects.Triple;

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
public class Statistics {
    int totalGold;
    public int totalSystem;
    int correctPartialId;
    int correctPartialIdExactRelation;
    int correctExactId;
    int correctExactIdExactRelation;
    public ArrayList<Triple> uniqueSystemTriples;

    public Statistics() {
        this.totalGold = 0;
        this.totalSystem = 0;
        this.correctPartialId = 0;
        this.correctExactId = 0;
        this.correctExactIdExactRelation = 0;
        this.correctPartialIdExactRelation = 0;
        this.uniqueSystemTriples = new ArrayList<Triple>();
    }

    public void addStatistics (Statistics oStats) {
        this.totalGold += oStats.totalGold;
        this.totalSystem += oStats.totalSystem;
        this.correctPartialId += oStats.correctPartialId;
        this.correctExactId += oStats.correctExactId;
        this.correctExactIdExactRelation += oStats.correctExactIdExactRelation;
        this.correctPartialIdExactRelation += oStats.correctPartialIdExactRelation;
        for (int i = 0; i < oStats.uniqueSystemTriples.size(); i++) {
            Triple Triple = oStats.uniqueSystemTriples.get(i);
            this.uniqueSystemTriples.add(Triple);
        }
    }

    public ArrayList<Triple> getUniqueSystemTriples() {
        return uniqueSystemTriples;
    }

    public void setUniqueSystemTriples(ArrayList<Triple> uniqueSystemTriples) {
        this.uniqueSystemTriples = uniqueSystemTriples;
    }

    public void addUniqueSystemTriples(Triple uniqueSystemTriple) {
        this.uniqueSystemTriples.add(uniqueSystemTriple);
    }

    public int getTotalSystem() {
        return totalSystem;
    }

    public void setTotalSystem(int totalSystem) {
        this.totalSystem = totalSystem;
    }

    public void incrementTotalSystem() {
        this.totalSystem++;
    }

    public int getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(int totalGold) {
        this.totalGold = totalGold;
    }

    public void incrementTotalGold() {
        this.totalGold++;
    }

    public int getCorrectPartialId() {
        return correctPartialId;
    }

    public void setCorrectPartialId(int correctPartialId) {
        this.correctPartialId = correctPartialId;
    }

    public void incrementCorrectPartialId() {
        this.correctPartialId++;
    }

    public int getCorrectPartialIdExactRelation() {
        return correctPartialIdExactRelation;
    }

    public void setCorrectPartialIdExactRelation(int correctPartialIdExactRelation) {
        this.correctPartialIdExactRelation = correctPartialIdExactRelation;
    }

    public void incrementCorrectPartialIdExactRelation() {
        this.correctPartialIdExactRelation++;
    }

    public int getCorrectExactId() {
        return correctExactId;
    }

    public void setCorrectExactId(int correctExactId) {
        this.correctExactId = correctExactId;
    }

    public void incrementCorrectExactId() {
        this.correctExactId++;
    }

    public int getCorrectExactIdExactRelation() {
        return correctExactIdExactRelation;
    }

    public void setCorrectExactIdExactRelation(int correctExactIdExactRelation) {
        this.correctExactIdExactRelation = correctExactIdExactRelation;
    }

    public void incrementCorrectExactIdExactRelation() {
        this.correctExactIdExactRelation++;
    }

    public int recallExactId() {
        int recall =  0;
        if (totalGold>0) {
            recall= (100* correctExactId)/ totalGold;
        }
        return recall;
    }
    public int recallExactIdExactRelation() {
        int recall = 0;
        if (totalGold>0) {
            recall= (100* correctExactIdExactRelation)/ totalGold;
        }
        return recall;
    }
    public int recallPartialId() {
        int recall = 0;
        if (totalGold>0) {
           recall = (100* correctPartialId)/ totalGold;
        }
        return (recall);
    }
    public int recallPartialIdExactRelation() {
        int recall = 0;
        if (totalGold>0) {
            recall = (100* correctPartialIdExactRelation)/ totalGold;
        }
        return recall;
    }

    public int precisionExactId() {
        int precision = 0;
        if (totalSystem>0) {
            precision = (100* correctExactId)/ this.getTotalSystem();
        }
        return precision;
    }
    public int precisionExactIdExactRelation() {
        int precision = 0;
        if (totalSystem>0) {
            precision = (100* correctExactIdExactRelation)/ this.getTotalSystem();
        }
        return precision;
    }
    public int precisionPartialId() {
        int precision = 0;
        if (totalSystem>0) {
            precision = (100* correctPartialId)/ this.getTotalSystem();
        }
        return precision;
    }
    public int precisionPartialIdExactRelation() {
        int precision = 0;
        if (totalSystem>0) {
            precision = (100* correctPartialIdExactRelation)/ this.getTotalSystem();
        }
        return precision;
    }

    public String toStringRelation () {
        String str = "\t"+ totalGold +"\t"+totalSystem
                + "\t"+this.correctExactIdExactRelation+"\t"+this.recallExactIdExactRelation()+"\t"+this.precisionExactIdExactRelation()
                +"\t"+this.correctPartialIdExactRelation+"\t"+this.recallPartialIdExactRelation()+"\t"+this.precisionPartialIdExactRelation()+"\n";
        return str;
    }

    public String toStringPartialRelation (int proportion, int proportionSystem) {
        String str = "\t"+ totalGold +"\t"+proportion+"%\t"+totalSystem+"\t"+proportionSystem
                +"%\t"+this.correctPartialIdExactRelation+"\t"+this.recallPartialIdExactRelation()+"\t"+this.precisionPartialIdExactRelation()+"\n";
        return str;
    }

    public String toStringPrecision () {
        String str = "\t"+this.getTotalSystem()
                + "\t"+this.correctExactIdExactRelation+"\t"+this.precisionExactIdExactRelation()
                +"\t"+this.correctExactId+"\t"+this.precisionExactId()
                +"\t"+this.correctPartialIdExactRelation+"\t"+this.precisionPartialIdExactRelation()
                +"\t"+this.correctPartialId+"\t"+this.precisionPartialId()+"\n";
        return str;
    }
}
