package xml_graph;

import beliefbase.BeliefBase;
import xml2bdi.XMLReader;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) throws Exception {

        //获取到完全来自于环境的环境变量
        SummaryEnv summaryEnv = new SummaryEnv("F:\\project\\pGPT\\8.xml");
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();
        System.out.println(absolutetEnv);
        XMLReader reader;
        reader = new XMLReader("F:\\project\\pGPT\\pGPT");
        BeliefBase beliefs = reader.getBeliefs();
        System.out.println("111");
    }
}
