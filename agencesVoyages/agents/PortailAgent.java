package agencesVoyages.agents;

import agencesVoyages.comportements.ContractNetVente;
import agencesVoyages.comportements.ContractNetVentePortail;
import agencesVoyages.data.ComposedJourney;
import agencesVoyages.data.Journey;
import agencesVoyages.data.JourneysList;
import agencesVoyages.launch.LaunchSimu;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.core.behaviours.ReceiverBehaviour;
import jade.domain.DFService;
import jade.domain.DFSubscriber;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.stream.Stream;

public class PortailAgent extends GuiAgent {

    /**
     * code shared with the gui to quit the agent
     */
    public static final int EXIT = 0;

    /**
     * catalog of the proposed journeys
     */
    private JourneysList catalog;
    /**
     * graphical user interface linked to the seller agent
     */
    private agencesVoyages.gui.PortailGui window;
    /**
     * topic from which the alert will be received
     */
    private AID topic;

    private ArrayList<AID> vendeursB2B;

    private String type;

    private AID customer;

    //Chemin pour le customer choisie par le portail
    private ComposedJourney customerJourney;

    // Initialisation de l'agent
    @Override
    protected void setup() {
        final Object[] args = getArguments(); // Recuperation des arguments
        catalog = new JourneysList();
        window = new agencesVoyages.gui.PortailGui(this);
        window.display();

        vendeursB2B = new ArrayList<>();
        detectAgences();

        if (args != null && args.length > 0) {
            type = (String) args[0];
        } else {
            type = "";
        }

        fromCSV2Catalog("agencesVoyages/"+ type+ ".csv");
        fromCSV2Catalog("agencesVoyages/"+ type+ "Autre.csv");

        AgentServicesTools.register(this, "travel agency", "portail");

        // attendre une demande de catalogue & achat
        var template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        addBehaviour(new ContractNetVentePortail(this, template, catalog));

    }

    /**
     * ecoute des evenement de type enregistrement en tant qu'agence aupres des pages jaunes
     */
    private void detectAgences() {
        var model = AgentServicesTools.createAgentDescription("travel agency", "seller");
        vendeursB2B = new ArrayList<>();

        //souscription au service des pages jaunes pour recevoir une alerte en cas mouvement sur le service travel agency'seller
        addBehaviour(new DFSubscriber(this, model) {
            @Override
            public void onRegister(DFAgentDescription dfd) {
                vendeursB2B.add(dfd.getName());
                window.println(dfd.getName().getLocalName() + " s'est inscrit en tant qu'agence : " + model.getAllServices().get(0));
            }

            @Override
            public void onDeregister(DFAgentDescription dfd) {
                vendeursB2B.remove(dfd.getName());
                window.println(dfd.getName().getLocalName() + " s'est desinscrit de  : " + model.getAllServices().get(0));
            }

        });

    }

    // Fermeture de l'agent
    @Override
    protected void takeDown() {
        // S'effacer du service pages jaunes
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            LaunchSimu.logger.log(Level.SEVERE, fe.getMessage());
        }
        LaunchSimu.logger.log(Level.INFO, "Agent Agence : " + getAID().getName() + " quitte la plateforme.");
        window.dispose();
    }

    /**
     * methode invoquee par la gui
     */
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == PortailAgent.EXIT) {
            doDelete();
        }
    }


    /**
     * display a msg on the window
     */
    public void println(String msg) {
        window.println(msg);
    }

    ///// GETTERS AND SETTERS
    public agencesVoyages.gui.PortailGui getWindow() {
        return window;
    }

    public List<AID> getVendeurs() {
        return vendeursB2B;
    }

    public void setCatalogs(JourneysList catalogs) {
        this.catalog = catalogs;
    }

    public void computeComposedJourney(String from, String to, int departure, String preference) {
        final List<ComposedJourney> journeys = new ArrayList<>();
        ComposedJourney myJourney = null;
        //recherche de trajets ac tps d'attentes entre via = 60mn
        final boolean result = catalog.findIndirectJourney(from, to, departure, 60, new ArrayList<>(),
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
                        journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
                default -> journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
            }
            if(journeys.size() > 0){myJourney = journeys.get(0);   println("I choose this journey : " + myJourney);}else{println("no journey found");}

            customerJourney = myJourney;
            this.sendJourneyToCustomer(myJourney);

        }
    }

    /**
     * initialize the catalog from a cvs file<br>
     * csv line = origine, destination,means,departureTime,duration,financial
     * cost, co2, confort, nbRepetitions(optional),frequence(optional)
     *
     * @param file name of the cvs file
     */
    private void fromCSV2Catalog(final String file) {
        List<String> lines = null;
        try {lines = Files.readAllLines(new File(file).toPath());}
        catch (IOException e) {
            window.println("fichier " + file + " non trouve !!!");
        }
        if(lines!=null)
        {
            int nbLines = lines.size();
            for(int i=1; i<nbLines; i++){
                String[] nextLine = lines.get(i).split(",");
                String origine = nextLine[0].trim().toUpperCase();
                String destination = nextLine[1].trim().toUpperCase();
                String means = nextLine[2].trim();
                int departureDate = Integer.parseInt(nextLine[3].trim());
                int duration = Integer.parseInt(nextLine[4].trim());
                double cost = Double.parseDouble(nextLine[5].trim());
                int co2 = Integer.parseInt(nextLine[6].trim());
                int confort = Integer.parseInt(nextLine[7].trim());
                int nbRepetitions = (nextLine.length == 9) ? 0 : Integer.parseInt(nextLine[8].trim());
                int frequence = (nbRepetitions == 0) ? 0 : Integer.parseInt(nextLine[9].trim());
                Journey firstJourney = new Journey(origine, destination, means, departureDate, duration, cost,
                        co2, confort);
                firstJourney.setProposedBy(this.getLocalName());
                int nbPlaces = switch (file) {
                    case "agencesVoyages/bus.csv" -> 50;
                    case "agencesVoyages/carAutre.csv" -> 3;
                    case "agencesVoyages/busAutre.csv" -> 50;
                    case "agencesVoyages/car.csv" -> 3;
                    case "agencesVoyages/train.csv" -> 200;
                    default -> 0;
                };
                firstJourney.setPlaces(nbPlaces);
                window.println(firstJourney.toString());
                catalog.addJourney(firstJourney);
                if (nbRepetitions > 0) {
                    repeatJourney(departureDate, nbRepetitions, frequence, firstJourney);
                }
            }
        }
    }

    /**
     * repeat a journey on a sequence of dates into a catalog
     *
     * @param departureDate date of the first journey
     * @param nbRepetitions nb of journeys to add
     * @param frequence     frequency of the journeys in minutes
     * @param journey       the first journey to clone
     */
    private void repeatJourney(final int departureDate, final int nbRepetitions, final int frequence,
                               final Journey journey) {
        int nextDeparture = departureDate;
        for (int i = 0; i < nbRepetitions; i++) {
            final Journey cloneJ = journey.clone();
            nextDeparture = Journey.addTime(nextDeparture, frequence);
            cloneJ.setDepartureDate(nextDeparture);
            window.println(cloneJ.toString());
            catalog.addJourney(cloneJ);
        }
    }

    public void sendJourneyToCustomer(ComposedJourney journey) {
        if (customer != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(customer);
            try {
                msg.setContentObject(journey);
            } catch (IOException e) {
                e.printStackTrace();
            }
            send(msg);
        } else {
            println("No customer to send the journey to.");
        }
    }

    public void setCustomer(AID customer) {
        this.customer = customer;
    }

    // Récupère la journey choisie par le portail
    public ComposedJourney getMyJourney() {
        return customerJourney;
    }

}
