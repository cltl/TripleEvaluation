package vu.tripleevaluation.kyoto;

import vu.tripleevaluation.io.TripleSaxParser;
import vu.tripleevaluation.objects.Triple;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kyoto
 * Date: 3/3/11
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveTriplesWithFirstAndSecondElementIdMatches {




    static public void main (String [] args) {
        String TripleFilePath = args[0];
        TripleSaxParser parser = new TripleSaxParser();
        parser.parseFile(TripleFilePath);
        System.out.println("parser.data.size() = " + parser.data.size());
        parser.removeTriplesWithElementFirstEqualElementSecond();
        System.out.println("parser.data.size() = " + parser.data.size());
        try {
            FileOutputStream fos = new FileOutputStream(TripleFilePath+".reduced.trp");
            String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<triples>";
            fos.write(str.getBytes());
            for (int i = 0; i < parser.data.size(); i++) {
                Triple Triple = parser.data.get(i);
                fos.write(Triple.toString().getBytes());
            }
            str = "</Triples>\n";
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
