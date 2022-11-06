package graph;

import goalplantree.TreeNode;

import java.util.HashSet;

public class Match_Infos {
    public State final_state;
    public HashSet<TreeNode> nodes_visited;

    public Match_Infos(State final_state, HashSet<TreeNode> nodes_visited)
    {
        this.final_state = final_state;
        this.nodes_visited = nodes_visited;
    }
}
