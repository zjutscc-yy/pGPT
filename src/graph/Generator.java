package graph;

import goalplantree.GoalNode;
import goalplantree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator {

    public static Graph bigGraph;
    public static int ID = 0;

    private static int TFlag = 0;

    public static Graph generatePathGraph(ArrayList<String> arrs, String gptPath, ArrayList<String> env) throws Exception {
        //保存每个action属于哪颗树，例T1-A1 保存1
        List<String> findEndIndexTempList = new ArrayList<>();
        for (int i = 0; i < arrs.size(); i++) {
            findEndIndexTempList.add(arrs.get(i).split("-")[0].replace("T", ""));
        }
        //保存每一个树的最后一个action的索引
        int lastActionIndex[] = new int[10];
        for (int iTemp = 0; iTemp < lastActionIndex.length; iTemp++) {
            lastActionIndex[iTemp] = findEndIndexTempList.lastIndexOf("" + iTemp);
        }

        Graph pathGraph = new Graph();
        pathGraph.setInitialState(gptPath);

        //每条路径都添加一个root节点
        Node root = new Node();

        HashMap map1 = new HashMap();
        ArrayList<GoalNode> tlgs = pathGraph.getInitialState();

        //创建一个新的TreeNode的ArryList，因为currentStep是Tree Node型的， 对GoalNode进行遍历，强制转成TreeNode型
        ArrayList<TreeNode> currentSteps = new ArrayList<>();
        for (GoalNode tlg : tlgs) {
            currentSteps.add((TreeNode) tlg);
            map1.put(tlg, tlg);

        }
        root.setCurrentStep(map1);
        root.setId(ID++);
        pathGraph.setCurrentNode(root);
        pathGraph.setRoot(root);
        pathGraph.addNode(root);

        int indexOfSingle = -1;
        //开始遍历action数组
        int i;
        for (i = 0; i < arrs.size(); i++) {
            indexOfSingle++;
            String[] strArray = arrs.get(i).split("-");

            Node node = new Node(ID++, strArray[0], strArray[1]);
            HashMap map = new HashMap();

            //把当前节点的map赋值一份，方便让孩子节点在其基础上更新
            map.putAll(pathGraph.getCurrentNode().getCurrentStep());

            ArrayList<GoalNode> achievedTlg = new ArrayList<>();
            achievedTlg.addAll(pathGraph.getCurrentNode().getAchievedGoal());
            node.setAchievedGoal(achievedTlg);

            GoalNode searchGoalNode = node.searchWhichGoal(tlgs, strArray);//找到当前执行的哪棵树
            TreeNode searchActionNode = node.traversal(searchGoalNode, node.getActionName());

            map.put(searchGoalNode, searchActionNode);
            node.setCurrentStep(map);

            pathGraph.addNode(node);

            pathGraph.getCurrentNode().addChildNode(node);

            pathGraph.setCurrentNode(node);

            /**
             * 如果当前节点某个T的最后一个节点，在其后添加null并把下一行的状态更新
             */
            while (isCurrentNodeEnd(lastActionIndex,indexOfSingle)){
                String TEnd = "";
                String GEnd = "";

                TEnd += "T" + TFlag;
                GEnd += "G" + 0;
                Node insertNode = new Node();
                HashMap insertMap = new HashMap();
                insertMap.putAll(pathGraph.getCurrentNode().getCurrentStep());

                for (GoalNode tlg : tlgs) {
                    if (tlg.getType().equals(TEnd + "-" + GEnd)) {
                        insertMap.put(tlg, null);
                        pathGraph.getCurrentNode().addAchievedGoal(tlg);
                    }
                }

                ArrayList<GoalNode> insertTlg = new ArrayList<>();
                insertTlg.addAll(pathGraph.getCurrentNode().getAchievedGoal());
                insertNode.setAchievedGoal(insertTlg);

                //如果是该条路径的最后一个动作
                if (i == arrs.size()-1){
                    break;
                }
                String[] nextArray = arrs.get(++i).split("-");
                //更新
                GoalNode searchGoal = node.searchWhichGoal(tlgs, nextArray);//找到当前执行的哪棵树
                TreeNode searchAction = node.traversal(searchGoal, nextArray[1]);

                insertMap.put(searchGoal, searchAction);
                insertNode.setCurrentStep(insertMap);
                insertNode.setId(ID++);
                pathGraph.getCurrentNode().addChildNode(insertNode);
                pathGraph.addNode(insertNode);
                pathGraph.setCurrentNode(insertNode);

                if (isCurrentNodeEnd(lastActionIndex, indexOfSingle + 1)) {//判断下一个action是否为end
                    indexOfSingle++;
                    continue;
                } else
                    indexOfSingle++;
                break;
            }

        }

        //添加最后节点
        Node lastNode = new Node();
        HashMap lastMap = new HashMap();
        lastMap.putAll(pathGraph.getCurrentNode().getCurrentStep());
        for (GoalNode tlg : tlgs) {
            lastMap.put(tlg,null);
            lastNode.addAchievedGoal(tlg);
        }
        lastNode.setCurrentStep(lastMap);
        lastNode.setId(ID++);
        pathGraph.addNode(lastNode);
        pathGraph.getCurrentNode().addChildNode(lastNode);

        pathGraph.setEndNode(lastNode);

        pathGraph.addEnv(env,pathGraph.getRoot().getChildNode().get(0).getId());
        return pathGraph;
    }

    public static Graph mergeGraph(Graph graph, Graph pathGraph){
        if (graph.getNodes().size() == 0) {
            return pathGraph;
        }

        Node curGraphPtr = graph.getRoot();
        Node curPathPtr = pathGraph.getRoot().getChildNode().get(0);
        curGraphPtr.addChildNode(curPathPtr);
        graph.addNode(curPathPtr);
        while (curPathPtr.getChildNode().size() != 0){
            curPathPtr = curPathPtr.getChildNode().get(0);
            graph.addNode(curPathPtr);
        }
        for (Map.Entry<ArrayList<String>, Integer> arrayListIntegerEntry : pathGraph.getEnvs().entrySet()) {
            graph.addEnv(arrayListIntegerEntry.getKey(),arrayListIntegerEntry.getValue());
        }

        return graph;
    }

    private static boolean isCurrentNodeEnd(int[] lastActionIndex,int currentIndex) {
        for (int i = 0; i < lastActionIndex.length && lastActionIndex[i] != -1; i++) {
            if (currentIndex == lastActionIndex[i]) {
                TFlag = i;
                return true;
            }
        }
        return false;
    }
}
