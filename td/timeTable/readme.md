# Jade :  Gestion d'un Emploi Du Temps 
*(benchmark asa)*

TP de résolution de problèmes de satisfaction de contraintes avec agents.

Prenons pour commencer un exemple "simple" de gestion d'emploi du temps.

Il met en jeu : enseignants, groupes d'étudiants et des créneaux horaires.

---
### Enoncé du problème A.
Selon le point de vue (enseignant, étudiant, gestionnaire de salle), le problème peut être défini ainsi :
- Trois groupes d'étudiants ($g_1$, $g_2$ et $g_3$) doivent suivre chacun deux enseignements de deux heures, répartis sur deux jours ($j_1$ et $j_2$), avec un enseignant ($e_1$, $e_2$ ou $e_3$).
- Trois enseignants ($e_1$, $e_2$ et $e_3$) doivent proposer chacun deux enseignements de deux heures, répartis sur deux jours ($j_1$ et $j_2$), à trois groupes d'étudiants ($g_1$, $g_2$ et $g_3$).
- Trois salles ($s_1$, $s_2$ et $s_3$) doivent accueillir chacune deux enseignements de deux heures, répartis sur deux jours ($j_1$ et $j_2$), avec un enseignant ($e_1$, $e_2$ ou $e_3$).

**Crenaux horaires** : le jour $j_1$ et le jour $j_2$ sont deux jours ouvrables de la semaine, de 8h à 18h, avec une pause de deux heures entre 12h et 14h. Pour simplifier, les créneaux sont limités à 8h-10h, 10h-12h, 14h-16h, 16h-18h.

**Contraintes** :
Il existe des contraintes supplémentaires :
- Les enseignants ont des indisponibilités pour certains créneaux. 
Les contraintes secondaires (2) peuvent être non respectées s'il n'y a pas d'autre solution :
  - $e_1$ ne peut enseigner : (1) le jour $j_1$ de 16h à 18h ; (2) le jour $j_2$ de 14h à 16h. 
  - $e_2$ ne peut enseigner : (1) le jour $j_2$ de 10h à 12h ; (2) le jour $j_1$ de 16h à 18h. 
  - $e_3$ ne peut enseigner : (1) le jour $j_1$ de 14h à 16h ; (2) le jour $j_2$ de 8h à 10h.

- Les trois salles s1, s2 et s3 sont disponibles pour ces enseignements, cependant elles sont soumises, elles aussi, à des contraintes :
  - la salle $s_1$ n'est pas disponible le jour $j_1$ de 10h à 12h (1),
  - la salle $s_2$ n'est pas disponible le jour $j_2$ de 16h à 18h (1) et de 8h à 10h (2),
  - la sale $s_3$ n'est pas disponible le jour $j_2$ de 16h à 18h (1) et le jour $j_1$ de 14h à 16h (2). 
  - Seules les salles $s_1$ et $s_2$ possèdent un vidéoprojecteur.

- Les enseignements possèdent également des contraintes matérielles ; par exemple :
    - il est nécessaire d'utiliser un vidéoprojecteur au moins une fois pour chaque enseignement pour chaque groupe.
 
---

**Approche du problème.**

Plusieurs approches sont possibles.
Par exemple : 
- Si $p_1$ et $p_2$ sont les poids associés aux respects des contraintes sur les jours (avec $p_1 + p_2 = 1$ et $p_1 > p_2$), 
- le degré de satisfaction d’un enseignant $e_i$ est défini par la fonction utilité :
$u_{e_i}(agenda) = (p_1 \times ok_{contrainte_1} + p_2 \times ok_{contrainte_2})$ avec $ok_{contrainte_i} =$ 0 ou 1 si la contrainte (i) est respectée ou non.
- Attention, ceci ne note que le point de vue de $e_i$, qui ne regarde pas les contraintes des indisponibilités des salles.

Pour un enseignant, il peut exister plusieurs solutions qui maximisent sa fonction utilité. Mais il se peut que cette valeur soit faible.
Et il se peut qu'il n'existe aucune solution qui maximise la fonction d'utilité de chaque enseignant, salle, groupe, ...


## Agentification 
  - Agentifiez le problème en définissant les voyelles AEIO.
    - il est possible de définir des agents pour les enseignants, les groupes, les salles, les créneaux, pour la coordination des enseignants, des salles, ...
  - Plus précisément, définissez les agents, leurs rôles, leurs comportements, leurs interactions.
    - Quel(s) mécanisme(s) de résolution utiliserez-vous ? (vote, négociation, adaptation, place de marché, ...)

## Spécification
  - Utilisez de préférence PlantUML pour générer les diagrammes de classes, d'états, d'activité et de séquence
  - Précisez les types comportements permettant l'implémentation des rôles que vous avez décrits. Vous ne donnerez que leurs noms et types (simples, cycliques, ...), leurs fonctionnements sont à décrire en texte simple.
  - Donnez les diagrammes de dialogue JADE entre les agents pour la résolution du problème de l'EDT en précisant les comportements impliqués dans les échanges.

## Implémentation
  - Implémentez le problème en JADE.
  - listez les solutions trouvées, les contraintes non respectées...
