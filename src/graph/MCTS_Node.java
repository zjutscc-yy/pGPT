package graph;

import goalplantree.ActionNode;
import goalplantree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;

public class MCTS_Node {

    // state of this node
    public State state;

    // selection information
    TreeNode actionChoice;

    // child nodes
    public ArrayList<MCTS_Node> children;

    // statistics
    public int nVisits;
    public Match match;
    public double totValue;
    public double totSqValue;
    public HashMap<TreeNode, Double> totValueAllNodes;
    public HashMap<TreeNode, Integer> nVisitsAllNodes;

    public MCTS_Node(State state, Match match)
    {
        this.state = state;
        this.match = match;

        // statistics initialisation
        init();
    }

    private void init()
    {
        nVisits = 0;
        this.totValue = 0.0;
        this.totSqValue = 0.0;
        this.totValueAllNodes = new HashMap<TreeNode, Double>();
        this.nVisitsAllNodes = new HashMap<TreeNode, Integer>();
    }

    /**
     * @return true if it is the leaf node; false, otherwise
     */
    public boolean isLeaf()
    {
        return children == null;
    }

    /**
     * expand the current node
     */
    public void expand(boolean[] intentionAvailable, boolean legacy, String player_name)
    {
        children = new ArrayList<>();
        ArrayList<TreeNode> expansionActions = state.getExpansionActions(intentionAvailable, legacy, player_name);

        for (TreeNode t : expansionActions)
        {
            State new_state = state.clone();

            if (t != null)
            {
                new_state.applyAction(t);
            }

            if (t == null || t instanceof ActionNode)
            {
                new_state.playerTurn = (state.playerTurn + 1) % match.numAgents;
            }

            MCTS_Node child_node = new MCTS_Node(new_state, match);
            child_node.actionChoice = t;
            this.children.add(child_node);
        }
    }

    public TreeNode getActionChoice()
    {
        return actionChoice;
    }
}
