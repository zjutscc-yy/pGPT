package graph;

import goalplantree.TreeNode;

import java.util.ArrayList;

public class Decision {

    public ArrayList<TreeNode> actionChoice;
    public boolean forcedPass;

    public Decision(ArrayList<TreeNode> actionChoice, boolean forcedPass)
    {
        this.actionChoice = actionChoice;
        this.forcedPass = forcedPass;
    }
}
