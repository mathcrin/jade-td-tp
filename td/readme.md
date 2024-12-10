<meta name="description" content="Programming multi-agent in Java : use of an updated version of the Jade 
platform. Materials for Jade Tutorial : communication, protocols, votes, services, behaviors, ..." />

# Programming agents in Jade
## Tutorials

Set of exercises on multi-agent programming with the JADE platform.

To run these codes, it is necessary to import the library "[JadeUPHF.jar](https://github.com/EmmanuelADAM/JadeUPHF/blob/master/JadeUPHF.jar)".
This library is an update, with JAVA 17, of the last official version of  [Jade]
(https://jade.tilab.com) of Tilab and add functionalities to easier the implementation of agents.

The source of this new version, the library and notes, can be accessed from here : [JadeUPHF](https://emmanueladam.github.io/JadeUPHF/)".

_ Java >= 17 is required._


----


### protocoles

- Enchères, [englishAuctionOpenCry](https://github.com/EmmanuelADAM/jade/tree/english/td/englishAuctionOpenCry) : tant que le temps imparti n'est pas écoulé, écouter les propositions; sinon, adjuger la vente au plus offrant.
- Négociation,  [negociationWindow](https://github.com/EmmanuelADAM/jade/tree/english/td/negociationWindow) : une personne interagit avec un agent pour la négociation dans l'achat d'un produit.
- Agencement, [timeTable](https://github.com/EmmanuelADAM/jade/tree/english/td/timeTable) : problème de résolution de contraintes partagées par des enseignants, des groupes pour la création d'un emploi du temps.
- TicTacMoe : TicTacToe à 3. 1 personne contre 1 personne contre 1 agent. Ou 1 personne contre 2 agents.
  - Reprenez le code du [OXO](https://github.com/EmmanuelADAM/IntelligenceArtificielleJava/tree/master/MCTS/OXO) pour l'inclure dans un agent. 
  - L'interface est purement textuelle pour simplifier.
    - Les pions seront X, O et S
    - La grille sera en 4x4, ou en 5x3
    - gain lorsque 3 pions sont alignés.
- Solutions utilitaires, de bien être social : reprenez le code d'un vote simple (comme [BordaCount](https://github.com/EmmanuelADAM/jade/tree/english/protocols/bordaCount)). Cette fois l'initiateur (la place de marché) demande l'utilité des restos à chaque agent (une note) et opte pour la solution la plus utile, ou la solution de bien être social. 
- Mariages stables : 
  - Chaque agent transmet ses caractéristiques aux autres.  
  - Le but est de former des couples stables.
  - Chaque agent émet une préférence sur chaque autre (basé sur la longueur moyenne du petit doigt, écartement des yeux; pour l'instant on peut, il n'y a pas de petitdoigtophobie).
  - Solution centralisée : un agent "matrimonial" récupère les préférences des agents et applique l'algo de mariage stable classique : 
  1. Tous les agents sont célibataires
  2. Tant Que : il existe un célibataire c qui peut se proposer à un partenaire p (sans harcèlement, on ne peut se proposer 2x à un même agent) 
     3. Si p est célibataire
     4. Alors (c, p) forment un couple
     5. Sinon un couple (c', p) existe 
        6. Si p préfère c à c' Alors 
           7. (c, p) forment un couple
           8. c' devient célibataire 
        9. Sinon (c', p) restent en couple
  3. retourner aux agents les couples auxquels ils sont affectés.

