import java.awt.*;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SetSolver {
    static Card[][] cardAttributes = new Card[3][4];
    //delete when upload
    static StringBuilder testCase = new StringBuilder();
    static ArrayList<Card[]> finalSets = new ArrayList<>();
    static BufferedImage img;

    public static void main(String[] args) throws Exception {
        //User selects file containing the image of the board
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Choose Image File");
        jf.showOpenDialog(null);
        File file = jf.getSelectedFile();
        img = ImageIO.read(file);

        deconstructImage();
        findSets();
        displaySets();
        //checkCards();
    }

    public static void displaySets() {
        Paint.image = img;
        Paint.displaySets();
    }

    //delete when upload
    public static void testCases() {
        boolean canBeSet;
        for(int i=0; i<10; i++) {
            for(int j=i+1; j<11; j++) {
                for (int k=j+1; k<12; k++) {
                    canBeSet = isSet(cardAttributes[i/4][i%4], cardAttributes[j/4][j%4], cardAttributes[k/4][k%4]);

                    if(canBeSet) {
                        testCase.append(i + 1).append(",").append(j + 1).append(",").append(k + 1).append("-");
                    }
                }
            }
        }
    }

    //delete when upload
    public static void checkCards() {
        for(int i=0; i<3; i++) {
            for(int j=0; j<4; j++) {
                System.out.print(cardAttributes[i][j].getColor() + " " +
                        cardAttributes[i][j].getShape() + " " +
                        cardAttributes[i][j].getShading() + " " +
                        cardAttributes[i][j].getNumber());

                System.out.println();
            }
        }
    }

    /**
     * Determines the x and y coordinate ranges that the 12 cards lie in using the determineCardBounds method, then
     * loops through the 12 regions the cards are in. For each card, the x and y coordinate ranges that the actual
     * colored shapes lie in are determined again using the determineCardBounds method, then the deconstructCard
     * method is called on this region.
     * @throws InvalidBoardException when coordinates extend past the image
     */
    public static void deconstructImage() throws InvalidBoardException {
        int width, height, loopXLeft, loopXRight, loopYTop, loopYBottom;

        /*
        The boardBounds object contains the x and y coordinates for the first occurrence of a colored pixel when
        traveling to the center of the board from the left, right, top, and bottom edges
        */
        CardBounds boardBounds = determineCardBounds(0, img.getWidth(), 0, img.getHeight(), img);

        //boardBounds.increaseHeight(.05);

        /*
        The boardBounds object currently outlines a rectangular region bordering the edges of the shapes within the
        cards and not the cards themselves, and dividing the region into 12 identical regions might split some cards
        incorrectly. The bounds of the large region on the left and right will continually grow larger until the
        borders between the 12 regions don't encounter colored pixels.
         */
        double percentIncrease = 0.0;
        int anchor = -1;
        CardBounds confirmBounds = boardBounds;

        while (!boardBoundsCorrect(confirmBounds, img)) {
            anchor *= -1;
            if (anchor == 1) percentIncrease += .01;
            confirmBounds = boardBounds.increaseWidth(percentIncrease, anchor);
        }

        confirmBounds.increaseHeight(.05);

        boardBounds = confirmBounds;

        height = boardBounds.getYBottom() - boardBounds.getYTop();
        width = boardBounds.getXRight() - boardBounds.getXLeft();

        //System.out.println("xLeft " + cb.getXLeft() + " xRight " + cb.getXRight() + " yTop " + cb.getYTop() + " bottom " + cb.getYBottom());

        //Loops through all 12 sections of the board
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                //The x and y coordinates outlining the region on the board containing a single card
                loopXLeft = width / 4 * col + boardBounds.getXLeft();
                loopXRight = width / 4 * (col + 1) - 1 + boardBounds.getXLeft();
                loopYTop = height / 3 * row + boardBounds.getYTop();
                loopYBottom = height / 3 * (row + 1) - 1 + boardBounds.getYTop();

                //Determine y coordinate of the leftmost point of the shape in the card
                int leftMostPoint = determineLeftOfShape(loopXLeft, loopXRight, loopYTop, loopYBottom, img);

                //CardBounds object outlining the shapes in the card themselves
                CardBounds cardBounds = determineCardBounds(loopXLeft, loopXRight, loopYTop, loopYBottom, img);

                //System.out.println("individual shapes bounded at left " + cardBounds.getXLeft() + " right " + cardBounds.getXRight() + " up " + cardBounds.getYTop() + " down " + cardBounds.getYBottom());

                Card card = deconstructCard(cardBounds, leftMostPoint, img);

                cardAttributes[row][col] = card;
            }
        }
    }

    /**
     * Determines whether a CardBounds object properly outlines the region of the board.
     * @param cardBounds CardBounds object outlining a region on the board.
     * @param img BufferedImage object of the board
     * @return When divided into 12 equal regions, if the borders between the regions have any colored pixels, return
     * false, otherwise return true;
     * @throws InvalidBoardException if the coordinates in cardBounds aren't on the image
     */
    public static boolean boardBoundsCorrect(CardBounds cardBounds, BufferedImage img) throws InvalidBoardException {
        int xLocation;
        for (int col = 0; col < 4; col++) {
            xLocation = ((cardBounds.getXRight() - cardBounds.getXLeft()) / 4) * col + cardBounds.getXLeft();
            if (xLocation >= img.getWidth()) throw new InvalidBoardException();
            for (int row = cardBounds.getYTop(); row < cardBounds.getYBottom(); row++) {
                if (!(colorOfPixel(img.getRGB(xLocation, row)) == CardColor.OTHER)) return false;
            }
        }
        return true;
    }

    public static Card deconstructCard(CardBounds cardBounds, int yLeftMostOfShape, BufferedImage img) {
        Card card = new Card();
        int xLeftOfShape = cardBounds.getXLeft();
        int xRightOfShape = cardBounds.getXRight();
        int yTopOfShape = cardBounds.getYTop();
        int yBottomOfShape = cardBounds.getYBottom();

        //Determine shading of card
        int numberColoredPixels = 0;
        int pixel;
        int height = yBottomOfShape - yTopOfShape;
        int width = xRightOfShape - xLeftOfShape;
        CardColor previousColor;

        for (int i = 0; i < 2; i++) {
            previousColor = CardColor.OTHER;
            for (int y = yTopOfShape - (height) / 10; y < yBottomOfShape + (height) / 10; y++) {
                pixel = img.getRGB(((width) / 2 + xLeftOfShape) + i * (width) / 4, y);
                if (!(colorOfPixel(pixel) == CardColor.OTHER)) numberColoredPixels++;
                if (!(colorOfPixel(pixel) == previousColor)) {
                    previousColor = colorOfPixel(pixel);
                }
            }
            //System.out.println(((width) / 2 + xLeftOfShape) + i * (width) / 4);
            //if less than 10 pixels were colored, the x location was likely in between two figures and it needs to be moved either right or left
            if (numberColoredPixels >= height / 10) break;
            else {
                //System.out.println("was repeated");
                numberColoredPixels = 0;
            }
        }

        if(numberColoredPixels < .3 * height) card.setShading(CardShading.EMPTY);
        else if(numberColoredPixels > .75 * height) card.setShading(CardShading.FULL);
        else card.setShading(CardShading.PARTIALLY);
        //shadingValues.add((double) numberColoredPixels / height);
        //System.out.println((double) numberColoredPixels / height);
        //System.out.println("num colored pixels " + numberColoredPixels + " height " + height);

        //Determine number of shapes on card
        if((width) < (height) * .75) card.setNumber(1);
        else if((width) > (height) * 1.25) card.setNumber(3);
        else card.setNumber(2);
        /*System.out.println("width " + width + " height " + height);
        System.out.println("to determine number of shapes " + (double) height / width);*/

        //Determine shape of card
        int xLocDivot = 0;
        int yCenterOfShape = (height)/2 + yTopOfShape;

        double tolerance = height*.3;
        int newBottom = (int) (yCenterOfShape + tolerance);
        int newTop = (int) (yCenterOfShape - tolerance);

        for (int y = newBottom; y > newTop; y--) {
            for (int x = xLeftOfShape; x < xRightOfShape; x++) {
                pixel = img.getRGB(x, y);
                if (!(colorOfPixel(pixel) == CardColor.OTHER)) {
                    //determines the x-location of the "divot," or where left side of the shape is furthest from the edge
                    //only checks within a 30% tolerance of the height from the center, otherwise the divot would always be either the top of bottom of the shape
                    if (x > xLocDivot) {
                        xLocDivot = x;
                    }
                    break;
                }
            }
        }

        //if the y-cord of the leftmost edge is within 10% of the height of the shape from the center in either direction, it's a diamond


        if(yLeftMostOfShape > yCenterOfShape-(height)*.1 && yLeftMostOfShape < yCenterOfShape+(height)*.1) {
            card.setShape(CardShape.DIAMOND);
        }
        else {
            //if the difference between the depth of the shape and the leftmost edge of the shape is greater than 5% of the height of the shape, the shape is a squiggle
            if ((xLocDivot - xLeftOfShape) > (height) * .05) {
                card.setShape(CardShape.SQUIGGLE);
            }
            else {
                card.setShape(CardShape.OVAL);
            }
        }


        //Determine color of shape
        int numRedPixels = 0;
        int numGreenPixels = 0;
        int numPurplePixels = 0;
        int yMid = width / 2 + cardBounds.getYTop();

        for (int x = xLeftOfShape - 20; x < xRightOfShape  + 20; x++) {
            pixel = img.getRGB(x, yMid);

            if(colorOfPixel(pixel) == CardColor.RED) numRedPixels++;
            if(colorOfPixel(pixel) == CardColor.GREEN) numGreenPixels++;
            if(colorOfPixel(pixel) == CardColor.PURPLE) numPurplePixels++;
        }
        if(numRedPixels > numGreenPixels && numRedPixels > numPurplePixels) card.setColor(CardColor.RED);
        else if(numGreenPixels > numRedPixels && numGreenPixels > numPurplePixels) card.setColor(CardColor.GREEN);
        else card.setColor(CardColor.PURPLE);

        card.setCardBounds(cardBounds);

        return card;
    }

    public static CardBounds determineCardBounds(int loopXLeft, int loopXRight, int loopYTop, int loopYBottom, BufferedImage img) {
        int xLeft = img.getWidth();
        int xRight = 0;
        int yTop = img.getHeight();
        int yBottom = 0;
        int coloredPixelsCount = 0;

        for (int x = loopXLeft; x < loopXRight; x++) {
            for (int y = loopYTop; y < loopYBottom; y++) {
                if (!(colorOfPixel(img.getRGB(x, y)) == CardColor.OTHER)) {
                    coloredPixelsCount++;
                    if (coloredPixelsCount > 5) {
                        if (x < xLeft) xLeft = x;
                        if (x > xRight) xRight = x;
                        if (y < yTop) yTop = y;
                        if (y > yBottom) yBottom = y;
                    }
                } else coloredPixelsCount = 0;
            }
        }

        return new CardBounds(xLeft, xRight, yBottom, yTop - 5);
    }

    public static int determineLeftOfShape(int xLeft, int xRight, int yTop, int yBottom, BufferedImage img) {
        int pixel;
        int xLocLeftOfShape = xRight;
        int yLocLeftOfShape= 0;
        for (int y = yBottom; y > yTop; y--) {
            for (int x = xLeft; x < xRight; x++) {
                pixel = img.getRGB(x, y);
                if (!(colorOfPixel(pixel) == CardColor.OTHER)) {
                    //determine the left-most edge of the shape
                    if (x < xLocLeftOfShape) {
                        xLocLeftOfShape = x;
                        yLocLeftOfShape = y;
                    }
                }
            }
        }
        return yLocLeftOfShape;
    }

    public static boolean isSet(Card card1, Card card2, Card card3) {
        return colorFitsSet(card1, card2, card3) && numberFitsSet(card1, card2, card3) && shapeFitsSet(card1, card2, card3) && shadingFitsSet(card1, card2, card3);
    }

    public static boolean colorFitsSet(Card card1, Card card2, Card card3) {
        return (card3.getColor() == card1.getColor() && card3.getColor() == card2.getColor() && card1.getColor() == card2.getColor()) ||
                (card3.getColor() != card1.getColor() && card3.getColor() != card2.getColor() && card1.getColor() != card2.getColor());
    }

    public static boolean numberFitsSet(Card card1, Card card2, Card card3) {
        return (card1.getNumber() == card2.getNumber() && card1.getNumber() == card3.getNumber() && card2.getNumber() == card3.getNumber()) ||
                (card1.getNumber() != card2.getNumber() && card1.getNumber() != card3.getNumber() && card2.getNumber() != card3.getNumber());
    }

    public static boolean shapeFitsSet(Card card1, Card card2, Card card3) {
        return (card1.getShape() == card2.getShape() && card1.getShape() == card3.getShape() && card2.getShape() == card3.getShape()) ||
                (card1.getShape() != card2.getShape() && card1.getShape() != card3.getShape() && card2.getShape() != card3.getShape());
    }

    public static boolean shadingFitsSet(Card card1, Card card2, Card card3) {
        return (card1.getShading() == card2.getShading() && card2.getShading() == card3.getShading() && card1.getShading() == card3.getShading()) ||
                card1.getShading() != card2.getShading() && card2.getShading() != card3.getShading() && card1.getShading() != card3.getShading();
    }

    /*
     * This method will loop through the 3D array containing the attributes of all the cards to determine the sets
     * This method will go through every 3 card combination. For each attribute, if all three cards of the same
     * value or all three cards have a different value, there is a set and the set is printed.
     */
    public static void findSets() {
        boolean canBeSet;
        StringBuilder setsFound = new StringBuilder("The following are sets:\n");
        for(int i=0; i<10; i++) {
            for(int j=i+1; j<11; j++) {
                for (int k=j+1; k<12; k++) {
                    canBeSet = isSet(cardAttributes[i/4][i%4], cardAttributes[j/4][j%4], cardAttributes[k/4][k%4]);

                    if(canBeSet) {
                        finalSets.add(new Card[]{
                                cardAttributes[i / 4][i % 4],
                                cardAttributes[j / 4][j % 4],
                                cardAttributes[k / 4][k % 4]});
                        setsFound.append(i + 1).append(" ").append(j + 1).append(" ").append(k + 1).append("\n");
                    }
                }
            }
        }
        //JOptionPane.showMessageDialog(null, setsFound.toString(), "Set Solver",
                //JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * Input: Color object containing a pixel of the image
     * Return: The color of the pixel as either Red (R), Green (G), Purple (P), or Other (O)
     */
    public static CardColor colorOfPixel(int pixel) {
        Color inputColor = new Color(pixel, true);
        if(Math.abs(inputColor.getRed()-inputColor.getGreen())<15 && Math.abs(inputColor.getGreen()-inputColor.getBlue())<15) {
            return CardColor.OTHER; //used to catch gray colors
        }
        if(inputColor.getRed()>175 && inputColor.getGreen()<180 && inputColor.getBlue()<180) {
            return CardColor.RED;
        }
        else if(inputColor.getRed()<180 && inputColor.getGreen()>150 && inputColor.getBlue()<180) {
            return CardColor.GREEN;
        }
        else if (inputColor.getRed()<120 && inputColor.getGreen()<90) {
            return CardColor.PURPLE;
        }
        else return CardColor.OTHER;
    }
}