package xml_graph;

import beliefbase.Condition;
import goalplantree.GoalNode;
import goalplantree.TreeNode;
import xml2bdi.XMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 删除不满足条件的环境
 *
 * 需要的参数：
 * pGPT的原文件路径 config
 *
 * 要判断的文件夹路径 main
 */

public class DelUnrealizedEnv {
    public static void main(String[] args) throws Exception {
        //树的存储路径
        String gptFilePath;
        if (args.length == 0) {
            System.out.println("ERROR: no GPT file specified!");
            return;
        }
        gptFilePath = args[0];
        //获取到完全来自于环境的环境变量
        SummaryEnv summaryEnv = new SummaryEnv(gptFilePath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        ArrayList<ArrayList<Condition[]>> allGoalExe = new ArrayList<>();
        //获取保证每个目标可实现的集合
        ExecutableGoal executableGoal = new ExecutableGoal(absolutetEnv);
        XMLReader reader = new XMLReader(gptFilePath);

        ArrayList<GoalNode> tlgs = new ArrayList<>();
        for (TreeNode intention : reader.getIntentions()) {
            if (intention instanceof GoalNode){
                tlgs.add((GoalNode)intention);
            }
        }

        //获取所有tlg可能集合
        for (GoalNode tlg : tlgs) {
            ArrayList<Condition[]> literals = executableGoal.checkGoal(tlg);
            allGoalExe.add(literals);
        }
//        Graph_5_0.3
        List<File> fileList = getFileList("F:\\project\\pGPT\\8\\8test");
        //遍历每个文件
        for (int i = 0; i < fileList.size(); i++) {
            int m = 0;
            //检查每个文件是否符合所有goal
            for (ArrayList<Condition[]> singleGoal : allGoalExe) {
                //checkFile为false，说明当前目标在该文件下没有可以实现的组合,删掉该文件
                if (!checkFile(fileList.get(i).getPath(), singleGoal, absolutetEnv)) {
                    m++;
                    //删掉该文件
//                    fileList.get(i).delete();
//                    break;
                }

            }
            //删除一个目标都不能实现的环境
            if (m == tlgs.size()) {
                fileList.get(i).delete();
            }
        }

    }

    /**
     * @param gptPath
     * @param conditions 当前目标可执行的集合
     * @return
     */
    public static boolean checkFile(String gptPath, ArrayList<Condition[]> conditions, ArrayList<String> absoluteEnv) throws Exception {
        XMLReader reader = new XMLReader(gptPath);
        //遍历每种可能性
        for (Condition[] possibleLiterals : conditions) {
            //用于统计完全来自于环境中的变量与每种可能性中名字相同的交集的个数
            int nameNum = 0;
            int valueNum = 0;
            //遍历每种可能性里包含的元素
            for (Condition c : possibleLiterals) {
                if (absoluteEnv.contains(c.getLiteral())) {
                    nameNum++;
                    Map<String, Boolean> map = reader.getBeliefs().getMap();
                    if (map.get(c.getLiteral()) == c.value){
                        valueNum++;
                    }
                }
            }
            if (nameNum == valueNum) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件夹下的文件列表
     *
     * @param dirStr 文件夹的路径
     * @return
     */
    public static List<File> getFileList(String dirStr) {
        //if istxt
        File file = new File(dirStr);
        List<File> sourceList = Arrays.stream(file.listFiles()).toList();
        List<File> resultList = new ArrayList<>();

        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).isFile()) {
                if (sourceList.get(i).getName().contains("txt")) {
                    System.out.println(sourceList.get(i).getName());
                } else {
                    resultList.add(sourceList.get(i));
                }
            }
        }
        return resultList;
    }
}
