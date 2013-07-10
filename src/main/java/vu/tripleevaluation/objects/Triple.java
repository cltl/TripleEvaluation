package vu.tripleevaluation.objects;

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
public class Triple {

    private String tripleId;
    private String relation;
    private String kaflayer;
    private ArrayList<String> elementFirstIds;
    private String elementFirstLabel;
    private String elementSecondLabel;
    private String elementFirstComment;
    private ArrayList<String> elementSecondIds;
    private String elementSecondComment;
    private String profileId;
    private int profileConfidence;

    public Triple() {
        this.tripleId = "";
        this.relation = "";
        this.kaflayer = "";
        this.profileId = "";
        this.profileConfidence = 0;
        this.elementFirstLabel = "";
        this.elementSecondLabel = "";
        this.elementFirstIds = new ArrayList<String>();
        this.elementSecondIds = new ArrayList<String>();
        this.elementFirstComment = "";
        this.elementSecondComment = "";
    }

    public Triple(Triple trp) {
        this.tripleId = trp.getTripleId();
        this.relation = trp.getRelation();
        this.profileId = trp.getProfileId();
        this.profileConfidence = trp.getProfileConfidence();
        this.elementFirstIds = trp.getElementFirstIds();
        this.kaflayer = trp.getKaflayer();
        this.elementSecondIds = trp.getElementSecondIds();
        this.elementFirstLabel = trp.elementFirstLabel;
        this.elementSecondLabel = trp.elementSecondLabel;
        this.elementFirstComment = trp.getElementFirstComment();
        this.elementSecondComment = trp.getElementSecondComment();
    }

    public int getProfileConfidence() {
        return profileConfidence;
    }

    public void setProfileConfidence(int profileConfidence) {
        this.profileConfidence = profileConfidence;
    }

    public String getKaflayer() {
        return kaflayer;
    }

    public void setKaflayer(String kaflayer) {
        this.kaflayer = kaflayer;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getTripleId() {
        return tripleId;
    }

    public void setTripleId(String tripleId) {
        this.tripleId = tripleId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public ArrayList<String> getElementFirstIds() {
        return elementFirstIds;
    }

    public void setElementFirstIds(ArrayList<String> elementFirstIds) {
        this.elementFirstIds = elementFirstIds;
    }

    public void addElementFirstIds(String eventId) {
        this.elementFirstIds.add(eventId);
    }

    public ArrayList<String> getElementSecondIds() {
        return elementSecondIds;
    }

    public void setElementSecondIds(ArrayList<String> elementSecondIds) {
        this.elementSecondIds = elementSecondIds;
    }

    public void addElementSecondIds(String participantId) {
        this.elementSecondIds.add(participantId);
    }

    public String getElementSecondComment() {
        return elementSecondComment;
    }

    public void setElementSecondComment(String elementSecondComment) {
        String str = elementSecondComment.replaceAll("\"","");
        str = str.replaceAll("&", "&amp;") ;
        this.elementSecondComment = str;
    }

    public void extendParticipantComment(String participantComment) {
        this.elementSecondComment += " "+participantComment;
    }

    public String getElementFirstComment() {
        return elementFirstComment;
    }

    public void setElementFirstComment(String elementFirstComment) {
        String str = elementFirstComment.replaceAll("\"","");
        str = str.replaceAll("&", "&amp;") ;
        this.elementFirstComment = str;
    }

    public void extendEventComment(String eventComment) {
        this.elementFirstComment += " "+eventComment;
    }

    public String getElementFirstLabel() {
        return elementFirstLabel;
    }

    public void setElementFirstLabel(String elementFirstLabel) {
        this.elementFirstLabel = elementFirstLabel;
    }

    public String getElementSecondLabel() {
        return elementSecondLabel;
    }

    public void setElementSecondLabel(String elementSecondLabel) {
        this.elementSecondLabel = elementSecondLabel;
    }

    public String toString () {
        String str = "<triple id=\""+this.tripleId +"\"";
        if (!this.profileId.isEmpty()) {
            str += " profile_id=\""+this.profileId+"\"";
        }
        if (this.profileConfidence>0) {
            str += " profile_confidence=\""+this.profileConfidence+"\"";
        }
        if (!this.relation.isEmpty()) {
            str += " relation=\""+this.relation+"\"";
        }
        if (!this.kaflayer.isEmpty()) {
            str += " kaflayer=\""+this.kaflayer+"\"";
        }
        str += ">\n";
        if (elementFirstIds.size()==0) {
            str += "\t<elementFirstIds";
            if (!this.elementFirstLabel.isEmpty()) {
                str += " label=\""+this.elementFirstLabel +"\"";
            }
            if (!this.elementFirstComment.isEmpty()) {
                str += " comment=\""+this.elementFirstComment.replaceAll("\"","")+"\"";
            }
            str += "/>\n";
        }
        else {
            str += "\t<elementFirstIds";
            if (!this.elementFirstLabel.isEmpty()) {
                str += " label=\""+this.elementFirstLabel +"\"";
            }
            if (!this.elementFirstComment.isEmpty()) {
                str += " comment=\""+this.elementFirstComment.replaceAll("\"","")+"\"";
            }
            str += ">\n";
            for (int i = 0; i < elementFirstIds.size(); i++) {
                String s = elementFirstIds.get(i);
                str += "\t\t<elementFirst id=\""+s+"\"/>\n";
            }
            str += "\t</elementFirstIds>\n";
        }
        if (elementSecondIds.size()==0) {
            str += "\t<elementSecondIds";
            if (!this.elementSecondLabel.isEmpty()) {
                str += " label=\""+this.elementSecondLabel +"\"";
            }
            if (!this.elementSecondComment.isEmpty()) {
                str += " comment=\""+this.elementSecondComment.replaceAll("\"","")+"\"";
            }
            str += "/>\n";
        }
        else {
            str += "\t<elementSecondIds";
            if (!this.elementSecondLabel.isEmpty()) {
                str += " label=\""+this.elementSecondLabel +"\"";
            }
            if (!this.elementSecondComment.isEmpty()) {
                str += " comment=\""+this.elementSecondComment.replaceAll("\"","")+"\"";
            }
            str += ">\n";
            for (int i = 0; i < elementSecondIds.size(); i++) {
                String s = elementSecondIds.get(i);
                str += "\t\t<elementSecond id=\""+s+"\"/>\n";
            }
            str += "\t</elementSecondIds>\n";
        }
        str += "</triple>\n";
        return str;
    }

   public String toTableRowString () {
        String str = "<triple id=\""+this.tripleId +"\"";
        if (!this.profileId.isEmpty()) {
            str += " profile_id=\""+this.profileId+"\"";
        }
        if (!this.relation.isEmpty()) {
            str += " relation=\""+this.relation+"\"";
        }
        if (!this.kaflayer.isEmpty()) {
            str += " kaflayer=\""+this.kaflayer+"\"";
        }
        str += ">\t";
        if (elementFirstIds.size()==0) {
            str += "<elementFirstIds";
            if (!this.elementFirstLabel.isEmpty()) {
                str += " label=\""+this.elementFirstLabel +"\"";
            }
            if (!this.elementFirstComment.isEmpty()) {
                str += " comment=\""+this.elementFirstComment.replaceAll("\"","")+"\"";
            }
            str += "/>\t";
        }
        else {
            str += "<elementFirstIds";
            if (!this.elementFirstLabel.isEmpty()) {
                str += " label=\""+this.elementFirstLabel +"\"";
            }
            if (!this.elementFirstComment.isEmpty()) {
                str += " comment=\""+this.elementFirstComment.replaceAll("\"","")+"\"";
            }
            str += "> ";
            for (int i = 0; i < elementFirstIds.size(); i++) {
                String s = elementFirstIds.get(i);
                str += "<elementFirst id=\""+s+"\"/> ";
            }
            str += "</elementFirstIds>\t";
        }
        if (elementSecondIds.size()==0) {
            str += "<elementSecondIds";
            if (!this.elementSecondLabel.isEmpty()) {
                str += " label=\""+this.elementSecondLabel +"\"";
            }
            if (!this.elementSecondComment.isEmpty()) {
                str += " comment=\""+this.elementSecondComment.replaceAll("\"","")+"\"";
            }
            str += "/>\t";
        }
        else {
            str += "<elementSecondIds";
            if (!this.elementSecondLabel.isEmpty()) {
                str += " label=\""+this.elementSecondLabel +"\"";
            }
            if (!this.elementSecondComment.isEmpty()) {
                str += " comment=\""+this.elementSecondComment.replaceAll("\"","")+"\"";
            }
            str += "> ";
            for (int i = 0; i < elementSecondIds.size(); i++) {
                String s = elementSecondIds.get(i);
                str += "<elementSecond id=\""+s+"\"/> ";
            }
            str += "</elementSecondIds>\t";
        }
        str += "</triple>\n";
        return str;
    }

    public boolean isEqual (Triple oTriple) {
        if (!oTriple.getRelation().equals(this.getRelation())) {
           return false;
        }
        if ((!oTriple.getElementFirstLabel().equals(this.getElementFirstLabel()))) {
            return false;
        }
        if (!oTriple.getElementSecondLabel().equals(this.getElementSecondLabel())) {
            return false;
        }
        for (int i = 0; i < elementFirstIds.size(); i++) {
            String s = elementFirstIds.get(i);
            if (!oTriple.getElementFirstIds().contains(s)) {
                return false;
            }
        }
        for (int i = 0; i < elementSecondIds.size(); i++) {
            String s = elementSecondIds.get(i);
            if ((!oTriple.getElementSecondIds().contains(s))) {
                return false;
            }
        }
        for (int i = 0; i < oTriple.getElementFirstIds().size(); i++) {
            String s = oTriple.getElementFirstIds().get(i);
            if (!elementFirstIds.contains(s)) {
                return false;
            }
        }
        for (int i = 0; i < oTriple.getElementSecondIds().size(); i++) {
            String s = oTriple.getElementSecondIds().get(i);
            if (!elementSecondIds.contains(s)) {
                return false;
            }
        }
        return true;
    }
}
