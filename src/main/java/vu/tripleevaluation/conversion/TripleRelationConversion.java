package vu.tripleevaluation.conversion;

import vu.tripleevaluation.io.TripleSaxParser;
import vu.tripleevaluation.objects.Triple;

import java.io.*;
import java.util.HashMap;

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
public class TripleRelationConversion {

    static public HashMap<String, String> readConversionFile(String filePath) {
        HashMap<String, String> conversionMap = new HashMap<String, String>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String [] fields = inputLine.split("\t");
                    if (fields.length==2) {
                        String rel1 = fields[0].trim();
                        String rel2 = fields[1].trim();
                        conversionMap.put(rel1, rel2);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return conversionMap;
    }

    static public void main (String[] args) {

        String tripleFilePath = "";
        String conversionFile = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--triples")) {
                if (i+1<args.length) {
                    tripleFilePath = args[i+1];
                }
                else {
                    System.out.println("NO FILE PROVIDED AS TRIPLES!");
                }
            }
            else if (arg.equalsIgnoreCase("--relation-mapping")) {
                if (i+1<args.length) {
                    conversionFile = args[i+1];
                }
                else {
                    System.out.println("NO FILE PROVIDED AS RELATION MAPPING!");
                }
            }
        }
        if (!tripleFilePath.isEmpty() && !conversionFile.isEmpty()) {
            HashMap<String, String> conversionMap = readConversionFile(conversionFile);
            TripleSaxParser parser = new TripleSaxParser();
            parser.parseFile(tripleFilePath);
            try {
                String outputFile = tripleFilePath+".conv.trp";
                FileOutputStream fos = new FileOutputStream (outputFile);
                String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
                str += "<triples>\n";
                fos.write(str.getBytes());
                for (int i = 0; i < parser.data.size(); i++) {
                    Triple triple = parser.data.get(i);
                    if (conversionMap.containsKey(triple.getRelation())) {
                        String newRelation = conversionMap.get(triple.getRelation());
                        triple.setRelation(newRelation);
                    }
                    str = triple.toString();
                    fos.write(str.getBytes());
                }
                str = "</triples>\n";
                fos.write(str.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
