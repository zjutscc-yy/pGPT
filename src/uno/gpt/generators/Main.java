package uno.gpt.generators;

import java.util.Random;

/**
 * generator pGPT
 * 改变参数设置
 * 设置生成的pGPT保存路径
 */

public class Main {
    /**
     * The main function, takes a set of commandline arguments
     */
    public static void main(String[] args) {
        // NOTE: The below variables only apply if useRandomXML == true
        int depth, numEnvironmentVariables, numVarToUseAsPostCond, numGoalPlanTrees, subgoalsPerPlan, plansPerGoal, actionsPerPlan;
        double propGuaranteedCPrecond;

        // Generator settings
        //树的深度
        depth = 5;
        //环境变量总个数
        numEnvironmentVariables = 60;
        //用于作为后置条件的环境变量个数
        numVarToUseAsPostCond = 50;
        //goal plan tree 数量
        numGoalPlanTrees = 8;
        //每个计划有几个子目标
        subgoalsPerPlan = 1;
        //每个目标几个计划
        plansPerGoal = 2;
        //每个计划几个动作
        actionsPerPlan = 3;
        propGuaranteedCPrecond = 0.5;

        Random rm = new Random();

        String generatorArgsStr = "";

        int randomSeed = rm.nextInt();

        //生成的pGPT保存路径
        String generatedXmlFilename = "F:\\project\\pGPT\\8.1.xml";

        String[] generatorArgs = new String[21];
        generatorArgs[0] = "synth"; // Options are "synth", "miconic", "block", "logi"
        generatorArgs[1] = "-f";
        generatorArgs[2] = generatedXmlFilename;
        generatorArgs[3] = "-s";
        generatorArgs[4] = Integer.toString(randomSeed);
        generatorArgs[5] = "-d";
        generatorArgs[6] = Integer.toString(depth);
        generatorArgs[7] = "-t";
        generatorArgs[8] = Integer.toString(numGoalPlanTrees);
        generatorArgs[9] = "-v";
        generatorArgs[10] = Integer.toString(numEnvironmentVariables);
        generatorArgs[11] = "-g";
        generatorArgs[12] = Integer.toString(subgoalsPerPlan);
        generatorArgs[13] = "-p";
        generatorArgs[14] = Integer.toString(plansPerGoal);
        generatorArgs[15] = "-a";
        generatorArgs[16] = Integer.toString(actionsPerPlan);
        generatorArgs[17] = "-y";
        generatorArgs[18] = Double.toString(propGuaranteedCPrecond);
        generatorArgs[19] = "-w";
        generatorArgs[20] = Integer.toString(numVarToUseAsPostCond);
        //得到类似于生成goal plan tree中config设置的一串字符
        for (int argNum = 0; argNum < generatorArgs.length; argNum++)
        {
            generatorArgsStr = generatorArgsStr + generatorArgs[argNum] + " ";
        }
        XMLGenerator.generate(generatorArgs);
    }
}
