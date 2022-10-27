package xml_graph;

import beliefbase.Condition;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.PlanNode;
import goalplantree.TreeNode;
import xml2bdi.XMLReader;

import java.util.ArrayList;

/**
 * 总结出pGPT完全来自于环境中的变量
 */
public class SummaryEnv {

    //获取所有计划的前置条件
    ArrayList<Condition> planPre = new ArrayList<>();

    //获取所有动作的后置条件
    ArrayList<Condition> actPost = new ArrayList<>();

    //得到完全来自于环境的环境变量
    ArrayList<Condition> absoluteEnv = new ArrayList<>();


    public ArrayList<Condition> getPlanPre() {
        return planPre;
    }

    public ArrayList<Condition> getActPost() {
        return actPost;
    }

    public ArrayList<Condition> getAbsoluteEnv() {
        return absoluteEnv;
    }

    //读取树的xml文件根节点
    public SummaryEnv(String gptXMLFileName) throws Exception {
        XMLReader reader = new XMLReader(gptXMLFileName);
        ArrayList<TreeNode> intentions = reader.getIntentions();
        for (TreeNode intention : intentions) {
            if (intention instanceof GoalNode){
                GoalNode tlg = (GoalNode) intention;
                checkGoal(tlg);
                checkAbsolutetEnv();
                planPre.clear();
                actPost.clear();
            }
        }
    }

    //检查计划节点，如果它的孩子节点是action，把post加入set，如果是goal，进行checkGoal
    public void checkPlan(PlanNode plan) {
        TreeNode[] planbody = plan.getPlanbody();
        for (TreeNode treeNode : planbody) {
            if (treeNode instanceof ActionNode) {
                ActionNode actionNode = (ActionNode) treeNode;
                for (Condition condition : actionNode.getPostc()) {
                    int notSameNum = 0;
                    for (Condition searchCondition : actPost) {
                        if (!searchCondition.isSame(condition)){
                            notSameNum++;
                        }
                    }
                    if (notSameNum == actPost.size()){
                        actPost.add(condition);
                    }
                }
            } else {
                GoalNode goalNode = (GoalNode) treeNode;
                checkGoal(goalNode);
            }
        }
    }

    //检查目标节点，获取实现目标的计划，遍历所有计划，将计划的前置条件加入到planPre里，接着对plan进行处理
    public void checkGoal(GoalNode goal) {
        PlanNode[] plans = goal.getPlans();
        for (PlanNode plan : plans) {
            for (Condition condition : plan.getPrec()) {
                if (!planPre.contains(condition)){
                    planPre.add(condition);
                }
            }
            checkPlan(plan);
        }
    }

    //plan的pre减去action的post
    public void checkAbsolutetEnv() {
        for (Condition condition : this.getPlanPre()) {
            if (!this.getActPost().contains(condition) && !this.getAbsoluteEnv().contains(condition)) {
                absoluteEnv.add(condition);
            }
        }
    }

    public ArrayList<String> checkAbsolutetEnvName() {
        //得到完全来自于环境的环境变量的名字
        ArrayList<String> absoluteEnvName = new ArrayList<>();
        for (Condition condition : this.getAbsoluteEnv()) {
            absoluteEnvName.add(condition.getLiteral());
        }
        return absoluteEnvName;
    }
}
