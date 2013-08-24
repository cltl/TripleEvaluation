package vu.tripleevaluation.evaluation;

import vu.tripleevaluation.objects.Triple;
import vu.tripleevaluation.statistics.Statistics;
import vu.tripleevaluation.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 12/18/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluateTripleFolders {

    static HashMap<String, Statistics> totalRelationMap = new HashMap<String, Statistics>();
    static HashMap<String, HashMap<String, Integer>> relationConfusionMatrix = new HashMap<String, HashMap<String, Integer>> ();
    static HashMap<String, Integer> wrongRelationMap = new HashMap<String, Integer> ();
    static HashMap<String, Integer> missedRelationMap = new HashMap<String, Integer> ();
    static ArrayList<String> relationMismatchLog;
    static ArrayList<Triple> notCoveredTriples;
    static ArrayList<Triple> wrongRelationTriples;

    static ArrayList<String> relationFilter = new ArrayList<String>();
    static int nGoldTriples;
    static int nSystemTriples;
    static double dExactIdExactRelationPrecision;
    static double dExactIdExactRelationRecall;
    static double dPartialIdExactRelationPrecision;
    static double dPartialIdExactRelationRecall;
    static double dPartialIdExactRelationFmeasure;
    static double dExactIdPrecision;
    static double dExactIdRecall;
    static double dPartialIdPrecision;
    static double dPartialIdRecall;
    static int nAverageFirstElementIdRangeGold;
    static int nAverageSecondElementIdRangeGold;
    static int nAverageFirstElementIdRangeSystem;
    static int nAverageSecondElementIdRangeSystem;

    static int nUniqueFirstElementsGold;
    static int nUniqueSecondElementsGold;
    static int nUniqueFirstElementsSystem;
    static int nUniqueSecondElementsSystem;

    static int nCorrectPartialIdExactRelationTriples;
    static int nCorrectExactIdTriples;
    static int nCorrectExactIdExactRelationTriples;
    static int nCorrectPartialIdTriples;

    static int nUniquePartialIdExactRelationTriples;
    static int nUniqueExactIdTriples;
    static int nUniqueExactIdExactRelationTriples;
    static int nUniquePartialIdTriples;
    static int nUniqueSystemRelationTriples;
    static int nameSuffixLength;

    static void initGlobals () {
        nameSuffixLength = 0;
        relationConfusionMatrix = new HashMap<String, HashMap<String, Integer>> ();
        totalRelationMap = new HashMap<String, Statistics>();
        wrongRelationMap = new HashMap<String, Integer> ();
        missedRelationMap = new HashMap<String, Integer> ();
        relationMismatchLog = new ArrayList<String>();
        notCoveredTriples = new ArrayList<Triple> ();
        wrongRelationTriples = new ArrayList<Triple> ();

        relationFilter = new ArrayList<String>();
        nGoldTriples = 0;
        nSystemTriples = 0;
        dExactIdExactRelationPrecision = 0;
        dExactIdExactRelationRecall = 0;
        dPartialIdExactRelationPrecision = 0;
        dPartialIdExactRelationRecall = 0;
        dPartialIdExactRelationFmeasure = 0;
        dExactIdPrecision = 0;
        dExactIdRecall = 0;
        dPartialIdPrecision = 0;
        dPartialIdRecall = 0;

        nUniqueFirstElementsGold = 0;
        nUniqueSecondElementsGold = 0;
        nUniqueFirstElementsSystem = 0;
        nUniqueSecondElementsSystem = 0;

        nAverageFirstElementIdRangeGold = 0;
        nAverageSecondElementIdRangeGold = 0;
        nAverageFirstElementIdRangeSystem = 0;
        nAverageSecondElementIdRangeSystem = 0;

        nCorrectPartialIdExactRelationTriples = 0;
        nCorrectExactIdTriples = 0;
        nCorrectExactIdExactRelationTriples = 0;
        nCorrectPartialIdTriples = 0;

        nUniquePartialIdExactRelationTriples = 0;
        nUniqueExactIdTriples = 0;
        nUniqueExactIdExactRelationTriples = 0;
        nUniquePartialIdTriples = 0;
        nUniqueSystemRelationTriples = 0;
    }

    static void updateTotals (EvaluateTriplesDebug evaluation) {
        nGoldTriples += evaluation.getnGoldTriples();
        nSystemTriples += evaluation.getnSystemTriples();
        dExactIdExactRelationPrecision += evaluation.getdExactIdExactRelationPrecision();
        dExactIdExactRelationRecall += evaluation.getdExactIdExactRelationRecall();
        dPartialIdExactRelationPrecision += evaluation.getdPartialIdExactRelationPrecision();
        dPartialIdExactRelationRecall += evaluation.getdParialIdExactRelationRecall();
        dExactIdPrecision += evaluation.getdExactIdPrecision();
        dExactIdRecall += evaluation.getdExactIdRecall();
        dPartialIdPrecision += evaluation.getdPartialIdPrecision();
        dPartialIdRecall += evaluation.getdPartialIdRecall();

        nUniqueFirstElementsGold += evaluation.goldParser.nUniqueElementsFirst();
        nUniqueSecondElementsGold += evaluation.goldParser.nUniqueElementsSecond();
        nUniqueFirstElementsSystem += evaluation.systemParser.nUniqueElementsFirst();
        nUniqueSecondElementsSystem += evaluation.systemParser.nUniqueElementsSecond();


        nAverageFirstElementIdRangeGold += evaluation.goldParser.getAverageElementFirstIdRange();
        nAverageSecondElementIdRangeGold += evaluation.goldParser.getAverageElementSecondIdRange();
        nAverageFirstElementIdRangeSystem += evaluation.systemParser.getAverageElementFirstIdRange();
        nAverageSecondElementIdRangeSystem += evaluation.systemParser.getAverageElementSecondIdRange();

        nCorrectPartialIdExactRelationTriples += evaluation.getCorrectPartialIdExactRelationTriples().size();
        nCorrectExactIdTriples += evaluation.getCorrectExactIdTriples().size();
        nCorrectExactIdExactRelationTriples += evaluation.getCorrectExactIdExactRelationTriples().size();
        nCorrectPartialIdTriples += evaluation.getCorrectPartialIdTriples().size();

        nUniquePartialIdExactRelationTriples += evaluation.getUniquePartialIdExactRelationTriples().size();
        nUniqueExactIdTriples += evaluation.getUniqueExactIdTriples().size();
        nUniqueExactIdExactRelationTriples += evaluation.getUniqueExactIdExactRelationTriples().size();
        nUniquePartialIdTriples += evaluation.getUniquePartialIdTriples().size();
        nUniqueSystemRelationTriples += evaluation.getUniqueSystemRelationTriples().size();

    }

    static void updateTripleErrorLog (EvaluateTriplesDebug evaluation) {
        for (int i = 0; i < evaluation.notCoveredTriples.size(); i++) {
            Triple Triple = evaluation.notCoveredTriples.get(i);
            Triple.setProfileId(evaluation.goldParser.fileName);
            notCoveredTriples.add(Triple);
        }
        for (int i = 0; i < evaluation.wrongRelationTriples.size(); i++) {
            Triple Triple = evaluation.wrongRelationTriples.get(i);
            Triple.setProfileId(evaluation.goldParser.fileName);
            wrongRelationTriples.add(Triple);
        }
    }


    static String outputErrorLog () {
        String str = "";
        str = "\n\nNot covered Triples:"+notCoveredTriples.size()+"\n";
        TreeSet set = Util.sortTriplesForRelation(notCoveredTriples);
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Triple Triple = (Triple) iterator.next();
            str += Triple.toTableRowString();
        }
        str += "\n\n";
        str += "Wrong Triples:"+wrongRelationTriples.size()+"\n";
        set = Util.sortTriplesForRelation(wrongRelationTriples);
        iterator = set.iterator();
        while (iterator.hasNext()) {
            Triple Triple = (Triple) iterator.next();
            str += Triple.toTableRowString();
        }
        return str;
    }

    static void updateConfusionMatrix(String systemFileName, EvaluateTriplesDebug evaluation) {
        Set keySet = evaluation.relationConfusionMatrix.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String localKey = (String) keys.next();
            HashMap<String, Integer> localMap = evaluation.relationConfusionMatrix.get(localKey);
            if (relationConfusionMatrix.containsKey(localKey)) {
                HashMap<String, Integer> overallMap = relationConfusionMatrix.get(localKey);
                Set localMapSet = localMap.keySet();
                Iterator localMapKeys = localMapSet.iterator();
                while (localMapKeys.hasNext()) {
                    String localMapKey = (String) localMapKeys.next();
                    Integer localCnt = localMap.get(localMapKey);
                    if (overallMap.containsKey(localMapKey)) {
                        Integer overallCount = overallMap.get(localMapKey);
                        overallCount += localCnt;
                        overallMap.put(localMapKey, overallCount);
                    }
                    else {
                        overallMap.put(localMapKey, localCnt);
                    }
                }
                /// we put the updated overallMap for the key back into  relationConfusionMatrix
                relationConfusionMatrix.put(localKey, overallMap);
            }
            else {
                /// we put the new key into the hasmap relationConfusionMatrix
                relationConfusionMatrix.put(localKey, localMap);
            }
        }
        for (int i = 0; i < evaluation.relationMismatchLog.size(); i++) {
            String s = systemFileName+":"+evaluation.relationMismatchLog.get(i);
            relationMismatchLog.add(s);
        }
    }

    static void updateTotalRelationMap(EvaluateTriplesDebug evaluation) {
        Set keySet = evaluation.getRelationMap().keySet();
        Iterator keys = keySet.iterator();
       // str += "Relation\tTotal gold\tProportion\tTotal system\tPartialIdExactRelation\tRecall\tPrecision\n";
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Statistics stat = evaluation.getRelationMap().get(key);
            if (totalRelationMap.containsKey(key)) {
                Statistics keyStats = totalRelationMap.get(key);
                keyStats.addStatistics(stat);
                totalRelationMap.put(key, keyStats);
            }
            else {
                totalRelationMap.put(key, stat);
            }
        }
    }


    static String outputTotalRelationMap() {
        String str = "";
        int relationCorrectPartial = 0;
        int relationGold = 0;
        int relationSystem = 0;
        str += "\nResults per relation\n";

        Set keySet = totalRelationMap.keySet();
        Iterator keys = keySet.iterator();
        str += "Relation\tTotal gold\tProportion gold\tTotal system\tProportion system\tPartialIdExactRelation\tRecall\tPrecision\n";
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Statistics stat = totalRelationMap.get(key);
            stat.totalSystem = stat.uniqueSystemTriples.size();
            relationCorrectPartial += stat.getCorrectPartialIdExactRelation();
            relationGold += stat.getTotalGold();
            relationSystem += stat.getTotalSystem();
            Double perc1 = 100*(double)stat.getTotalGold()/(double)nGoldTriples;
            Double perc2 = 100*(double)stat.getTotalSystem()/(double)nSystemTriples;
            str += key+stat.toStringPartialRelation(perc1.intValue(), perc2.intValue());
        }
        int totalPartialRecall = 0;
        int totalPartialPrecision = 0;
        if (relationGold>0) totalPartialRecall = (100*relationCorrectPartial)/relationGold;
        if (relationSystem>0) totalPartialPrecision = (100*relationCorrectPartial)/relationSystem;
        str += "Total\t"+relationGold+"\t"+"\t"+relationSystem+"\t"+"\t"+relationCorrectPartial+"\t"+totalPartialRecall+"\t"+totalPartialPrecision+"\n";
        return str;
    }

    static String outputConfusionMatrix() {
        String str = "";
        str = "\n\nRelation confusion matrix, system (rows) versus gold (columns) with partial ID matches:\n";
        Set keySet = relationConfusionMatrix.keySet();
        Iterator keys = keySet.iterator();
        TreeSet sorter1 = new TreeSet();
        TreeSet sorter2 = new TreeSet();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            sorter1.add(key);
            HashMap<String, Integer> map = relationConfusionMatrix.get(key);
            Set mapSet = map.keySet();
            Iterator mapKeys = mapSet.iterator();
            while (mapKeys.hasNext()) {
                String mapKey = (String) mapKeys.next();
                sorter2.add(mapKey);
            }
        }
        Iterator mapKeys = sorter2.iterator();
        while (mapKeys.hasNext()) {
            String mapKey = (String) mapKeys.next();
            str += "\t"+mapKey;
        }
        str += "\n";
        keys = sorter1.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            str += key;
            HashMap<String, Integer> map = relationConfusionMatrix.get(key);
            mapKeys = sorter2.iterator();
            while (mapKeys.hasNext()) {
                String mapKey = (String) mapKeys.next();
                if (map.containsKey(mapKey)) {
                    Integer cnt = map.get(mapKey);
                    str += "\t"+cnt.toString();
                }
                else {
                    str += "\t0";
                }
            }
            str += "\n";
        }
        str += "\n\nMismatches of relations, syntax: system-triple:gold-triple\n";
        for (int i = 0; i < relationMismatchLog.size(); i++) {
            String s = relationMismatchLog.get(i);
            str+=s+"\n";
        }
        return str;
    }

    static String outputTotalCounts () {
            String str = "\nPrecision and recall figures\n";
            str += "Gold standard"+"\n";
            str += "\tNr. of Triples\t"+nGoldTriples+"\n";
            str += "\tAverage nr. of first element ids\t"+(double) nAverageFirstElementIdRangeGold /nGoldTriples+"\n";
            str += "\tAverage nr. of second element ids\t"+(double) nAverageSecondElementIdRangeGold /nGoldTriples+"\n";
            str += "\tNumber of first elements represented in gold standard Triples\t"+ nUniqueFirstElementsGold +"\n";
            str += "\tNumber of second elements represented in gold standard Triples\t"+ nUniqueSecondElementsGold +"\n";
            str += "\n";

            str += "\nSystem file"+"\n";
            str += "\tNr. of Triples in range\t"+nSystemTriples+"\n";
            str += "\tAverage nr. of first element ids\t"+(double) nAverageFirstElementIdRangeSystem /nSystemTriples+"\n";
            str += "\tAverage nr. of second element ids\t"+(double) nAverageSecondElementIdRangeSystem /nSystemTriples+"\n";
            str += "\n";
            str += "\tNumber of first elements represented in system Triples\t"+ nUniqueFirstElementsSystem +"\n";
            str += "\tNumber of second element represented in system Triples\t"+ nUniqueSecondElementsSystem +"\n";

            str += "\n";

            dPartialIdExactRelationPrecision = (double)nCorrectPartialIdExactRelationTriples /(double)nUniquePartialIdExactRelationTriples;
            dPartialIdExactRelationRecall = (double)nCorrectPartialIdExactRelationTriples /(double)nGoldTriples;
            dPartialIdExactRelationFmeasure = (2*dPartialIdExactRelationPrecision* dPartialIdExactRelationRecall) /(dPartialIdExactRelationPrecision+ dPartialIdExactRelationRecall);

            str+= "\nPartial id, exact relation\n";
            str+= "Nr of Triples\tNr of unique Triples\tCorrect\tPrecision\tRecall\tFmeasure\n";
            str+= nSystemTriples+"\t"+ nUniquePartialIdExactRelationTriples+"\t"+ nCorrectPartialIdExactRelationTriples+"\t"+dPartialIdExactRelationPrecision+"\t"+ dPartialIdExactRelationRecall +"\t"+ dPartialIdExactRelationFmeasure +"\n";


            str += "\nPartial identifiers and same relation\n";
            str += "\tNr. correct\t"+ nCorrectPartialIdExactRelationTriples +"\n";
            str += "\tPrecision\t"+ dPartialIdExactRelationPrecision+"\n";
            str += "\tRecall\t"+ dPartialIdExactRelationRecall +"\n";

            dPartialIdPrecision = (double)nCorrectPartialIdTriples /(double)nUniquePartialIdTriples;
            dPartialIdRecall = (double)nCorrectPartialIdTriples /(double)nGoldTriples;
            str += "\n";
            str += "\nPartial identifiers and ignore relation\n";
            str += "\tNr. correct\t"+ nCorrectPartialIdTriples +"\n";
            str += "\tPrecision\t"+ dPartialIdPrecision+"\n";
            str += "\tRecall\t"+ dPartialIdRecall+"\n";

            dExactIdExactRelationPrecision = (double)nCorrectExactIdExactRelationTriples /(double)nUniqueExactIdExactRelationTriples;
            dExactIdExactRelationRecall = (double)nCorrectExactIdExactRelationTriples /(double)nGoldTriples;
            str += "\n";
            str += "\nExact identifiers and same relation\n";
            str += "\tNr. correct\t"+ nCorrectExactIdExactRelationTriples +"\n";
            str += "\tPrecision\t"+ dExactIdExactRelationPrecision+"\n";
            str += "\tRecall\t"+ dExactIdExactRelationRecall+"\n";

            dExactIdPrecision = (double)nCorrectExactIdTriples /(double)nUniquePartialIdTriples;
            dExactIdRecall = (double)nCorrectExactIdTriples /(double)nGoldTriples;
            str += "\n";
            str += "\nExact identifiers and ignore relation\n";
            str += "\tNr. correct\t"+ nCorrectExactIdTriples +"\n";
            str += "\tPrecision\t"+ dExactIdPrecision+"\n";
            str += "\tRecall\t"+ dExactIdRecall+"\n";

        return str;
    }
    static final String usage = "This program will compare a folder with system tripl files with a folder woth gold-standard triple files.\n" +
            "It will create a new folder in the system folder with an XSL file containing the evaluation.\nArguments and Options:\n"+
            "\t --gold-standard-triples\t<path to the folder with the gold-standard triples>\n"+
            "\t --system-triples\t<path to the folder with the system triples>\n"+
            "\t --key\t[OPTIONAL]<any string to name the evaluation result file\n"+
            "\t --ignore-file-suffix\t[OPTIONAL]<number of characters that are ignored to compare a gold-standard file with a system file\n" +
            "If left out only identical file names are compared, otherwise they need to match expect for specified substring length>\n"+
            "\t --ignore-element-second\t[OPTIONAL]<only the first element of the triples is considered>\n"+
            "\t --skip-time-and-location\t[OPTIONAL]<Time and location included in the KYOTO based triples are ignored>\n"+
            "\t --relation-filter\t<path to a text file listing the relations that need to be considered. This can be used to limit the evaluation to certain relations only. Each relation is listed on a separate line\n";

    static public void main (String [] args) {
        if (args.length==0) {
            System.out.println("usage = " + usage);
        }
        initGlobals();
        EvaluateTriplesDebug evaluation = new EvaluateTriplesDebug();
        String goldStandardTripleFolder = "";
        String systemTripleFolder = "";
        String relationFilterFile = "";
        String relationLabel = "";
        String goldKey = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--gold-standard-triples")) {
                if (i+1<args.length) {
                    goldStandardTripleFolder = args[i+1];
                }
                else {
                    System.out.println("NO FILE PROVIDED!");
                }
            }
            else if (arg.equals("--system-triples")) {
                if (i+1<args.length) {
                    systemTripleFolder = args[i+1];
                }
                else {
                    System.out.println("NO FILE PROVIDED!");
                }
            }
            else if (arg.equals("--key")) {
                if (i+1<args.length) {
                    goldKey = args[i+1];
                }
                else {
                    System.out.println("NO KEY PROVIDED!");
                }
            }
            else if (arg.equals("--ignore-file-suffix")) {
                if (i+1<args.length) {
                    try {
                        nameSuffixLength = Integer.parseInt(args[i+1]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                else {
                    System.out.println("NO KEY SUFFIX LENGTH!");
                }
            }
            else if (arg.equals("--ignore-element-second")) {
                evaluation.elementSecondMatch = false;
            }
            else if (arg.equals("--skip-time-and-location")) {
                evaluation.timeAndLocation = false;

            }
            else if (arg.equals("--relation-filter")) {
                if (i+1<args.length) {
                    relationFilterFile = args[i+1];
                    relationLabel = "-"+ new File(relationFilterFile).getName();
                    relationFilter = Util.ReadFileToArrayList (relationFilterFile);
                }
                else {
                    System.out.println("NO RELATION FILE PROVIDED!");
                }

            }
        }
        System.out.println("goldStandardTripleFolder = " + goldStandardTripleFolder);
        System.out.println("systemTripleFolder = " + systemTripleFolder);
        System.out.println("name suffix length = " + nameSuffixLength);
        Date date = new Date();
        System.out.println("relationFilterFile = " + relationFilterFile);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");
        File evaluationResultFolder = new File(systemTripleFolder+"/"+ "evaluation."+ft.format(date));
        if (!evaluationResultFolder.exists()) {
            evaluationResultFolder.mkdir();
        }
        allInSeparate(evaluation, goldStandardTripleFolder, systemTripleFolder, goldKey, date, evaluationResultFolder);
        try {
            System.out.println("evaluationResultFolder = " + evaluationResultFolder);
            FileOutputStream fos = new FileOutputStream(evaluationResultFolder+"/"+goldKey+".results"+relationLabel+"."+date.toString()+".xls");
            String str = "goldStandardTripleFolder\t" + goldStandardTripleFolder+"\n";
            str += "systemTripleFolder\t" + systemTripleFolder+"\n\n";
            ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
            str += "Date\t"+ft.format(date)+"\n\n";
            fos.write(str.getBytes());
            fos.write(outputTotalRelationMap().getBytes());
            fos.write(outputTotalCounts().getBytes());
            fos.write(outputConfusionMatrix().getBytes());
            fos.write(outputErrorLog().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    static void allInSeparate (EvaluateTriplesDebug evaluation,String goldStandardTripleFolder,String systemTripleFolder, String key, Date date, File evaluationResultFolder) {
     //   try {
            ArrayList<File> goldFiles = Util.makeFlatFileList(goldStandardTripleFolder, ".trp");
            ArrayList<File> systemFiles = Util.makeFlatFileList(systemTripleFolder, ".trp");
            for (int i = 0; i < goldFiles.size(); i++) {
                File goldFile = goldFiles.get(i);
                String goldFileKey = goldFile.getName();
                if (nameSuffixLength>0) {
                    goldFileKey = goldFileKey.substring(0, (goldFileKey.length()-nameSuffixLength));
                    /// e.g. .tag.trp
                }
               // System.out.println("goldFileKey = " + goldFileKey);
                for (int j = 0; j < systemFiles.size(); j++) {
                    File systemFile = systemFiles.get(j);
                    String systemFileKey = systemFile.getName();
                    if (nameSuffixLength>0) {
                        systemFileKey = systemFileKey.substring(0, (systemFileKey.length()-nameSuffixLength));
                    } /// e.g. ".kaf.trp"
                    if (goldFileKey.equals(systemFileKey)) {
                        System.out.println("goldStandardTripleFile = " + goldFile.getAbsolutePath());
                        System.out.println("systemTripleFile = " + systemFile.getAbsolutePath());
                       // evaluation.fos = new FileOutputStream(evaluationResultFolder+"/"+systemFile.getName()+"."+key+".results"+date.toString()+".xls");
                       // evaluation.log = new FileOutputStream(evaluationResultFolder+"/"+systemFile.getName()+"."+key+".results"+date.toString()+".log");
                        String str ="";
                        evaluation.compareTripleFiles(goldFile.getAbsolutePath(), systemFile.getAbsolutePath(),relationFilter);
                       // evaluation.fos.write(str.getBytes());
                        //str = evaluation.printResults(goldFile.getName(), systemFile.getName());
                        //evaluation.fos.write(str.getBytes());
                        //evaluation.fos.close();
                        //evaluation.log.close();
                        updateTotalRelationMap(evaluation);
                        updateTotals(evaluation);
                        updateConfusionMatrix(systemFile.getName(), evaluation);
                        updateTripleErrorLog(evaluation);
                        break;
                    }
                }
                //break;
            }
/*        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/
    }



}
