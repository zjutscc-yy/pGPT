package graph;

import scheduler_GPG.Scheduler_GPG;
import util.Log;
import xml2bdi.XMLReader;

import java.util.ArrayList;

public class MCTSGraph {
    public static void main(String[] args) throws Exception {

        XMLReader reader = new XMLReader("F:\\project\\pGPT\\8.xml");
        boolean run_main_experiment = true;

        Log.log_to_file = false;

        // MCTS settings
        int mcts_alpha = 100;
        int mcts_beta = 10;
        double mcts_c = 2.0 * Math.sqrt(2.0); // Using a largeish value because the overall match scores are relatively large (not in [0, 1])

        // Assumed politeness controls how the MCTS agents expect the other agents in the environment to behave.
        // 0 --> Other agents are expected to behave as neutrals (selfishly).
        // 1 --> Other agents are expected to behave as allies.
        // -1 --> Other agents are expected to behave as adversaries.
        double[] assumed_politeness_of_other_agent = {0.0};

        // GPG Schedulers
        ArrayList<String> gpg_scheduler_names = new ArrayList<String>();
        ArrayList<Scheduler_GPG> gpg_schedulers = new ArrayList<Scheduler_GPG>();
        ArrayList<Scheduler_GPG> gpg_scheduler_clones = new ArrayList<Scheduler_GPG>(); // For mirror matches


    }
}
