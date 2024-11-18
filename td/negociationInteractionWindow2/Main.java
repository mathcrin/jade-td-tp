package td.negociationInteractionWindow2;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;

import java.util.Properties;

/**
 * Agent class to allow exchange of messages between an agent named ping, that initiates the 'dialog', and an agent
 * named 'pong'
 *
 * @author emmanueladam
 */
public class Main  {

    public static void main(String[] args) {
        // prepare argument for the JADE container
        Properties prop = new ExtendedProperties();
        // display a control/debug window
        prop.setProperty(Profile.GUI, "true");
        // declare the agents
        var sb = new StringBuffer();
        sb.append("seller:td.negociationInteractionWindow2.SellerAgent;");
        sb.append("buy1:td.negociationInteractionWindow2.BuyerAgent;");
        sb.append("buy2:td.negociationInteractionWindow2.BuyerAgent;");
        prop.setProperty(Profile.AGENTS, sb.toString());
        // create the ain container
        ProfileImpl profMain = new ProfileImpl(prop);
        // launch it !
        Runtime rt = Runtime.instance();
        rt.createMainContainer(profMain);
    }
}
