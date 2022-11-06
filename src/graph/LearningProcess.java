package graph;

import goalplantree.ActionNode;
import xml2bdi.XMLReader;
import xml_graph.SummaryEnv;
import xml_graph.WriteGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 学习过程
 */

public class LearningProcess {
    //总图
    public static Graph resultGraph;

    public static void main(String[] args) throws Exception {
        XMLReader reader;
        //用来暂时保存训练文件夹下不同文件
        String trainPath;
        //原本树的路径
        String gptPath = "F:\\project\\pGPT\\8.xml";
        //每个环境测试次数
        int testNum = 5;
        int numGoalPlanTree = 8;
        //可以执行成功的新环境要复制到哪
        String newFilePath = "F:\\project\\pGPT\\8\\500";
        //要训练的环境所在文件夹
        List<File> fileList = getFileList("F:\\project\\pGPT\\8\\8envs");

        //存放结果，方便判断是不是加入图的每个环境都为可执行成功的环境
        List<Integer> resultList = new ArrayList<>();

        //得到完全来自于环境中的变量名
        SummaryEnv summaryEnv = new SummaryEnv(gptPath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        resultGraph = new Graph();

        System.out.println("开始学习环境");
        for (int i = 0; i < fileList.size(); i++) {
            if (resultGraph.getEnvs().size() >= 500) {
                break;
            }
            trainPath = fileList.get(i).getPath();
            reader = new XMLReader(trainPath);

            Map<String, Boolean> map = reader.getBeliefs().getMap();

            //判断环境是否已存在
            ArrayList<String> thisEnv = new ArrayList<>();
            for (Map.Entry<String, Boolean> stringBooleanEntry : map.entrySet()) {
                if (absolutetEnv.contains(stringBooleanEntry.getKey()) && stringBooleanEntry.getValue() == true) {
                    thisEnv.add(stringBooleanEntry.getKey());
                }
            }

            if (resultGraph.getEnvs().keySet().contains(thisEnv)) {
                System.out.println("该环境已存在于图中");
                resultGraph.setRunCurrentNode(resultGraph.getRoot());

                Integer searchRouteId = resultGraph.getEnvs().get(thisEnv);
                for (Node node : resultGraph.getRoot().getChildNode()) {
                    if (node.getId() == searchRouteId) {
                        //找到对应那条路径
                        ActionNode act = Node.getDifferentAction(resultGraph.getRunCurrentNode(), node);
                        System.out.println(act.getType());
                        resultGraph.setRunCurrentNode(node);
                        break;
                    }
                }

                //开始执行
                while (resultGraph.getRunCurrentNode().getChildNode().size() != 0) {
                    Node node = resultGraph.getRunCurrentNode().getChildNode().get(0);
                    if (Node.getDifferentAction(resultGraph.getRunCurrentNode(), node) == null) {
                        break;
                    }
                    ActionNode act = Node.getDifferentAction(resultGraph.getRunCurrentNode(), node);
                    System.out.println(act.getType());
                    resultGraph.setRunCurrentNode(node);
                }
                System.out.println(resultGraph.getRunCurrentNode().getAchievedGoal().size());
            } else {
                /**
                 * start 树
                 */

                for (int j = 0; j < testNum; j++) {
                    System.out.println("该环境不在图中");
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

                    for (String actionName : gpg_scheduler.getActions()) {
                        System.out.println(actionName);
                    }

                    System.out.println(match.agent_name + " achieve " + match.getAchieveGoalNum() + " goal.");
                    //如果某次实现全部目标，则加入到图中
                    if (match.getAchieveGoalNum() == numGoalPlanTree) {
                        //把当前环境加入到envs文件夹中
                        copyFile(fileList.get(i).getAbsolutePath(), newFilePath + "\\" + fileList.get(i).getName());

                        //生成单条路径，合并到图里
                        Graph pathGraph = Generator.generatePathGraph(gpg_scheduler.getActions(), gptPath, thisEnv);

                        //合并爪形图
                        resultGraph = Generator.mergeGraph(resultGraph, pathGraph);
                        resultList.add(match.getAchieveGoalNum());

                        break;
                    }

                }
            }

        }

        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println("一共学习了" + resultList.size() + "个可执行环境");
        double averageAchieveGoal = x / (double) resultList.size();
        System.out.println("平均实现目标数：" + averageAchieveGoal);

        WriteGraph wxf = new WriteGraph();
        wxf.CreateXML(resultGraph, "F:\\project\\graph\\graph8_pGPT_500.xml");
    }

    public static void copyFile(String source, String dest) throws Exception {
        FileInputStream in = new FileInputStream(new File(source));
        FileOutputStream out = new FileOutputStream(new File(dest));
        byte[] buff = new byte[512];
        int n = 0;
        while ((n = in.read(buff)) != -1) {
            out.write(buff, 0, n);
        }
        out.flush();
        in.close();
        out.close();
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
