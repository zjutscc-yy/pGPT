package graph;

public abstract class Scheduler {

    public Match match;
    //单智能体
    public int agent_num;

    public boolean mirror_match;

    public double[][] allianceMatrix;
    public boolean[][] available_intentions;
    public double[][] intention_values;

    public abstract Decision getDecision(State state);

    public abstract void reset();

    public void loadMatchDetails(Match match, int agent_num,boolean mirror_match) {
        this.match = match;
        this.agent_num = agent_num;
        this.mirror_match = mirror_match;

        this.allianceMatrix = new double[match.numAgents][match.numAgents];
        this.available_intentions = new boolean[1][match.numGoalPlanTrees];
        this.intention_values = new double[1][match.numGoalPlanTrees];

        for (int i = 0; i < match.numAgents; i++) {
            for (int j = 0; j < match.numAgents; j++) {
                if (i == j) {
                    // An agent is always allied with itself
                    allianceMatrix[i][j] = 1.0;
                } else {
                    allianceMatrix[i][j] = 0.0;
                }
            }

        }

        for (int intentionNum = 0; intentionNum < match.numGoalPlanTrees; intentionNum++) {
            int agentToAssignIntention = mirror_match ? ((intentionNum + 1) % match.numAgents) : (intentionNum % match.numAgents);

            for (int agentNum = 0; agentNum < match.numAgents; agentNum++) {
                available_intentions[agentNum][intentionNum] = (agentNum == agentToAssignIntention);
            }

            for (int agentNum = 0; agentNum < match.numAgents; agentNum++) {
                intention_values[agentNum][intentionNum] = allianceMatrix[agentNum][agentToAssignIntention];
            }
        }
    }
}
