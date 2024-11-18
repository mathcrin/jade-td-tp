package issia23.agents;

import jade.core.AgentServicesTools;
import jade.gui.AgentWindowed;
import jade.gui.SimpleWindow4Agent;

import java.awt.*;

public class DistributorAgent extends AgentWindowed {
    @Override
    public void setup(){
        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.GRAY);
        println("hello, do you want a new object  ?");

        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "distributor");
        println("I'm just registered as a Distributor");

    }

}
