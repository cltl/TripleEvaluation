package vu.tripleevaluation.objects;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 11/19/12
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafTripleElement {

    String kafLayerName;
    String kafElementName;
    String kafAttribute;
    String tripleElementName;

    public KafTripleElement() {
        this.kafLayerName = "";
        this.kafAttribute = "";
        this.kafElementName = "";
        this.tripleElementName = "";
    }

    public KafTripleElement(String kafLayerName, String kafAttribute, String kafElementName, String tripleElementName) {
        this.kafLayerName = kafLayerName;
        this.kafAttribute = kafAttribute;
        this.kafElementName = kafElementName;
        this.tripleElementName = tripleElementName;
    }

    public String getKafLayerName() {
        return kafLayerName;
    }

    public void setKafLayerName(String kafLayerName) {
        this.kafLayerName = kafLayerName;
    }

    public String getKafAttribute() {
        return kafAttribute;
    }

    public void setKafAttribute(String kafAttribute) {
        this.kafAttribute = kafAttribute;
    }

    public String getKafElementName() {
        return kafElementName;
    }

    public void setKafElementName(String kafElementName) {
        this.kafElementName = kafElementName;
    }

    public String getTripleElementName() {
        return tripleElementName;
    }

    public void setTripleElementName(String tripleElementName) {
        this.tripleElementName = tripleElementName;
    }
}
