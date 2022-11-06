package graph;

import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.PlanNode;
import goalplantree.TreeNode;

import java.util.*;

public class Node {

    //节点名字：先用数字
    private int id;

    //action 中 T1-A1 中的T1
    private String actionTreeName;

    //action 中 T1-A1 中的A1
    private String actionName;

    //当前执行到多棵 goal-plan tree的哪个步骤  如<G0,Action A0>  <G1,Action A5>
    private HashMap<GoalNode, TreeNode> currentSteps;

    //节点的孩子节点
    private ArrayList<Node> childNode = new ArrayList<>();

    //该节点当前实现的目标
    private ArrayList<GoalNode> achievedGoal = new ArrayList<>();

    public void addAchievedGoal(GoalNode goal){
        achievedGoal.add(goal);
    }

    public ArrayList<GoalNode> getAchievedGoal() {
        return achievedGoal;
    }

    public void setAchievedGoal(ArrayList<GoalNode> achievedGoal) {
        this.achievedGoal = achievedGoal;
    }

    //在currentNode下添加子节点
    public void addChildNode(Node node) {
        childNode.add(node);
    }

    public void removeChildNode(Node node) {
        childNode.remove(node);
    }

    //判断两个节点是否相等(currentsteps相等即认为节点相等)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node otherNode = (Node) o;
        HashMap otherCurrentSteps = otherNode.currentSteps;
        if (currentSteps.size() != otherCurrentSteps.size()) {
            return false;
        }
        //遍历当前对象的currentSteps
        for (Map.Entry<GoalNode, TreeNode> goalNodeActionNodeEntry : currentSteps.entrySet()) {
            // 当前的key （tlg）
            GoalNode tlg = goalNodeActionNodeEntry.getKey();
            // 当前的value （current step）
            if (goalNodeActionNodeEntry.getValue() != null) {
                TreeNode curStep = goalNodeActionNodeEntry.getValue();
                // 如果比较的对象不含有当前key，则说明一定不相等
                if (!otherCurrentSteps.containsKey(tlg)) {
                    return false;
                }
                // 如果当前tlg的current step与比较对象的current step不相等，返回false
                if (otherCurrentSteps.get(tlg) == null || !otherCurrentSteps.get(tlg).equals(curStep)) {
                    return false;
                }
            }else {
                if (!otherCurrentSteps.containsKey(tlg)) {
                    return false;
                }
                if (otherCurrentSteps.get(tlg) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentSteps);
    }

    public ArrayList<Node> getChildNode() {
        return childNode;
    }

    public Node() {
    }

    public Node(int id, HashMap<GoalNode, TreeNode> currentSteps ,ArrayList<GoalNode> achievedGoal) {
        this.id = id;
        this.currentSteps = currentSteps;
        this.achievedGoal = achievedGoal;
    }

    public Node(int id, String actionTreeName, String actionName) {
        this.id = id;
        this.actionTreeName = actionTreeName;
        this.actionName = actionName;
    }

    //找到当前action属于哪颗树
    public GoalNode searchWhichGoal(ArrayList<GoalNode> tlgs,String[] str) {
        for (GoalNode tlg : tlgs) {
            String searchTree;
            String[] strArray = tlg.getType().split("-");//把 T 分割出来
            searchTree = strArray[0];
            if (searchTree.equals(str[0])) {//找到当前读取的action属于哪颗树，接下来对树进行层次遍历
                return tlg;
            }
        }
        return null;
    }

    //已经找到当前action属于哪颗树，        现对这棵树进行遍历找到对应action，然后setCurrentStep
    public static TreeNode traversal(GoalNode node, String actionName) {
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(node);
        int i = 0;
        while (!queue.isEmpty()) {
            i++;

            TreeNode poll = queue.poll();

            //判断poll是否为action节点，如果是，与当前读到的action比较
            if (poll instanceof ActionNode) {
                String searchAction;
                String[] strArray = poll.getType().split("-");//把 T 分割出来
                searchAction = strArray[1];

                if (searchAction.equals(actionName)) {
                    return poll;
                }
            } else if (poll instanceof PlanNode) {
                PlanNode planNode = (PlanNode) poll;
                if (planNode.getPlanbody() != null) {
                    for (TreeNode treeNode : planNode.getPlanbody()) {
                        queue.offer(treeNode);
                    }

                }
            } else if (poll instanceof GoalNode) {
                GoalNode goalNode = (GoalNode) poll;
                if (goalNode.getPlans() != null) {
                    for (PlanNode plan : goalNode.getPlans()) {
                        queue.offer((TreeNode) plan);
                    }

                }
            }

        }
        return null;
    }

    //读取图的xml文件时使用
    public static TreeNode traversalGoal(GoalNode node, String actionName) {
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(node);
        int i = 0;
        while (!queue.isEmpty()) {
            i++;

            TreeNode poll = queue.poll();

            //判断poll是否为action节点，如果是，与当前读到的action比较
            if (poll instanceof ActionNode) {
                String searchAction;
                String[] strArray = poll.getType().split("-");//把 T 分割出来
                searchAction = strArray[1];

                if (searchAction.equals(actionName)) {
                    return poll;
                }
            } else if (poll instanceof PlanNode) {
                PlanNode planNode = (PlanNode) poll;
                if (planNode.getPlanbody() != null) {
                    for (TreeNode treeNode : planNode.getPlanbody()) {
                        queue.offer(treeNode);
                    }

                }
            } else if (poll instanceof GoalNode) {
                GoalNode goalNode = (GoalNode) poll;

                String searchGoal;
                String[] strArry = poll.getType().split("-");
                searchGoal = strArry[1];

                if (goalNode.getPlans() != null) {
                    for (PlanNode plan : goalNode.getPlans()) {
                        queue.offer((TreeNode) plan);
                        if (searchGoal.equals(actionName)) {
                            return poll;
                        }
                    }

                }
            }

        }
        return null;
    }


    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }


    //返回当前action是哪棵树的
    public String getActionTreeName() {
        return actionTreeName;
    }

    public void setActionTreeName(String actionTreeName) {
        this.actionTreeName = actionTreeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<GoalNode, TreeNode> getCurrentStep() {
        return currentSteps;
    }

    public void setCurrentStep(HashMap currentStep) {
        this.currentSteps = currentStep;
    }

    /**
     * 获得导致两个节点 currentSteps 不同的actionNode
     *
     * @param fnode 父节点
     * @param cnode 孩子节点
     * @return
     */
    public static ActionNode getDifferentAction(Node fnode, Node cnode) {
        //孩子节点的hashmap
        HashMap<GoalNode, TreeNode> nodeCurrentStep = cnode.getCurrentStep();
        //遍历父节点的curentStep
        for (Map.Entry<GoalNode, TreeNode> entry : fnode.getCurrentStep().entrySet()) {
            GoalNode key = entry.getKey();//获得父节点的key
            if(entry.getValue() != null) {//如果父节点key对应的value不为空
                TreeNode value = entry.getValue();
                //遍历孩子节点
                for (Map.Entry<GoalNode, TreeNode> childEntry : nodeCurrentStep.entrySet()) {
                    GoalNode childKey = childEntry.getKey();
                    if (childEntry.getValue() != null){
                        if (key.getType().equals(childKey.getType()) &&
                                !value.getType().equals(childEntry.getValue().getType())){
                            if (childEntry.getValue() instanceof ActionNode){
                                ActionNode act = (ActionNode) childEntry.getValue();
                                return act;
                            }

                        }
                    }
                }
//                if (nodeCurrentStep.get(key) != value && nodeCurrentStep.get(key) != null) {
//                    if (nodeCurrentStep.get(key) instanceof ActionNode) {
//                        //返回孩子节点的那个动作
//                        ActionNode act = (ActionNode) nodeCurrentStep.get(key);
//                        return act;
//                    }
//                }
            }
        }
        return null;
    }

}
