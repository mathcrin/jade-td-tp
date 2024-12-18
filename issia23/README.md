<meta name="description" content="Programming multi-agent in Java : use of an updated version of the Jade 
platform. Materials for Jade Tutorial : communication, protocols, votes, services, behaviors, ..." />

# Programming agents in Jade

[(web version)](https://emmanueladam.github.io/jade/)
[(french version)](https://github.com/EmmanuelADAM/jade/tree/master/)

Case study for ISSIA'23.

----
## Circular economy

Here is a scenario:
 - user have products
 - some products can be broken or become obsolescent 
 - several solutions exists:
   - tuto on website
   - repair coffees
   - spare parts stores
   - distributors.

- When the product doesn't work anymore; the user try to find a help : by itself, locally (repair coffee), in a larger 
region, and finally to the distributors.

- Solutions can exist on web, but the user can need a repair coffee to understand, to fix its product.

We can add these specification for a simple scenario : 
- some type of products (P1, P2, ...)
  - Cafetiere : 3 parts, approx. 40€,
  - LaveLinge : 3 parts, approx. 200€,
  - SourisOrdi : 3 parts, approx. 40€,
  - Aspirateur : 3 parts, approx. 100€,
  - LaveVaisselle : 4 parts, approx. 200€,
- some instances of products (Cafetiere10 (a Cafetiere with a price+10%, LaveVaisselle-30 a LaveVaisselle with a price - 30%, ...)
- a product have from 1 to 4 removable/fixable elements, we name them Cafetiere10-1, Cafetiere10-2, ....
- a breakdown :
  - is focused on 1 elt.
  - can be very light (0), easy(1), average (2), difficult(3), definitive (4)
    - this level of pb is detected during the reparation  
- **a user** has a skill for repairing, this skill can be of a level: 
  - 0 (unable to repair by itself and understand), 
  - 1 (can understand breakdown level up to 1, but only repair level 0),
  - 2 (can understand and repair breakdown level up to 2,
  - 3 (can understand and repair breakdown level up to 3.

- **repair coffees**
  - are specialised on type of product
  - have only 4 items of elt 1,2 or 3 and do not have elt4 for each product in which they are specialised
  - a repair coffee has a skill for repairing, this skill can be of 2 or 3.

- **spare parts** stores are specialised in some products and have only 10 items of elt 1,2 or 3 and do not have elt4

- **distributors** have not pb of products

- **prices**
  - *repair coffees* have a cost of 5 to 15€/elt   (second hand)
    - P1-elt1 can cost 7€ in repair coffee 1 and 12€ in repair coffee 3 
  - *spare parts* stores  have a cost of 20 to 40€/elt
  - *distributors* have a cost of *50-70*€/elt

- a user has a limited amount of money. 
- he/she search for a repair coffee adapted to its type of product
- chooses a repair coffee (the repair coffee can send an estimation of the cost, a date of rendez-vous, ...)
- go to the repair coffee to detect the failure, correct it if possible
  - possible if repair coffee has the element, and the skill
  - if impossible (failure on elt 4), let the product in the repair coffee 
- if not possible, ask for a element to the spare store and search another repair coffee having the skill
- if no element in the spare store, if too difficult, buy a new product if enough money

---
Draw and build some agents using the new Jade Library to simulate this behaviour.
- Generate a random object for the user agents, 
- and random elt in the store and coffee.
- 
---
Next repair coffee can interact between them to exchange piece.
 - this has a cost in time..1 day/elt

We add a second criteria, the time:
- flexible time for a rendez-vous, 
- flexible time for receiving a elt from the spare stores, a product from the distributors.

---
**Code**
- The proposed code is working. You can launch it via the class launch.Launch
- User receive products, repairCoffeAgents and PartstoreAgents receive parts (more for SparestoreAgents), distributors receive product
- A user starts a call for proposal to RepairCoffeeAgents. This CFP does nothing, it is just an example.