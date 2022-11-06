package graph;

import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.TreeNode;
import xml2bdi.XMLReader;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    public ArrayList<GoalNode> initialState;

    //图的根节点
    private Node root;

    //图中存储哪些Node
    private ArrayList<Node> nodes = new ArrayList<>();

    //表示当前指向哪个节点
    private Node currentNode;

    //运行graph时的当前节点
    private Node runCurrentNode;

    //所有目标都实现的节点
    private Node endNode;

    //图中所包含环境及对应的哪个点（爪形使用）
    private HashMap<ArrayList<String>, Integer> envs = new HashMap();

    public void addEnv(ArrayList<String> env, int a){
        envs.put(env,a);
    }

    public HashMap<ArrayList<String>, Integer> getEnvs() {
        return envs;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public Node getRunCurrentNode() {
        return runCurrentNode;
    }

    public void setRunCurrentNode(Node runCurrentNode) {
        this.runCurrentNode = runCurrentNode;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public ArrayList<GoalNode> getInitialState() {
        return initialState;
    }

    /**
     * 读取初始状态（用GPTs表示）
     * @param gptXMLFileName GPTs的xml文件url
     */
    public void setInitialState(String gptXMLFileName) throws Exception {
        XMLReader reader = new XMLReader(gptXMLFileName);
        ArrayList<GoalNode> tlgs = new ArrayList<>();
        for (TreeNode intention : reader.getIntentions()) {
            if (intention instanceof GoalNode){
                tlgs.add((GoalNode) intention);
            }
        }
        initialState = tlgs;
    }

    //在图中添加节点
    public void addNode(Node node){
        nodes.add(node);
    }


    //遍历节点Id
    public void traversalId(){
        for(Node node : nodes){
            System.out.println(node.getId() + ";");
        }
    }

    //遍历出所有路径
    public void traversalChildNode(){
        for(Node node : nodes){
            for (Node node1 : node.getChildNode()) {
                System.out.println(node.getId() + "->" + node1.getId() + ";");
            }
        }
    }

    public Graph clone(){
        Graph cGraph = new Graph();
        for (Node node : this.getNodes()) {
            cGraph.addNode(node);
        }
        cGraph.setRoot(this.getRoot());
        cGraph.setRunCurrentNode(this.getRunCurrentNode());
        cGraph.setEndNode(this.getEndNode());
        return cGraph;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * 如果选择的这个action执行成功，更新图
     */
    public void success(ActionNode action){
        Node runCurrentNode = this.getRunCurrentNode();
        action.setStatus(TreeNode.Status.SUCCESS);
        for (Node node : runCurrentNode.getChildNode()) {
            if (Node.getDifferentAction(runCurrentNode, node) != null) {
                if (Node.getDifferentAction(runCurrentNode, node).equals(action)) {
                    this.setRunCurrentNode(node);
                }
            }else {//也就是到了图的最终状态（getDifferentAction为空）
                this.setRunCurrentNode(node);
            }
        }
    }


    public void fail(ActionNode action){
        action.setStatus(TreeNode.Status.FAILURE);
    }

}
