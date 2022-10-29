package graph;

import xml2bdi.XMLReader;

public class MCTS_pGPT {
    public static void main(String[] args) throws Exception {

        XMLReader reader = new XMLReader("F:\\project\\pGPT\\test.xml");
        int numGoalPlanTrees = reader.getIntentions().size();

        // MCTS settings
        int mcts_alpha = 100;
        int mcts_beta = 10;
        double mcts_c = 2.0 * Math.sqrt(2.0); // Using a largeish value because the overall match scores are relatively large (not in [0, 1])


        // *** pGPG VERSIONS ***
        MCTS_Scheduler gpg_scheduler = new MCTS_Scheduler(mcts_alpha, mcts_beta, mcts_c, 1.0, false);

        // Read the initial state from the XML file
        State currentStateGPG = new State(reader.getBeliefs(), reader.getIntentions(), reader.getNodeLib(), reader.getPreqMap(), reader.getParentMap(), 0);

        currentStateGPG.reset_linearisations();

        Match match = new Match(
                numGoalPlanTrees,
                currentStateGPG.clone(),
                "MCTS_Agent",
                gpg_scheduler
        );
        match.run_two_sided_series(true,false);

        for (String actionName : gpg_scheduler.getActions()) {
            System.out.println(actionName);
        }

        System.out.println(match.getAchieveGoalNum());
    }
}
