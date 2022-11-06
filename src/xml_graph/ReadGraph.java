package xml_graph;

import goalplantree.GoalNode;
import goalplantree.TreeNode;
import graph.Graph;
import graph.Node;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadGraph {
    /**
     * 所有节点
     */
    private Graph bigGraph;
    xml2bdi.XMLReader reader;


    public ReadGraph(String url, String gptFilePath) {
        try {
            reader = new xml2bdi.XMLReader(gptFilePath);
//            translate(url);
        } catch (Exception e) {
            System.out.println("Read XML file error! " + " url: " + url);
        }
    }

    public Graph translate(String url) throws Exception {

        bigGraph = new Graph();
        SAXBuilder builder = new SAXBuilder();
        Document read_doc = builder.build(new File(url));
        Element graph = read_doc.getRootElement();

        //获得graph里的所有信息
        List<Element> graphInfo = graph.getChildren();

        //获得图中存在的环境
        Element environements = graphInfo.get(2);
        List<Element> envs = environements.getChildren();
        for (int i = 0; i < envs.size(); i++) {
            //key值
            String[] arrs = envs.get(i).getAttributeValue("env").split(", ");
            ArrayList<String> thisEnv = new ArrayList<>();
            for (int j = 0; j < arrs.length; j++) {
                if (j == 0){
                    String[] split = arrs[j].split("\\[");
                    thisEnv.add(split[1]);
                }else if (j == arrs.length - 1){
                    String[] split = arrs[j].split("]");
                    thisEnv.add(split[0]);
                }else {
                    thisEnv.add(arrs[j]);
                }
            }
            //value值
            int correspond_id = Integer.parseInt(envs.get(i).getAttributeValue("correspond_Id"));
            bigGraph.addEnv(thisEnv, correspond_id);
            System.out.println("读环境" + i);
        }


        //获得nodes标签里的所有信息，即所有nodes
        Element nodesElement = graphInfo.get(3);
        List<Element> node = nodesElement.getChildren();

        for (int i = 0; i < node.size(); i++) {
            System.out.println("读点");
            bigGraph.addNode(readNode(node.get(i)));
        }

        //获取graph的root
        Element rootElement = graphInfo.get(0);
        int rootId = Integer.parseInt(rootElement.getAttributeValue("Id"));
        for (Node bigGraphNode : bigGraph.getNodes()) {
            if (rootId == bigGraphNode.getId()) {
                bigGraph.setRoot(bigGraphNode);
            }
        }

//        获取graph的end
        Element endElement = graphInfo.get(1);
        int endId = Integer.parseInt(endElement.getAttributeValue("Id"));
        for (Node bigGraphNode : bigGraph.getNodes()) {
            if (endId == bigGraphNode.getId()) {
                bigGraph.setEndNode(bigGraphNode);
            }
        }

        //获取Node_Relation标签里的所有信息
        Element Relation = graphInfo.get(4);
        List<Element> nodeRelation = Relation.getChildren();
        //每条node_id
        for (int i = 0; i < nodeRelation.size(); i++) {
            System.out.println("读关系" + i);
            readChild(nodeRelation.get(i), bigGraph);
        }

        return bigGraph;
    }

    private Node readNode(Element element) {
        int id = Integer.parseInt(element.getAttributeValue("Id"));
        List<Element> achieveCur = element.getChildren();

        //获得Achieved所有标签
        Element achievedElement = achieveCur.get(0);
        //获得Achieved标签下的所有AchievedGoal标签
        List<Element> achievedGoal = achievedElement.getChildren();

        ArrayList<GoalNode> achievedTlg = new ArrayList<>();
        for (int i = 0; i < achievedGoal.size(); i++) {
            achievedTlg = readGoal(achievedGoal.get(i), achievedTlg);
        }

        //获得currentStep标签
        Element currentStepsElement = achieveCur.get(1);
        //获得currentStep标签下的所有step标签
        List<Element> steps = currentStepsElement.getChildren();

        HashMap<GoalNode, TreeNode> curSteps = new HashMap();
        for (int i = 0; i < steps.size(); i++) {
            curSteps = readSteps(steps.get(i), curSteps);
        }

        //创建一个新的node
        Node graphNode = new Node(id, curSteps, achievedTlg);
        return graphNode;
    }

    private ArrayList<GoalNode> readGoal(Element element, ArrayList arr) {
        ArrayList <GoalNode> tlgs = new ArrayList<>();
        for (TreeNode intention : reader.getIntentions()) {
            if (intention instanceof GoalNode){
                tlgs.add((GoalNode) intention);
            }
        }
        for (GoalNode tlg : tlgs) {
            String data = element.getAttributeValue("Tlg_name");
            String[] strArr = data.split("-");
            if (strArr.length >= 2 && element.getAttributeValue("Tlg_name").equals(tlg.getType())) {
                arr.add(tlg);
            }
        }
        return arr;
    }


    private HashMap<GoalNode, TreeNode> readSteps(Element element, HashMap map) {
        ArrayList <GoalNode> tlgs = new ArrayList<>();
        for (TreeNode intention : reader.getIntentions()) {
            if (intention instanceof GoalNode){
                tlgs.add((GoalNode) intention);
            }
        }

        for (GoalNode tlg : tlgs) {
            if (tlg.getType().equals(element.getAttributeValue("Tlg_name"))) {
                String data = element.getAttributeValue("curStep");
                String[] strArray = data.split("-");
                if (strArray.length < 2) {
                    map.put(tlg, null);
                } else {
                    TreeNode actionNode = Node.traversalGoal(tlg, strArray[1]);
                    map.put(tlg, actionNode);
                }
            }
        }

        return map;
    }

    private void readChild(Element element, Graph graph) {
        int parentId = Integer.parseInt(element.getAttributeValue("Id"));

        List<Element> childNode = element.getChildren();

        ArrayList<Node> childNodes = new ArrayList<>();
        //每条childNode
        for (int i = 0; i < childNode.size(); i++) {
            int childId = Integer.parseInt(childNode.get(i).getAttributeValue("Id"));
            for (Node node : graph.getNodes()) {
                if (node.getId() == childId) {
                    childNodes.add(node);
                }
            }

        }

        //找到父节点
        for (Node node : graph.getNodes()) {
            if (node.getId() == parentId) {
                for (Node childNode1 : childNodes) {
                    node.addChildNode(childNode1);
                }
            }
        }
    }

}
