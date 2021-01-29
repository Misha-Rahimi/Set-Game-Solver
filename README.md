# Set-Game-Solver

## Overview
This program allows a user to upload a picture of a set board from the [Daily SET Puzzle](https://www.setgame.com/set/puzzle), and displays the board with colored rectangles around the cards of a set.

For example, a user could upload the picture below:

![](SampleImages/Sample%20Input.jpg)

This program would then display the following:

![](SampleImages/Sample%20Output.JPG)

A set comprises 3 cards, and is displayed by outlining each of the three cards with a unique color.

## How To Run This Program

Simply download this repository and run the SetSolver jar. You'll then be asked to navigate to an image file of a set board. You can go to the [Daily SET Puzzle](https://www.setgame.com/set/puzzle) website and use the snipping tool to save a picture of the set board (the board doesn't have to be perfectly centered in the picture, as long as the entire board is there). You could also simply use one of the Sample Set Boards in the *SampleImages* folder. After you find your image file, double-click on it, and that's it! You'll see the image you just chose, but now there'll be uniquely colored rectangles around the cards in every set on the board.

## Background

*Set* is a game I've spent many fun hours playing with my family, and after you get the hang of it you really start to enjoy it. The full rules are [here](https://www.setgame.com/sites/default/files/instructions/SET%20INSTRUCTIONS%20-%20ENGLISH.pdf), but here's a quick summary. You start with a deck of 81 unique cards. Each card has 4 attributes: color, shape, shading, and number of shapes. Each attribute has 3 variations. A card's color can either be red, green, or purple. The shape on the card can either be an oval, a diamond, or a squiggle. The shading of a card can either be full, partially filled, or open. And finally, a card can either have 1, 2, or 3 shapes on it. To start the game, 12 cards are placed on the playing surface. Players then try to find a set on the board. When a set is found, the three cards are given to the player and three cards are added from the deck. The game continues until the deck is empty and there are no more sets. So what makes a set? A set consists of 3 cards in which for each attribute, all 3 cards have either the same variation, or all 3 cards have a different variation. Examples of sets and non-sets are in the above link.

## Known Issues
This program won't work if a user uploads a Set Board with any green, red, or purple objects in the picture that aren't part of a card, such as the red buttons on the Daily Set Puzzle website.

## Future Improvements
Here are some things that I'd like to add to this project:
* Displaying sets- It's sometimes hard to make out all the different colors of rectangles and find each card in a set. I'd like to add a feature where whenever a user hovers their mouse over a card, it'll only show the other cards in its set and gray out the rest.
* Solve set boards that aren't from the Daily Set Puzzle Website- As of right now, this program only works on set boards from this website. I'd like to be able to play an actual game with 12 cards on a table, and be able to take a picture of the playing surface and find all the sets.
* Convert to an app- After completing the above item, I would turn this program into an app and be able to instantly find all the sets when playing a game with friends and family
