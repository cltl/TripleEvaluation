package vu.tripleevaluation.objects;

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
public class RdfObject {

/*
    <rdf:Description rdf:about="http://www.kyoto-project.eu/facts#e91">

  <kyoto:event>
    <rdf:Description rdf:about="http://www.kyoto-project.eu/events#w14026">
      <rdfs:label>eat</rdfs:label>
    </rdf:Description>
  </kyoto:event>
  <kyoto:relation>
      <rdf:Description rdf:about="http://www.kyoto-project.eu/relations#done-by">
	<rdfs:label>done-by</rdfs:label>
      </rdf:Description>
  </kyoto:relation>
  <kyoto:participant>
    <rdf:Description rdf:about="http://www.kyoto-project.eu/participants#w14041">
	<rdfs:label>man</rdfs:label>
      </rdf:Description>
  </kyoto:participant>
</rdf:Description>

     */
    final String aboutParticipant = "http://www.kyoto-project.eu/participants#";
    final String aboutEvent = "http://www.kyoto-project.eu/events#";
    final String aboutRelation = "http://www.kyoto-project.eu/relations#";
    final String aboutFact = "http://www.kyoto-project.eu/facts#";
    String objectAbout;
    String eventAbout;
    String eventLabel;
    String relationAbout;
    String relationLabel;
    String participantAbout;
    String participantLabel;

    public RdfObject() {
        init();
    }

    public RdfObject(Triple triple) {
        init();
        this.setObjectAbout(triple.getTripleId());
        this.setEventAbout(triple.getElementFirstIds().get(0));
        this.setParticipantAbout(triple.getElementSecondIds().get(0));
        this.setRelationAbout(triple.getRelation());
        this.setEventLabel(triple.getElementFirstComment());
        this.setParticipantLabel(triple.getElementSecondComment());
        this.setRelationLabel(triple.getRelation());
    }

    void init() {
        this.objectAbout = "";
        this.eventAbout = "";
        this.eventLabel = "";
        this.relationAbout = "";
        this.relationLabel = "";
        this.participantAbout = "";
        this.participantLabel = "";
    }

    public String getObjectAbout() {
        return objectAbout;
    }

    public void setObjectAbout(String objectAbout) {
        this.objectAbout = aboutFact+objectAbout;
    }

    public String getEventAbout() {
        return eventAbout;
    }

    public void setEventAbout(String eventAbout) {
        this.eventAbout = aboutEvent+eventAbout;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public void setEventLabel(String eventLabel) {
        this.eventLabel = eventLabel;
    }

    public String getRelationAbout() {
        return relationAbout;
    }

    public void setRelationAbout(String relationAbout) {
        this.relationAbout = aboutRelation+relationAbout;
    }

    public String getRelationLabel() {
        return relationLabel;
    }

    public void setRelationLabel(String relationLabel) {
        this.relationLabel = relationLabel;
    }

    public String getParticipantAbout() {
        return participantAbout;
    }

    public void setParticipantAbout(String participantAbout) {
        this.participantAbout = aboutParticipant+participantAbout;
    }

    public String getParticipantLabel() {
        return participantLabel;
    }

    public void setParticipantLabel(String participantLabel) {
        this.participantLabel = participantLabel;
    }

    public String toXmlString () {
        String str = "<rdf:Description rdf:about=\""+objectAbout+"\">\n";

        str += "\t<kyoto:event>\n";
        str += "\t\t<rdf:Description rdf:about=\""+this.eventAbout+"\">\n";
        str += "\t\t\t<rdfs:label rdf:about=\""+this.eventLabel+"\"/>\n";
        str += "\t\t</rdf:Description>\n";
        str += "\t</kyoto:event>\n";

        str += "\t<kyoto:relation>\n";
        str += "\t\t<rdf:Description rdf:about=\""+this.relationAbout+"\">\n";
        str += "\t\t\t<rdfs:label rdf:about=\""+this.relationLabel+"\"/>\n";
        str += "\t\t</rdf:Description>\n";
        str += "\t</kyoto:relation>\n";

        str += "\t<kyoto:participant>\n";
        str += "\t\t<rdf:Description rdf:about=\""+this.participantAbout+"\">\n";
        str += "\t\t\t<rdfs:label rdf:about=\""+this.participantLabel+"\"/>\n";
        str += "\t\t</rdf:Description>\n";
        str += "\t</kyoto:participant>\n";

        str += "</rdf:Description>\n";
        return str;
    }
}
