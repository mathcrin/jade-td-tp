package agencesVoyages.agents;

import agencesVoyages.comportements.ContractNetAchat;
import agencesVoyages.data.ComposedJourney;
import agencesVoyages.data.Journey;
import agencesVoyages.data.JourneysList;
import agencesVoyages.gui.TravellerGui;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ReceiverBehaviour;
import jade.domain.DFService;
import jade.domain.DFSubscriber;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.SubscriptionInitiator;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Journey searcher
 *
 * @author Emmanuel ADAM
 */
public class TravellerAgent extends GuiAgent {
    /**
     * code pour ajout de livre par la gui
     */
    public static final int EXIT = 0;
    /**
     * code pour achat de livre par la gui
     */
    public static final int BUY_TRAVEL = 1;

    /**
     * liste des vendeurs
     */
    private ArrayList<AID> vendeurs;

    /**
     * catalog received by the sellers
     */
    private JourneysList catalogs;

    /**
     * the journey chosen by the agent
     */
    private ComposedJourney myJourney;

    /**
     * topic from which the alert will be received
     */
    private AID topic;

    /**
     * gui
     */
    private TravellerGui window;

    /**
     * Initialisation de l'agent
     */
    @Override
    protected void setup() {
        this.window = new TravellerGui(this);
        window.setColor(Color.cyan);
        window.println("Hello! AgentAcheteurCN " + this.getLocalName() + " est pret. ");
        window.setVisible(true);

        vendeurs = new ArrayList<>();
        detectAgences();

        topic = AgentServicesTools.generateTopicAID(this, "TRAFFIC NEWS");
        //ecoute des messages radio
        addBehaviour(new ReceiverBehaviour(this, -1, MessageTemplate.MatchTopic(topic), true, (a, m)->{
            println("Message recu sur le topic " + topic.getLocalName() + ". Contenu " + m.getContent()
                    + " emis par :  " + m.getSender().getLocalName());
            if(topic.getLocalName().equals("TRAFFIC NEWS")){
                handleAlert(m.getContent());
            }
        }));

//        // Ecoute des messages contenant un ComposedJourney
//        addBehaviour(new CyclicBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msg = receive();
//                if (msg != null) {
//                    try {
//                        ComposedJourney journey = (ComposedJourney) msg.getContentObject();
//                        handleReceivedJourney(journey);
//                    } catch (UnreadableException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    block();
//                }
//            }
//        });

    }

    private void handleReceivedJourney(ComposedJourney journey) {
        println("ComposedJourney reçu: " + journey);
        // Stocker le ComposedJourney reçu
        myJourney = journey;
    }


    /**
     * ecoute des evenement de type enregistrement en tant qu'agence aupres des pages jaunes
     */
    private void detectAgences() {
        var model = AgentServicesTools.createAgentDescription("travel agency", "portail");
        vendeurs = new ArrayList<>();

        //souscription au service des pages jaunes pour recevoir une alerte en cas mouvement sur le service travel agency'seller
        addBehaviour(new DFSubscriber(this, model) {
            @Override
            public void onRegister(DFAgentDescription dfd) {
                vendeurs.add(dfd.getName());
                window.println(dfd.getName().getLocalName() + " s'est inscrit en tant que potail : " + model.getAllServices().get(0));
            }

            @Override
            public void onDeregister(DFAgentDescription dfd) {
                vendeurs.remove(dfd.getName());
                window.println(dfd.getName().getLocalName() + " s'est desinscrit de  : " + model.getAllServices().get(0));
            }

        });

    }

    // 'Nettoyage' de l'agent
    @Override
    protected void takeDown() {
        window.println("Je quitte la plateforme. ");
    }

    ///// SETTERS AND GETTERS

    /**
     * @return agent gui
     */
    public TravellerGui getWindow() {
        return window;
    }

    public void computeComposedJourney(final String from, final String to, final int departure,
                                       final String preference) {
        final List<ComposedJourney> journeys = new ArrayList<>();
        //recherche de trajets ac tps d'attentes entre via = 60mn
        final boolean result = catalogs.findIndirectJourney(from, to, departure, 60, new ArrayList<>(),
                new ArrayList<>(), journeys);

        if (!result) {
            println("no journey found !!!");
        }
        if (result) {
            //oter les voyages demarrant trop tard (1h30 apres la date de depart souhaitee)
            journeys.removeIf(j -> j.getJourneys().get(0).getDepartureDate() - departure > 90);
            switch (preference) {
                case "duration" -> {
                    Stream<ComposedJourney> strCJ = journeys.stream();
                    OptionalDouble moy = strCJ.mapToInt(ComposedJourney::getDuration).average();
                    final double avg = moy.orElse(0);
                    println("duree moyenne = " + avg);//+ ", moy au carre = " + avg * avg);
                    journeys.sort(Comparator.comparingInt(ComposedJourney::getDuration));
                }
                case "confort" -> journeys.sort(Comparator.comparingInt(ComposedJourney::getConfort).reversed());
                case "cost" -> journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
                case "duration-cost" ->
                        //TODO: replace below to make a compromise between cost and confort...
                        journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
                default -> journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
            }
            if (journeys.size() > 0) {
                journeys.get(0).getJourneys().get(0).setPreference(preference);
                myJourney = journeys.get(0);
                println("Je choisi ce trajet : " + myJourney);
            }else{
                println("Pas de trajet trouvé");
            }

        }
    }

    /**
     * get event from the GUI
     */
    @Override
    protected void onGuiEvent(final GuiEvent eventFromGui) {
        if (eventFromGui.getType() == TravellerAgent.EXIT) {
            doDelete();
        }
        if (eventFromGui.getType() == TravellerAgent.BUY_TRAVEL) {
            addBehaviour(new ContractNetAchat(this, new ACLMessage(ACLMessage.CFP),
                    (String) eventFromGui.getParameter(0), (String) eventFromGui.getParameter(1),
                    (Integer) eventFromGui.getParameter(2), (String) eventFromGui.getParameter(3)));
        }
    }

    /**
     * @return the vendeurs
     */
    public List<AID> getVendeurs() {
        return (ArrayList<AID>) vendeurs.clone();
    }


    /**
     * print a message on the window lined to the agent
     *
     * @param msg text to display in th window
     */
    public void println(final String msg) {
        window.println(msg);
    }

    /**
     * set the list of journeys
     */
    public void setCatalogs(final JourneysList catalogs) {
        this.catalogs = catalogs;
    }


    public ComposedJourney getMyJourney() {
        return myJourney;
    }

    private void handleAlert(String impactedRoute) {
        println("Alerte reçu: " + impactedRoute + ". Regarde si le trajet actuel est impacté.");
        println("Trajet actuel: " + myJourney);
        // Vérifier si le trajet actuel est impacté
        if (myJourney != null && myJourney.isImpactedBy(impactedRoute)) {
            println("Trajet actuel impacté.");
            println("Modification du trajet actuel: ");
            // Relancer une demande pour un nouveau trajet
            String from = myJourney.getJourneys().get(0).getOrigine();
            String to = myJourney.getJourneys().get(myJourney.getJourneys().size() - 1).getDestination();
            int departure = myJourney.getJourneys().get(0).getDepartureDate();
            String preference = myJourney.getJourneys().get(0).getPreference();

            addBehaviour(new ContractNetAchat(this, new ACLMessage(ACLMessage.CFP),
                    (String) from, (String) to,
                    (Integer) departure, (String) preference));

        } else {
            println("Trajet actuel non impacté.");
        }
    }

}
