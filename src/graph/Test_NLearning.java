package graph;

import xml2bdi.XMLReader;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test_NLearning {
    public static void main(String[] args) throws Exception {

        File actionPath1 = new File("F:\\project\\pGPT_IJCAI\\data_N_MCTS.txt");
        FileWriter actionPath = new FileWriter("data_N_MCTS.txt", true);

        XMLReader reader;
        //测试时环境
        String trainPath;

        int numGoalPlanTree = 8;

        //获取总目录
        File file = new File("F:\\project\\pGPT\\8\\zongtest");
        List<File> sourceList = Arrays.stream(file.listFiles()).toList();

        //遍历每个测试集
        for (int i = 0; i < sourceList.size(); i++) {
            //每个测试集新建一个存储时间和目标的list
            List<Long> allTime = new ArrayList<>();
            List<Double> allAchievedGoal = new ArrayList<>();

            for (int m = 0; m < 7; m++) {
//                System.out.println(sourceList.get(i).getName() + "第" + m + "次");
                List<Integer> thisAverageAchieveGoal = new ArrayList<>();
                //不同环境,之后可以不用读文件
                List<File> fileList = getFileList(sourceList.get(i).getAbsolutePath());

                long start = System.currentTimeMillis();
                for (int k = 0; k < fileList.size(); k++) {
                    trainPath = fileList.get(k).getPath();
                    reader = new XMLReader(trainPath);

                    MCTS_Scheduler gpg_scheduler = new MCTS_Scheduler(false);
                    State currentStateGPG = new State(reader.getBeliefs(), reader.getIntentions(), reader.getNodeLib(), reader.getPreqMap(), reader.getParentMap(), 0);

                    currentStateGPG.reset_linearisations();

                    Match match = new Match(
                            numGoalPlanTree,
                            currentStateGPG.clone(),
                            "MCTS_Agent",
                            gpg_scheduler
                    );
                    match.run_two_sided_series(true, false);

//                    for (String actionName : gpg_scheduler.getActions()) {
//                        System.out.println(actionName);
//                    }

//                    System.out.println(match.agent_name + " achieve " + match.getAchieveGoalNum() + " goal.");
                    thisAverageAchieveGoal.add(match.getAchieveGoalNum());

                }//50个文件
                long end = System.currentTimeMillis();
//                System.out.println(sourceList.get(i).getName() + "第" + m + "次程序运行时间" + (end - start));
                allTime.add(end - start);

                //计算每次里的50个文件的平均实现目标
                int thisAver = 0;
                for (int j = 0; j < thisAverageAchieveGoal.size(); j++) {
                    thisAver += thisAverageAchieveGoal.get(j);
                }
                double wushiGoal = thisAver / (double) thisAverageAchieveGoal.size();
//                System.out.println(sourceList.get(i).getName() + "第" + m + "次平均实现目标数量" + wushiGoal);
                allAchievedGoal.add(wushiGoal);
            }//次数

            //求该测试集下的平均时间
            long x = 0;
            for (int j = 0; j < allTime.size(); j++) {
                x += allTime.get(j);
            }
            double averageTime = x / (double) allTime.size();
            System.out.println(sourceList.get(i) + "平均运行时间为" + averageTime);

            //求该测试集下的平均目标数量
            double y = 0;
            for (int j = 0; j < allAchievedGoal.size(); j++) {
                y += allAchievedGoal.get(j);
            }
            double averageGoal = y / (double) allAchievedGoal.size();
            System.out.println(sourceList.get(i) + "平均实现目标数量为" + averageGoal);

            //记录下来
            actionPath.append(sourceList.get(i).getName());
            actionPath.append("\n");
            actionPath.append(String.valueOf(averageTime));
            actionPath.append("\n");
            actionPath.append(String.valueOf(averageGoal));
            actionPath.append("\n");
            actionPath.append("\n");

        }
        actionPath.close();

    }

    public static List<File> getFileList(String dirStr) {
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
