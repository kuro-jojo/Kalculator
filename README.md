# Kalculator 

Une application android permettant de faire des opérations arithmétiques et la conversion de devise.


## 1 Diagramme de classe


![diagramme de classe](https://i.postimg.cc/g0qVyvLd/Kalculator.jpg)
FIGURE2 – Diagramme de classe du convertisseur

## 2 Calculatrice

Nous avons construis une calculette qui permet d’effectuer les 4 opérations de base,
ainsi que le pourcentage. Une fonctionnalité de reprise (effacer un élément) et de reset
(remettre à zéro la calculatrice) a aussi été implémentée (cf figure 7). Au dessus de la
calculette, il y a deux boutons : celui à gauche permet d’accéder à la partie conversion
de devise de l’application (cf figure 9) , et celui à droite, à l’historique des calculs réalisés
par la calculatrice.


![accueil](https://i.postimg.cc/Nj2bX9xf/launch.png)
FIGURE3 – Page d’accueil de l’application
```

![calcul](https://i.postimg.cc/prNP2nkh/calcul.png)
FIGURE4 – Exemple de calcul réalisable par la calculette


![division](https://i.postimg.cc/h40Y4d2k/zero.png)
FIGURE5 – Une division par zéro

### 2.2 Historique de calcul

L’historique contient tous les calculs qui ont été réalisé depuis le lancement de l’ap-
plication. Il est possible aussi d’effacer tout l’historique via un bouton dès qu’un premier
calcul a été réalisé.
Avant de procéder à la réalisation, un boîte de dialogue apparaît pour demander une
confirmation (cf figure 8).

![history](https://i.postimg.cc/prnyCHc8/history.png)

FIGURE7 – Historique des calculs


## 3 Convertisseur de devise

Pour réaliser ce convertisseur, on a utilisé une API fourni par **apiLayer** (l’abonne-
ment gratuit nous fourni un nombre de 100 requêtes par mois ; après cela, il y a des
erreurs au niveau de l’application). L’API nous fourni la liste des monnaies et aussi le
taux d’échange entre deux monnaies.
L’envoi des requêtes a nécessité l’utilisation de **Threads** (sans ça il y a des crashs),
ce qui fait qu’il y a parfois des temps de latence entre la demande de l’utilisation et la
réponse de l’API.

```
![currency](https://i.postimg.cc/bwR0kxtP/currency.png)

FIGURE9 – Convertisseur de devise
```

