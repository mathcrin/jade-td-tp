# Jade : Agents Sujet du TP

## Agence de voyages

---

Ces codes illustrent un petit cas d'étude permettant de manipuler le protocole de ContractNet.

Un voyageur souhaite aller d'un point `a` à un point `b` :

- il lance un appel d'offre à plusieurs agences de voyages,
- certaines sont spécialisées dans les bus, les trains ou les voitures.
- ces agences envoient leurs catalogues de voyages possibles
- le client fait son choix et peut combiner différentes offres selon ses critères (coûts, temps, émission de CO2, ...)


- Des classes sont déjà proposées :
    - La classe [AgenceAgent](https://github.com/EmmanuelADAM/jade/blob/master/agentsVoyage/agents/AgenceAgent.java) est
      une classe représentant une agence de voyage. Une agence dispose d'un catalogue de voyages qu'elle crée à partir
      d'un fichier csv (bus.csv, car.csv ou train.csv).
    - La
      classe [TravellerAgent](https://github.com/EmmanuelADAM/jade/blob/master/agentsVoyage/agents/TravellerAgent.java)
      représente le client, qui émet l'appel d'offres et effectue sont choix, **à coder**
    - La classe [AlertAgent](https://github.com/EmmanuelADAM/jade/blob/master/agentsVoyage/agents/AlertAgent.java)
      représente un agent capable d'émettre un appel radio (broadcast) signalant une alerte sur un tronçon de route.
    - La classe [LaunchSimu](https://github.com/EmmanuelADAM/jade/blob/master/agentsVoyage/launch/LaunchSimu.java) lance
      un client, 3 agents agence de voyages spécialisée, et 1 agent d'alertes.
    - La
      classe [ContractNetVente](https://github.com/EmmanuelADAM/jade/blob/master/agentsVoyage/comportements/ContractNetVente.java)
      code le comportement de réponse à un appel d'offre, il consiste simplement à envoyer le catalogue complet des
      voyages, il peut être optimisé.
    - La
      classe [ContractNetAchat](https://github.com/EmmanuelADAM/jade/blob/master/agentsVoyage/comportements/ContractNetAchat.java)
      code le comportement de lancement et de gestion d'un appel d'offres.

- Dans le package [agencesVoyages.data](https://github.com/EmmanuelADAM/jade/tree/master/agentsVoyage/data) se trouvent
  les classes qui consruisent les chemins possibles.
- Dans le package [agencesVoyages.gui](https://github.com/EmmanuelADAM/jade/tree/master/agentsVoyage/gui) se trouvent
  les classes qui consruisent les feneêtre de dialogue avec les agencesVoyages.agents.

Le code utilise

- la librairie opencsv ([http://opencsv.sourceforge.net](http://opencsv.sourceforge.net)) version 3.9 attachée à ce
  dossier.
- la librairie jadeUPHF.jar à télécharger ici :  "[JadeUPHF.jar](https://github.com/EmmanuelADAM/JadeUPHF/blob/master/JadeUPHF.jar)".

-----
Le code s'exécute tel quel, mais le client ne peut effectuer qu'un choix par durée la plus courte parmi les voyages
proposés.

- **Proposez et codez** la décrémentation du nb de places par voyage (on pose 3 places par trajet en voiture, 50 en bus,
  et 200 places par trajet en train),
- **Proposez et codez** le comportement de réaction suite à la réception d'un message d'alerte sur un tronçon donné;
    - pour les agences (disparition des trajets impactés),
    - pour le client (relance d'une demande de trajet si impacté, achat des billets permettant de compléter le trajet).

> si code correct => + 5 points

-----

<span style='color:red'>**Enchères, choix** : </span>

- **enchères** : un client impacté par une route bloquée peut se retrouver avec des tickets achetés non remoursables.
    - pour chaque billet acheté,
        - il lance une enchère hollandaise, partant du prix d'achat, diminuant jusqu'à 1€. Si aucun acheteur, le billet
          est abandonné.
        - ou il lance une enchère anglaise (1 un tour (**facile**, mais pas de bonus de points)), en n tours
          classiquement : l'enchère grimpe au fur et à mesure des annonces et stoppe lorsqu'il n'y en a plus. le
          vainqueur étant celui ayant donné le prix le plus haut)
    - un voyageur peut également devoir abandonner son déplacement. Proposez une adaptation de sa fenêtre afin de lui
      permettre de lancer des enchères.
        - un trajet peut subitement devenir très prisé. Un voyageur peut décider de revendre un trajet plutôt que de le
          réaliser en faisant un bénéfice. Implémentez une enchère ascendante en un tour (cf. enchère de Vikrey)

> si code enchères correct => + 5 points (+ 4 points par autre type d'enchère)
>
> si code de modification d'une fenêtre client correct => + 3 points

---

Pour information, les trajets possibles sont les suivants :

- en bus (en noir dans la figure ci-dessous), entre les villes a<->b, b<->c, b<->d, c<->d, c<>-e, d<->e, e<->f,
- en train (en bleu dans la figure ci-dessous), entre les villes a<->d, d<->f,
- en voiture (covoiturage) (en rouge dans la figure ci-dessous), entre les villes a<->f, c<->f
- les coûts, vitesses, émissions de co2, confort ... dépendent du moyen utilisé


<!-- note, pour plantUml, ci-dessous retirer les espaces entre des tirets -- et le signe > 
```
@startuml trajetsV1
hide empty description
rectangle A
rectangle B
rectangle C
rectangle D
rectangle E
rectangle F
A <-- > B
A <-[#blue]> D
B <-> C
B <-> D
C <-- > D
C <-> E
D <-> E
D <-[#blue]> F
A <-[#red]> F 
C <-[#red]> F 
E <-- > F


@enduml 
```

-->

<img src="trajetsV1.png" alt="reseau v2" height="300"/>



---

## Autres agences

- Ajoutez 2 autres agences : l'une pour les bus utilisant les voyages de busAutre.csv, l'autre pour les voitures
  utilisant le fichier carAutre.csv
- Créez une classe PortailAgence. Une classe portail agence se comporte comme une agence, mais ne dispose pas de moyens
  de locomotion. Le client n'envoie plus de demande auprès des agences, mais auprès des portails.
- Créez un agent PortailBus qui sert d'intermédiaire entre les clients et les bus, et un agent PortailCar qui fait de
  même pour les agences de voiture. L'agent agence de train, se définit lui-même comme une agence..

- un achat auprès d'un portail est répercuté au niveau de l'agence.

> si code portail correct => + 5 points

Voici la nouvelle offre de voyage (en orange pour la 2e agence de voiture, en gris pour la 2e agence de bus)

<!-- note, pour plantUml, ci-dessous retirer les espaces entre deux tirets -- et le signe > 
```
@startuml trajetsV2
hide empty description
rectangle A
rectangle B
rectangle C
rectangle D
rectangle E
rectangle F
A <-- > B
A <--[#grey]> B
A <-[#blue]> D
B <-> C
B <-[#grey]> C
B <-> D
B <-[#grey]> D
C <-- > D
C <--[#grey]> D
C <-> E
C <-[#grey]> E
D <-> E
D <-[#grey]> E
D <-[#blue]> F
A <-[#red]> F
C <-[#red]> F
E <-- > F
B <--[#orange]> F
A <--[#orange]> E


@enduml
```

-->

<img src="trajetsV2.png" alt="reseau v2" height="300"/>


---
### Rapport à rendre

Vous associerez votre remise de codes d'un court rapport contenant :
- un diagramme de classes, contenant les classes sur les agents et leurs comportements
- de diagrammes de séquences détaillant les différents modes :
    - recherche classique de voyages
    - recherche via les agences "portails"
- d'un diagramme d'états pour le mode d'enchère proposé

Vous pouvez vous baser sur les exemples de diagramme illustrant les codes des TDs ; ainsi que sur les codes 
PlantUML associé si vous souhaitez uitiliser cet outil pour la création de diagrammes.


---

## Confiance

**Confiance dans le service**

- Ajoutez maintenance une notion de confiance envers le réseau routier...
    - pour chaque alerte, ajoutez une valeur de confiance (confiance dans l'alerte "problème A-B", ..... ,"problème E-F")
    - lorsqu'une alerte est tenue pour vraie, les trajets sont retirés temporairement des catalogues (reçus ou transmis)
    - la confiance en l'alerte décroit avec le temps : on peut supposer l'information comme étant obsolète
    - à chaque "top", la confiance envers une alerte décroit : 
      - $confiance_A \gets confiance_A \times \epsilon$, avec $epsilon \in [Ø,1]$,
      - $epsilon = 0$ signifie que l'agent ne croit à aucune alerte,
      - $epsilon = 1$ signifie que l'agent ne remet jamais en cause une alerte.
      - si $confiance_A < seuil$, alors $confiance_A \gets 0$
      - si $confiance_A \leq 0$, alors l'alerte $A$ est retirée des connaissances de l'agent
    - un acheteur qui se voit proposer un déplacement sur un axe décidera de risquer de prendre cet axe ou non selon la
      confiance qu'il accorde à l'alerte sur cet axe (par un tirage aléatoire, si le nombre est dessous la confiance 
      accordée, l'alerte est considérée comme vraie).
    - une agence choisira de même de proposer dans son catalogue les trajets selon la confiance accordée aux alertes 
      sur ceux-ci.

> si code "Confiance dans le service" correct => + 5 points

**Confiance envers l'autre**

- Ajoutez une notion d'évaluation aux agences, sur une valeur de 0 à 10.
    - Un agent peut maintenant avoir une nature courageuse ou prudente ou neutre.
    - Le courageux ne tient pas compte des avis et prendra l'offre la plus intéressante quelque soit l'évaluation de
      l'agence.
    - Le prudent se basera plutôt sur l'évaluation que sur l'intérêt de l'offre (sur un ratio de 90/10) : si le confort
      est demandé et qu'un trajet en voiture est proposé par une agence de faible renommée, le voyageur préfèrera
      éventuellement prendre le bus proposé par une agence plus renommée.
    - Le neutre effectue un ratio 50/50 sur la renommée et l'intérêt de l'offre pour faire son choix.

> si code "Confiance envers l'autre" correct => + 5 points

---

# Rendu du TP : 
Diaggrame de classe avec plant UML

```plantuml:
@startuml

package agencesVoyages.agents {
class AgenceAgent {
+ setup() : void
+ takeDown() : void
+ onGuiEvent(GuiEvent guiEvent) : void
+ fromCSV2Catalog(String file) : void
+ repeatJourney(int departureDate, int nbRepetitions, int frequence, Journey journey) : void
+ println(String msg) : void
+ getWindow() : AgenceGui
+ handleAlert(String impactedRoute) : void
}

    class PortailAgent {
        + setup() : void
        + takeDown() : void
        + onGuiEvent(GuiEvent guiEvent) : void
        + fromCSV2Catalog(String file) : void
        + repeatJourney(int departureDate, int nbRepetitions, int frequence, Journey journey) : void
        + println(String msg) : void
        + getWindow() : PortailGui
        + detectAgences() : void
        + computeComposedJourney(String from, String to, int departure, String preference) : void
        + sendJourneyToCustomer(ComposedJourney journey) : void
        + setCustomer(AID customer) : void
        + getMyJourney() : ComposedJourney
    }

    class AlertAgent {
        + setup() : void
        + takeDown() : void
        + onGuiEvent(GuiEvent eventFromGui) : void
        + println(String msg) : void
    }

    class TravellerAgent {
        + setup() : void
        + takeDown() : void
        + onGuiEvent(GuiEvent eventFromGui) : void
        + println(String msg) : void
        + computeComposedJourney(String from, String to, int departure, String preference) : void
        + getVendeurs() : List<AID>
        + setCatalogs(JourneysList catalogs) : void
        + getMyJourney() : ComposedJourney
        + handleAlert(String impactedRoute) : void
    }
}

package agencesVoyages.comportements {
class ContractNetVente {
+ handleCfp(ACLMessage cfp) : ACLMessage
+ handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) : ACLMessage
+ handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) : void
}

    class ContractNetVentePortail {
        + handleCfp(ACLMessage cfp) : ACLMessage
        + handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) : ACLMessage
        + handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) : void
    }

    class ContractNetAchat {
        + handleRefuse(ACLMessage refuse) : void
        + handleFailure(ACLMessage failure) : void
        + handleAllResponses(List<ACLMessage> responses, List<ACLMessage> acceptances) : void
        + handleInform(ACLMessage inform) : void
    }

    class ContractNetAchatPortail {
        + handleRefuse(ACLMessage refuse) : void
        + handleFailure(ACLMessage failure) : void
        + handleAllResponses(List<ACLMessage> responses, List<ACLMessage> acceptances) : void
        + handleInform(ACLMessage inform) : void
    }
}

AgenceAgent --> PortailAgent : Communicates with
PortailAgent --> TravellerAgent : Communicates with
AlertAgent --> AgenceAgent : Sends alerts to
AlertAgent --> TravellerAgent : Sends alerts to

AgenceAgent --> ContractNetVente
PortailAgent --> ContractNetVentePortail
TravellerAgent --> ContractNetAchat
PortailAgent --> ContractNetAchatPortail

@enduml
```
![img.png](img.png)

Diagramme de séquence pour la recherche classique de voyages

```plantuml:
@startuml
actor "Voyageur" as V
participant "AgentVoyageur" as AV
participant "AgentAgence" as AA
participant "ContractNetAchat" as CNA
participant "ContractNetVente" as CNV

V -> AV: Initier recherche
AV -> CNA: Envoyer CFP (Appel d'Offres)
CNA -> AA: Transférer CFP
AA -> CNV: Traiter CFP
CNV -> AA: Proposer voyages
AA -> CNA: Envoyer proposition
CNA -> AV: Recevoir proposition
AV -> CNA: Accepter proposition
CNA -> AA: Transférer acceptation
AA -> CNV: Traiter acceptation
CNV -> AA: Confirmer vente
AA -> CNA: Envoyer confirmation
CNA -> AV: Recevoir confirmation
@enduml
```

![img_2.png](img_2.png)


Diagramme de séquence pour la recherche via les agences "portails"

```plantuml:
@startuml
actor "Voyageur" as V
participant "AgentVoyageur" as AV
participant "AgentPortail" as AP
participant "AgentAgence" as AA
participant "ContractNetAchatPortail" as CNAp
participant "ContractNetVentePortail" as CNVp
participant "ContractNetVente" as CNV

V -> AV: Initier recherche
AV -> CNAp: Envoyer CFP (Appel d'Offres)
CNAp -> AP: Transférer CFP
AP -> CNVp: Traiter CFP
CNVp -> AP: Proposer voyages
AP -> CNAp: Envoyer proposition
CNAp -> AV: Recevoir proposition
AV -> CNAp: Accepter proposition
CNAp -> AP: Transférer acceptation
AP -> CNVp: Traiter acceptation
CNVp -> AP: Confirmer vente
AP -> CNAp: Envoyer confirmation
CNAp -> AV: Recevoir confirmation
@enduml
```
![img_1.png](img_1.png)

Diagramme d'états pour le mode d'enchère proposé

```plantuml:
@startuml
[*] --> Initialisation
Initialisation : Setup de l'agent
Initialisation --> AttenteProposition : Attente de proposition

AttenteProposition --> PropositionReçue : Proposition reçue
PropositionReçue --> EvaluationProposition : Évaluation de la proposition

EvaluationProposition --> PropositionAcceptée : Proposition acceptée
EvaluationProposition --> PropositionRejetée : Proposition rejetée

PropositionAcceptée --> ConfirmationVente : Confirmation de la vente
PropositionRejetée --> AttenteProposition : Retour à l'attente de proposition

ConfirmationVente --> [*] : Fin de l'enchère

@enduml
```
![img_3.png](img_3.png)