# Set-Game-Solver

## Overview
This program allows a user to upload a picture of a set board from the [Daily SET Puzzle](https://www.setgame.com/set/puzzle), and displays the board with colored rectangles around the cards of a set. 
For example, a user could upload the picture below.

![](SampleImages/Sample%20Input.JPG)

This program would then display the following.

![](SampleImages/Sample%20Output.png)

## Background

*Set* is a game I've spent many fun hours playing with my family, and after you get the hang of it you really start to enjoy it. The full rules are [here](https://www.setgame.com/sites/default/files/instructions/SET%20INSTRUCTIONS%20-%20ENGLISH.pdf), but here's a quick summary. You start with a deck of 81 unique cards. Each card has 4 attributes: color, shape, shading, and number of shapes. Each attribute has 3 variations. A card's color can either be red, green, or purple. The shape on the card can either be an oval, a diamond, or a squiggle. The shading of a card can either be full, partially filled, or open. And finally, a card can either have 1, 2, or 3 shapes on it. To start the game, 12 cards are placed on the playing surface. Players then try to find a set on the board. When a set is found, the three cards are given to the player and three cards are added from the deck. The game is continued until the deck is empty and there are no more sets. So what makes a set? A set consists of 3 cards in which for each attribute, all 3 cards have either the same variation, or all 3 cards have a different variation. Examples of sets and non-sets are in the above link.

