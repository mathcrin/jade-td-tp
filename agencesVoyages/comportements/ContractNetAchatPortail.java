package agencesVoyages.comportements;

import agencesVoyages.agents.PortailAgent;
import agencesVoyages.data.Journey;
import agencesVoyages.data.JourneysList;
import agencesVoyages.gui.PortailGui;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.*;


/**
 * Journey Buyer Behaviour by contract net to a seller agent
 *
 * @author revised by Mathis Crinchon
 * @version 1
 */
public class ContractNetAchatPortail extends ContractNetInitiator {

    private final String from;
    private final String to;
    private final int departure;
    private final String preference;

    /**
     * agent gui
     */
    private final PortailGui window;

    /**
     * acheteur lie a ce comportement
     */
    private final PortailAgent monAgent;

    /**
     * initialisation
     *
     * @param agent       agent initiator
     * @param msg         initial message to send
     * @param _from       origine city
     * @param _to         destination city
     * @param _departure  date of departure
     * @param _preference criteria (cost, duration, ...)
     */
    public ContractNetAchatPortail(Agent agent, ACLMessage msg, final String _from, final String _to, final int _departure, final String _preference) {
        super(agent, msg);
        from = _from;
        to = _to;
        departure = _departure;
        preference = _preference;
        monAgent = (PortailAgent) agent;
        window = monAgent.getWindow();
        // définition du prococole
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        // Réponse plus tard dans 1 sec
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));
        List<AID> vendeurs = monAgent.getVendeurs();
        vendeurs.forEach(msg::addReceiver);
        // relancer le comportement pour fixer la date de remise au plus tard, les destinataires, ...
        this.reset(msg);
    }


    /**
     * methode lancee a la reception de chaque refus
     *
     * @param refuse refus recu
     */
    @Override
    protected void handleRefuse(ACLMessage refuse) {
        window.println("Agent " + refuse.getSender().getLocalName() + " refuse");
    }

    /**
     * methode lancee a la reception d'un message d'erreur (impossibilite de poursuivre la vente)
     *
     * @param failure erreur recue
     */
    @Override
    protected void handleFailure(ACLMessage failure) {
        if (failure.getSender().equals(myAgent.getAMS())) {
            // ERREUR : le destinataire n'existe pas
            window.println("Le destinataire n'existe pas...");
        } else
            window.println("Agent " + failure.getSender().getLocalName() + " a echoue");
    }

    /**
     * methode lancée si toutes les reponses sont arrivées ou si le temps est écoulé<br>
     * accepte la meilleure offre, calcul basé sur la notoriété la plus haute et le prix le plus bas à part égale ici<br>
     * n'accepte pas l'offre si dépasse le prix max fixé par l'acheteur
     *
     * @param responses   reponses recues
     * @param acceptances vecteur des messages à transmettre en retour aux réponses reçues
     * @see ContractNetInitiator#handleAllResponses(List, List)
     */
    @Override
    protected void handleAllResponses(List<ACLMessage> responses, List<ACLMessage> acceptances) {
        //catalog of journeys built from answers
        var catalogs = new JourneysList();
        //map <name to the agent (agence), Msg built to answer to it>
        Map<String, ACLMessage> reponses = new HashMap<>();
        for (ACLMessage ans : responses) {
            if (ans.getPerformative() == ACLMessage.PROPOSE) {
                JourneysList receivedCatalog = null;
                try {
                    receivedCatalog = (JourneysList) ans.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                if (receivedCatalog != null) {
                    catalogs.addJourneys(receivedCatalog);
                    monAgent.println("reçu de " + ans.getSender().getLocalName() + " : ");
                    monAgent.println(receivedCatalog.toString());
                }

            }
            var reply = ans.createReply();
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            acceptances.add(reply);
            reponses.put(ans.getSender().getLocalName(), reply);
        }
        monAgent.setCatalogs(catalogs);
        monAgent.println("j'ai bien recu les calalogues : ");
        monAgent.println(catalogs.toString());
        monAgent.println("je fais mon choix...");
        monAgent.computeComposedJourney(from, to, departure, preference);
        //map <name to the agent (agence), list of journeys to buy to it>
        Map<String, ArrayList<Journey>> voyagesAAcheter = new HashMap<>();
        var journey = monAgent.getMyJourney();
        journey.getJourneys().forEach(j ->
                voyagesAAcheter.compute(j.getProposedBy(),
                        (agence, list) -> {
                            if (list == null) list = new ArrayList<>();
                            list.add(j);
                            return list;
                        }));
        voyagesAAcheter.forEach((agence, journeys) -> {
            var msg = reponses.get(agence);
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            try {
                msg.setContentObject(journeys);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * methode lancee a la reception d'un message d'information (vente confirmee)
     *
     * @param inform message recu
     */
    @Override
    protected void handleInform(ACLMessage inform) {
        window.println("Agent " + inform.getSender().getLocalName() + " : " + inform.getContent());
    }

}
