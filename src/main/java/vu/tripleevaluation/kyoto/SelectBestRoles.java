package vu.tripleevaluation.kyoto;

import eu.kyotoproject.kybotoutput.objects.KybotEvent;
import eu.kyotoproject.kybotoutput.objects.KybotRole;
import eu.kyotoproject.kybotoutput.parser.KybotOutputSaxParser;
import eu.kyotoproject.kybotoutput.parser.KybotOutputSaxParserDocCount;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
public class SelectBestRoles {

    static public HashMap readProfileScores (String filePath) {
        HashMap profileMap = new HashMap<String, Integer> ();
        try {
            if (new File(filePath).exists() ) {
                    FileInputStream fis = new FileInputStream(filePath);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader in = new BufferedReader(isr);
                    String inputLine = "";
                    while (in.ready()&&(inputLine = in.readLine()) != null) {
                        if (inputLine.trim().length()>0) {
                            String [] fields = inputLine.split("\t");
                            if (fields.length==10) {
                                String profile = fields[0];
                                Integer conf = Integer.parseInt(fields[7].trim());
                               // System.out.println("profile = " + profile+"\t"+conf);
                                profileMap.put(profile, conf);
                            }
                        }
                    }
            }
            else {
                System.out.println("Could not find filePath = " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return profileMap;
    }


    static int getTopScoreFromProfiles (HashMap<String, Integer> profileScores, String profileString) {
        int topScore = 0;
        String [] profiles = profileString.split(",");
        for (int i = 0; i < profiles.length; i++) {
            String profile = profiles[i];
            if (profileScores.containsKey(profile)) {
                Integer conf = profileScores.get(profile);
                if (conf.intValue()>topScore) {
                    topScore = conf.intValue();
                }
            }
        }
        return topScore;
    }

    static int compareProfiles (HashMap<String, Integer> profileScores, KybotRole role1, KybotRole role2) {
        int match = -1;
        int r1TopScore = getTopScoreFromProfiles(profileScores, role1.getProfileId());
        int r2TopScore = getTopScoreFromProfiles(profileScores, role2.getProfileId());
        if (r1TopScore<r2TopScore) {
            match = 2;
        }
        else if (r2TopScore<r1TopScore) {
            match = 1;
        }
        else {
            match = 0;
        }
        return match;
    }



    static int compareSynsetRanks (KybotRole role1, KybotRole role2) {
        int match = -1;
        if (role1.getSynsetScore()<role2.getSynsetScore()) {
            match = 2;
        }
        else if (role2.getSynsetScore()<role1.getSynsetScore()) {
            match = 1;
        }
        else {
            match = 0;
        }
        return match;
    }

    static public class ProfileStat {
        String profile;
        int count;

        public ProfileStat(String profile, int count) {
            this.profile = profile;
            this.count = count;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void incrementCount() {
            this.count++;
        }
    }

    static void addToMatrix (HashMap<String, ArrayList<ProfileStat>> matrix, String profile1, String profile2) {
        String [] profiles1 = profile1.split(",");
        String [] profiles2 = profile2.split(",");
        for (int i = 0; i < profiles1.length; i++) {
            String p1 = profiles1[i];
            if (matrix.containsKey(p1)) {
                ArrayList<ProfileStat> stats = matrix.get(p1);
                boolean match =false;
                for (int j = 0; j < profiles2.length; j++) {
                    String p2 = profiles2[j];
                    for (int k = 0; k < stats.size(); k++) {
                        ProfileStat profileStat = stats.get(k);
                        if (profileStat.getProfile().equals(p2)) {
                            profileStat.incrementCount();
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        ProfileStat profileStat = new ProfileStat(p2, 1);
                        stats.add(profileStat);
                    }
                }
                matrix.put(p1, stats);
            }
            else {
                ArrayList<ProfileStat> stats =new ArrayList<ProfileStat>();
                for (int j = 0; j < profiles2.length; j++) {
                    String s = profiles2[j];
                    ProfileStat profileStat = new ProfileStat(s, 1);
                    stats.add(profileStat);
                }
                matrix.put(p1, stats);
            }
        }
    }

    static void processKybotOutputFile (String kybotOutputFile, String bestRoleOutputFile, int profileScore, HashMap profileScores) {
        try {
            int threshold = 0;
            HashMap<String, ArrayList<ProfileStat>> profileConfusionMatrix= new HashMap<String, ArrayList<ProfileStat>>();
            FileOutputStream fos = new FileOutputStream (bestRoleOutputFile+".best_role."+profileScore+".xml");
            FileOutputStream log = new FileOutputStream (bestRoleOutputFile+".best_role."+profileScore+".log");
            KybotOutputSaxParser factParser = new KybotOutputSaxParser ();
            factParser.useShortName = true; /// required to able to find term references in the original KAF
            factParser.parseFile(kybotOutputFile, threshold);
            System.out.println("PARSED FILE");
            int nOrgRoles = 0;
            int nNewRoles = 0;
            String str = "<?xml version=\"1.0\"?>\n" +
                    "<kybotOut>\n";
            fos.write(str.getBytes());
            Set keySet = factParser.idFacts.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                KybotEvent fact = factParser.idFacts.get(key);
                nOrgRoles += fact.getRoles().size();
                ArrayList<KybotRole> newRoles = new ArrayList<KybotRole> ();
                for (int j = 0; j < fact.getRoles().size(); j++) {
                    KybotRole role = fact.getRoles().get(j);
                    boolean bettermatch = false;
                    for (int k = 0; k < fact.getRoles().size(); k++) {
                        if (k!=j) {
                            /// skip the self
                            KybotRole kybotRole = fact.getRoles().get(k);
                            //// if the roles apply to the same target
                            if (role.getTarget().equals(kybotRole.getTarget())) {
                                //str = fact.toXmlWithoutRolesString(); /// used for logging
                                //// if they propose different relations
                                if (!role.getRtype().equals(kybotRole.getRtype())) {
                                    //addToMatrix(profileConfusionMatrix, role.getProfileId()+":"+role.getRtype(), kybotRole.getProfileId()+":"+kybotRole.getRtype());
                                    addToMatrix(profileConfusionMatrix, role.getProfileId(), kybotRole.getProfileId());
                                    int comp = compareProfiles(profileScores, role, kybotRole);
/*
                                    str+= "\t"+role.toXmlString();
                                    str += "\t"+kybotRole.toXmlString();
                                    str += "\tcomp="+comp+"\n";
                                    log.write(str.getBytes());
*/                                  if (comp ==0) {
                                        comp = compareSynsetRanks (role, kybotRole);
                                    }
                                    if (comp == 2) {
                                       bettermatch = true;
                                       break;
                                    }
                                }
                            }
                            else {
                                ////they do not compete
                            }
                        }
                    }
                    if (!bettermatch) {
                        if (getTopScoreFromProfiles(profileScores, role.getProfileId())>=profileScore) {
                            newRoles.add(role);
                        }
                    }
                    else {
                        /// skipping this role since there is a better one!
                    }
                }
                nNewRoles += newRoles.size();
                fact.setRoles(newRoles);
                fos.write(fact.toXmlString().getBytes());
/*
                fos.write(fact.toXmlWithoutRolesString().getBytes());
                for (int j = 0; j < newRoles.size(); j++) {
                    KybotRole kybotRole = newRoles.get(j);
                    fos.write(kybotRole.toXmlString().getBytes());
                }
*/
            }
            str = "</kybotOut>\n";
            fos.write(str.getBytes());
            fos.close();
            System.out.println("FINISHED WRITING OUTPUT");
            str = "Nr of original roles="+nOrgRoles+"\n";
            str += "Nr of new roles="+nNewRoles+"\n";
            str += "Confusion matrix\n";
            keySet = profileConfusionMatrix.keySet();
            keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String profile = key;
                int idx =  key.indexOf(":");
                if (idx>-1) {
                    profile = key.substring(0, idx);
                }
                str += key+":"+getTopScoreFromProfiles(profileScores, profile)+"\n";
                ArrayList<ProfileStat> stats = profileConfusionMatrix.get(key);
                for (int i = 0; i < stats.size(); i++) {
                    ProfileStat profileStat = stats.get(i);
                    profile = profileStat.getProfile();
                    idx = profile.indexOf(":");
                    if (idx>-1) {
                        profile = profile.substring(0, idx);
                    }
                    str += "\t"+profileStat.getProfile()+":"+getTopScoreFromProfiles(profileScores,profile)+"\t"+profileStat.getCount()+"\n";
                }
            }
            log.write(str.getBytes());
            log.close();
            System.out.println("FINISHED WRITING LOG");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


        static public void ReadFileyDoc(String fileName) {
        if (new File (fileName).exists() ) {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (inputLine.trim().length()>0) {
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static void processKybotOutputFileDocByDoc (String kybotOutputFile, String bestRoleOutputFile, int profileScore, HashMap profileScores) {
        try {
            int threshold = 0;
            HashMap<String, ArrayList<ProfileStat>> profileConfusionMatrix= new HashMap<String, ArrayList<ProfileStat>>();
            FileOutputStream fos = new FileOutputStream (bestRoleOutputFile+".best_role."+profileScore+".xml");
            FileOutputStream log = new FileOutputStream (bestRoleOutputFile+".best_role."+profileScore+".log");
            KybotOutputSaxParser factParser = new KybotOutputSaxParser ();
            KybotOutputSaxParserDocCount counter = new KybotOutputSaxParserDocCount ();
            String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<kybotOut>\n";
            fos.write(str.getBytes());
            try {
                String docString = "";
                boolean docLine = false;
                FileInputStream fis = new FileInputStream(kybotOutputFile);
                InputStreamReader isr = new InputStreamReader(fis);
                String defaultEncoding = isr.getEncoding();
                System.out.println("defaultEncoding = " + defaultEncoding);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                int nDocs = 0;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (inputLine.indexOf("<doc")>-1) {
                        docString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+inputLine+"\n";
                        docLine = true;
                    }
                    else if (docLine) {
                        docString += inputLine+"\n";
                        if (inputLine.indexOf("/doc>")>-1) {
                            /// processDoc
                            factParser.useShortName = false;
                            factParser.init();
                            factParser.parseString(docString, threshold);
                            str = "<doc shortname=\""+factParser.getMatchingShortName()+"\">\n";
                            nDocs++;
                            if (nDocs%50==0) {
                                System.out.println("nDocs = " + nDocs);
                                System.out.println(str);
                            }
                            fos.write(str.getBytes());
                            outputBestRoles(factParser,profileConfusionMatrix, profileScores, profileScore,fos);
                            str  = "</doc>\n";
                            fos.write(str.getBytes());
                            docString = "";
                            docLine = false;
                        }
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
/*
            int nDocs = counter.parseFile(kybotOutputFile);
            System.out.println("nDocs = " + nDocs);
            for (int i = 0; i < nDocs; i++) {
                System.out.println("i = " + i);
                factParser.useShortName = false;
                factParser.init();
                factParser.docNumber = i;
                factParser.parseFile(kybotOutputFile);
                str = "<doc shortname=\""+factParser.getMatchingShortName()+"\">\n";
                fos.write(str.getBytes());
                System.out.println(str);
                outputBestRoles(factParser,profileConfusionMatrix, profileScores, profileScore,fos);
                str  = "</doc>\n";
                fos.write(str.getBytes());
            }
*/
            str = "</kybotOut>\n";
            fos.write(str.getBytes());
            fos.close();
            str = "Confusion matrix\n";
            Set keySet = profileConfusionMatrix.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String profile = key;
                int idx =  key.indexOf(":");
                if (idx>-1) {
                    profile = key.substring(0, idx);
                }
                str += key+":"+getTopScoreFromProfiles(profileScores, profile)+"\n";
                ArrayList<ProfileStat> stats = profileConfusionMatrix.get(key);
                for (int i = 0; i < stats.size(); i++) {
                    ProfileStat profileStat = stats.get(i);
                    profile = profileStat.getProfile();
                    idx = profile.indexOf(":");
                    if (idx>-1) {
                        profile = profile.substring(0, idx);
                    }
                    str += "\t"+profileStat.getProfile()+":"+getTopScoreFromProfiles(profileScores,profile)+"\t"+profileStat.getCount()+"\n";
                }
            }
            log.write(str.getBytes());
            log.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    static void outputBestRoles (KybotOutputSaxParser factParser,
                                 HashMap<String, ArrayList<ProfileStat>> profileConfusionMatrix,
                                 HashMap profileScores,
                                 int profileScore,
                                 FileOutputStream fos) throws IOException {
        int nOrgRoles = 0;
        int nNewRoles = 0;
        Set keySet = factParser.idFacts.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            KybotEvent fact = factParser.idFacts.get(key);
            nOrgRoles += fact.getRoles().size();
            ArrayList<KybotRole> newRoles = new ArrayList<KybotRole> ();
            for (int j = 0; j < fact.getRoles().size(); j++) {
                KybotRole role = fact.getRoles().get(j);
                boolean bettermatch = false;
                for (int k = 0; k < fact.getRoles().size(); k++) {
                    if (k!=j) {
                        /// skip the self
                        KybotRole kybotRole = fact.getRoles().get(k);
                        //// if the roles apply to the same target
                        if (role.getTarget().equals(kybotRole.getTarget())) {
                            //str = fact.toXmlWithoutRolesString(); /// used for logging
                            //// if they propose different relations
                            if (!role.getRtype().equals(kybotRole.getRtype())) {
                                //addToMatrix(profileConfusionMatrix, role.getProfileId()+":"+role.getRtype(), kybotRole.getProfileId()+":"+kybotRole.getRtype());
                                addToMatrix(profileConfusionMatrix, role.getProfileId(), kybotRole.getProfileId());
                                int comp = compareProfiles(profileScores, role, kybotRole);
/*
                                    str+= "\t"+role.toXmlString();
                                    str += "\t"+kybotRole.toXmlString();
                                    str += "\tcomp="+comp+"\n";
                                    log.write(str.getBytes());
*/                                  if (comp ==0) {
                                    comp = compareSynsetRanks (role, kybotRole);
                                }
                                if (comp == 2) {
                                   bettermatch = true;
                                   break;
                                }
                            }
                        }
                        else {
                            ////they do not compete
                        }
                    }
                }
                if (!bettermatch) {
                    if (getTopScoreFromProfiles(profileScores, role.getProfileId())>=profileScore) {
                        newRoles.add(role);
                    }
                }
                else {
                    /// skipping this role since there is a better one!
                }
            }
            nNewRoles += newRoles.size();
            fact.setRoles(newRoles);
            fos.write(fact.toXmlString().getBytes());
/*
                fos.write(fact.toXmlWithoutRolesString().getBytes());
                for (int j = 0; j < newRoles.size(); j++) {
                    KybotRole kybotRole = newRoles.get(j);
                    fos.write(kybotRole.toXmlString().getBytes());
                }
*/
        }
    }

    static public void main (String [] args) {
        String fileWithProfileScores = args[0];
        HashMap profileScores = readProfileScores(fileWithProfileScores);
        System.out.println("profileScores.size() = " + profileScores.size());
        String kybotOutputFile = args[1];
        String bestRoleOutputFile = args[1];
        int idx = bestRoleOutputFile.lastIndexOf(".");
        if (idx>-1) {
            bestRoleOutputFile = bestRoleOutputFile.substring(0, idx);
        }
        processKybotOutputFile(kybotOutputFile, bestRoleOutputFile, 0, profileScores);
        processKybotOutputFile(kybotOutputFile, bestRoleOutputFile, 1, profileScores);
        processKybotOutputFile(kybotOutputFile, bestRoleOutputFile, 5, profileScores);
        processKybotOutputFile(kybotOutputFile, bestRoleOutputFile, 10, profileScores);
      //  processKybotOutputFileDocByDoc(kybotOutputFile, bestRoleOutputFile, 10, profileScores);
        processKybotOutputFile(kybotOutputFile, bestRoleOutputFile, 25, profileScores);
        processKybotOutputFile(kybotOutputFile, bestRoleOutputFile, 50, profileScores);
        processKybotOutputFile(kybotOutputFile, bestRoleOutputFile, 75, profileScores);
    }
}
