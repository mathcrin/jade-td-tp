package issia23.agents;

import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPANames;
import jade.gui.AgentWindowed;
import jade.gui.GuiEvent;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.*;

public class UserAgent extends AgentWindowed {
    /**
     * skill in "repairing" from 0 (not understand) to 3 (repairman like)
     */
    int skill;
    /**
     * list of potential helpers (agent registered in the type of service "repair"
     */
    List<AID> helpers;

    @Override
    public void setup() {
        this.window = new SimpleWindow4Agent(getLocalName(), this);
        window.setButtonActivated(true);

        skill = (int) (Math.random() * 4);
        println("hello, I have a skill = " + skill);
        helpers = new ArrayList<>();
    }

    @Override
    public void onGuiEvent(GuiEvent evt) {
        //I suppose there is only one type of event, clic on go
        //search about repairing agents
        helpers.addAll(Arrays.stream(AgentServicesTools.searchAgents(this, "repair", null)).toList());

        println("-".repeat(30));
        for (AID aid : helpers)
            println("found this agent : " + aid.getLocalName());
        println("-".repeat(30));

        addCFP();
    }

    /**add a CFP from user to list of helpers*/
    private void addCFP(){
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setConversationId("id");
        int randint = (int)(Math.random()*3);
        msg.setContent(String.valueOf(randint));

        msg.addReceivers(helpers.toArray(AID[]::new));
        println("-".repeat(40));

        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));


        ContractNetInitiator init = new ContractNetInitiator(this, msg) {
            //function triggered by a PROPOSE msg
            // @param propose     the received propose message
            // @param acceptances the list of ACCEPT/REJECT_PROPOSAL to be sent back.
            //                    list that can be modified here or at once when all the messages are received
            @Override
            public void handlePropose(ACLMessage propose, List<ACLMessage> acceptations) {
                println("Agent %s proposes %s ".formatted(propose.getSender().getLocalName(), propose.getContent()));
            }

            //function triggered by a REFUSE msg
            @Override
            protected void handleRefuse(ACLMessage refuse) {
                println("REFUSE ! I received a refuse from " + refuse.getSender().getLocalName());
            }

            //function triggered when all the responses are received (or after the waiting time)
            //@param theirVotes the list of message sent by the voters
            //@param myAnswers the list of answers for each voter
            @Override
            protected void handleAllResponses(List<ACLMessage> theirVotes, List<ACLMessage> myAnswers) {
                ArrayList<ACLMessage> listeProposals = new ArrayList<>(theirVotes);
                //we keep only the proposals only
                listeProposals.removeIf(v -> v.getPerformative() != ACLMessage.PROPOSE);
                myAnswers.clear();

                ACLMessage bestProposal =null;
                ACLMessage bestAnswer =null;
                var bestPrice = Integer.MAX_VALUE;

                for (ACLMessage proposal : listeProposals) {
                    //by default, we build a accept answer for each proposal
                    var answer = proposal.createReply();
                    answer.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    myAnswers.add(answer);
                    var content = Integer.parseInt(proposal.getContent());
                    println(proposal.getSender().getLocalName() + " has proposed " + content);
                    if (content<bestPrice){
                        bestPrice = content;
                        bestProposal = proposal;
                        bestAnswer = answer;
                    }

                }

                if (bestProposal !=null)
                {
                    bestProposal.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    println("I choose the proposal of " + bestProposal.getSender().getLocalName());
                }

                println("-".repeat(40));

            }

            //function triggered by a INFORM msg : a voter accept the result
            // @Override
            protected void handleInform(ACLMessage inform) {
                println("the vote is accepted by " + inform.getSender().getLocalName());
            }


        };

        addBehaviour(init);
    }

    /**here we simplify the scenario. A breakdown is about 1 elt..
     * so whe choose a no between 1 to 4 and ask who can repair at at wich cost.*/
    private void breakdown(){

    }

    @Override
    public void takeDown(){println("bye !!!");}
}
