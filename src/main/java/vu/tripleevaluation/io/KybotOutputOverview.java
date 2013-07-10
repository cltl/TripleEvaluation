package vu.tripleevaluation.io;

import eu.kyotoproject.kybotoutput.parser.KybotOutputSaxParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class KybotOutputOverview {

        public static void main(String [] args) {
           File file = new File (args[0]);
           int threshold = Integer.parseInt(args[1]);
           KybotOutputSaxParser parser = new KybotOutputSaxParser();
           parser.parseFile(file, threshold);
           parser.mergeFacts();
            /// dumping facts in XML abstracting from instance level
            /// comment this out
            try {
                FileOutputStream out = new FileOutputStream (file.getAbsoluteFile()+"overview.xml");
                parser.serializeMergedFacts(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

}
