import java.awt.*;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * SetSolver
 *
 * Main class that extracts the necessary attributes of each card from an image containing a Set Board, determines all
 * the sets on the board, and uses Paint.java to display all the sets.
 */

public class SetSolver {
    static Card[][] cardAttributes = new Card[3][4];
    static ArrayList<Card[]> finalSets;
    static BufferedImage img;

    public static void main(String[] args) throws Exception {
        JOptionPane.showMessageDialog(null, "Please select the file containing the image of " +
                "the board", "Set Solver", JOptionPane.INFORMATION_MESSAGE);
        if (chooseFile()) {
            deconstructImage();
            findSets();
            displaySets();
        }
    }

    public static boolean chooseFile() throws IOException {
        //User selects file containing the image of the board
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Choose Image File");
        int result = jf.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = jf.getSelectedFile();
            img = ImageIO.read(file);
            try {
                img.getHeight();
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(null, "Please select an image file", "Set Solver",
                        JOptionPane.INFORMATION_MESSAGE);
                chooseFile();
            }
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Sorry to see you go!", "Set Solver",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    /**
     * Determines the x and y coordinate ranges that the 12 cards lie in using the determineCardBounds method, then
     * loops through the 12 regions the cards are in. For each subregion, the x and y coordinate ranges that the actual
     * colored shapes lie in are determined again using the determineCardBounds method, then the deconstructCard
     * method is called on this subregion defined by the result of the determineCardBounds method.
     */
    public static void deconstructImage() {
        int width, height, loopXLeft, loopXRight, loopYTop, loopYBottom;

        /*
        The boardBounds object contains the x and y coordinates for the first occurrence of a colored pixel when
        traveling to the center of the board from the left, right, top, and bottom edges
        */
        CardBounds boardBounds = determineCardBounds(0, img.getWidth(), 0, img.getHeight(), img);

        /*
        The boardBounds object currently outlines a rectangular region bordering the edges of the shapes within the
        cards and not the cards themselves, and dividing the region into 12 identical regions might split some cards
        incorrectly. The bounds of the large region on the left and right will continually grow larger until the
        borders between the 12 regions don't encounter colored pixels.
         */

        for (int right = boardBounds.getXRight(); right < img.getWidth(); right++) {
            for (int left = boardBounds.getXLeft(); left > 0; left--) {
                if (boardBoundsCorrect(new CardBounds(left, right, boardBounds.getYBottom(), boardBounds.getYTop()))) {
                    boardBounds.setXLeft(left);
                    boardBounds.setXRight(right);
                    right = img.getWidth();
                    break;
                }
            }
        }

        height = boardBounds.getYBottom() - boardBounds.getYTop();
        width = boardBounds.getXRight() - boardBounds.getXLeft();

        //Loops through all 12 equally sized subsections of the board
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

                Card card = deconstructCard(cardBounds, leftMostPoint, img);

                cardAttributes[row][col] = card;
            }
        }
    }

    /**
     * Determines whether a CardBounds object properly outlines the region of the board.
     * @param cardBounds CardBounds object outlining a region on the board.
     * @return When divided into 12 equal regions, if the borders between the regions have any colored pixels, return
     * false, otherwise return true;
     */
    public static boolean boardBoundsCorrect(CardBounds cardBounds) {
        int xLocation;
        for (int col = 0; col < 4; col++) {
            xLocation = (cardBounds.getWidth() / 4) * col + cardBounds.getXLeft();
            for (int row = cardBounds.getYTop(); row < cardBounds.getYBottom(); row++) {
                if (!(colorOfPixel(img.getRGB(xLocation, row)) == CardColor.OTHER)) return false;
            }
        }
        //No colored pixels encountered
        return true;
    }

    public static Card deconstructCard(CardBounds cardBounds, int yLeftMostOfShape, BufferedImage img) {
        Card card = new Card();
        card.setCardBounds(cardBounds);

        int xLeftOfShape = cardBounds.getXLeft();
        int xRightOfShape = cardBounds.getXRight();
        int yTopOfShape = cardBounds.getYTop();
        int yBottomOfShape = cardBounds.getYBottom();
        int height = cardBounds.getHeight();
        int width = cardBounds.getWidth();
        int pixel;

        //Determine the number of shapes on a card by comparing the height to the width of the shapes
        if((width) < (height) * .75) card.setNumber(1);
        else if((width) > (height) * 1.25) card.setNumber(3);
        else card.setNumber(2);


        //Determine shape of card
        int xLocDivot = 0;
        int yCenterOfShape = height / 2 + yTopOfShape;

        double tolerance = height * .3;
        int newBottom = (int) (yCenterOfShape + tolerance);
        int newTop = (int) (yCenterOfShape - tolerance);

        for (int y = newBottom; y > newTop; y--) {
            for (int x = xLeftOfShape; x < xRightOfShape; x++) {
                pixel = img.getRGB(x, y);
                if (!(colorOfPixel(pixel) == CardColor.OTHER)) {
                    /*Determines the x-location of the "divot," or where left side of the shape is furthest from the
                    edge only checks within a 30% tolerance of the height from the center, otherwise the divot would
                    always be either the top of bottom of the shape*/
                    if (x > xLocDivot) {
                        xLocDivot = x;
                    }
                    break;
                }
            }
        }

        /*If the y-cord of the leftmost edge is within 10% of the height of the shape from the center in either
        direction, the shape is a diamond*/
        if(yLeftMostOfShape > yCenterOfShape-(height)*.1 && yLeftMostOfShape < yCenterOfShape+(height)*.1)
            card.setShape(CardShape.DIAMOND);
        else {
            /*If the difference between the depth of the shape and the leftmost edge of the shape is greater than 5%
            of the height of the shape, the shape is a squiggle. If not, the shape is an oval
             */
            if ((xLocDivot - xLeftOfShape) > (height) * .05) {
                card.setShape(CardShape.SQUIGGLE);
            }
            else {
                card.setShape(CardShape.OVAL);
            }
        }


        //Determine the shading of the card

        int numberColoredPixels = 0;
        int yStart = yBottomOfShape;
        int yLast = 0;

        /*The shading of a card is determined by the number of colored pixels that are encountered when traveling
        vertically through the center of a shape. Only 75% of the shape's height is travelled in order to exclude the
        edges of an empty shape.
         */
        for (int y = yTopOfShape + (height) / 8; y < yBottomOfShape - (height) / 8; y++) {
            if (card.getNumber() == 2) {
                /*The path when travelling down at the x location of the top of a squiggle would cross through the left
                edge of the squiggle, so the x location needs to move to the right a bit to avoid the left edge
                 */
                if (card.getShape() == CardShape.SQUIGGLE) pixel = img.getRGB((int) (cardBounds.getXTop() +
                        cardBounds.getWidth() * .1), y);

                /*For a card with 2 shapes that aren't squiggle's, travelling down at the x location of the top of
                the shape is sufficient
                 */
                else pixel = img.getRGB(cardBounds.getXTop(), y);
            }
            //For a card with 1 or 3 shapes, travelling down at the x midpoint of the shape is sufficient
            else pixel = img.getRGB(cardBounds.getXLeft() + cardBounds.getWidth() / 2, y);

            /*Count the number of colored pixels (Green, Red, or Purple) and store the y location of the first colored
            pixel and the y location of the last colored pixel */
            if (!(colorOfPixel(pixel) == CardColor.OTHER)) {
                numberColoredPixels++;
                if (y < yStart) yStart = y;
                if (y > yLast) yLast = y;
            }
        }

        double heightRanThrough = yBottomOfShape - yTopOfShape - height / 4.0; //Total number of pixels checked
        double portionColored = numberColoredPixels / heightRanThrough; //Portion of pixels checked that were colored

        //Determine the shading of the shape based on the portion of pixels encountered that were colored
        if(portionColored < .1) card.setShading(CardShading.EMPTY);
        else if(portionColored > .85) card.setShading(CardShading.FULL);
        else card.setShading(CardShading.PARTIALLY);


        //Determine color of shape
        int numRedPixels = 0;
        int numGreenPixels = 0;
        int numPurplePixels = 0;
        int yMid = cardBounds.getYMid();

        /*Count the total number of red, green, and purple pixels when travelling horizontally at the shape's
        vertical midpoint
         */
        for (int x = xLeftOfShape; x < xRightOfShape; x++) {
            pixel = img.getRGB(x, yMid);

            if(colorOfPixel(pixel) == CardColor.RED) numRedPixels++;
            if(colorOfPixel(pixel) == CardColor.GREEN) numGreenPixels++;
            if(colorOfPixel(pixel) == CardColor.PURPLE) numPurplePixels++;
        }

        //Set the color of the card to the color encountered the most
        if(numRedPixels > numGreenPixels && numRedPixels > numPurplePixels) card.setColor(CardColor.RED);
        else if(numGreenPixels > numRedPixels && numGreenPixels > numPurplePixels) card.setColor(CardColor.GREEN);
        else card.setColor(CardColor.PURPLE);

        return card;
    }

    /**
     * This method determines the left, right, top, and bottom bounds of the shape(s) in a subregion defined by the
     * parameters. The bounds represent the first occurence of a colored pixel when traveling to the center of a region
     * from each side.
     * @param loopXLeft Left bound of the region containing the entire card
     * @param loopXRight Right bound of the region containing the entire card
     * @param loopYTop Top bound of the region containing the entire card
     * @param loopYBottom Bottom bound of the region containing the entire card
     * @param img BufferedImage object of the entire board uploaded by the user
     * @return A CardBound object containing the left, right, top, and bottom bounds of the shapes in a card.
     */
    public static CardBounds determineCardBounds(int loopXLeft, int loopXRight, int loopYTop, int loopYBottom,
                                                 BufferedImage img) {
        int xLeft = img.getWidth();
        int xRight = 0;
        int yTop = img.getHeight();
        int yBottom = 0;
        int xTop = 0;
        int coloredPixelsCount = 0;

        for (int x = loopXLeft; x < loopXRight; x++) {
            for (int y = loopYTop; y < loopYBottom; y++) {
                if (!(colorOfPixel(img.getRGB(x, y)) == CardColor.OTHER)) {
                    coloredPixelsCount++;
                    if (coloredPixelsCount > 5) {
                        if (x < xLeft) xLeft = x;
                        if (x > xRight) xRight = x;
                        if (y < yTop) {
                            yTop = y;
                            xTop = x;
                        }
                        if (y > yBottom) yBottom = y;
                    }
                } else coloredPixelsCount = 0;
            }
        }
        CardBounds cardBounds = new CardBounds(xLeft, xRight, yBottom, yTop - 5);
        cardBounds.setXTop(xTop);

        return cardBounds;
    }

    /**
     * Determines the y location of the leftmost colored pixel in a subregion defined by the parameters.
     * @param xLeft Left bound of the region containing the entire card
     * @param xRight Right bound of the region containing the entire card
     * @param yTop Top bound of the region containing the entire card
     * @param yBottom Bottom bound of the region containing the entire card
     * @param img BufferedImage object containing an image of the entire board uploaded by the user
     * @return The y location of the leftmost colored pixel in a subregion
     */
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

    /**
     * Returns whether 3 cards are a set depending on the color, number of shapes, type of shape, and shading of the
     * cards.
     * @param card1 Card 1 of the potential set
     * @param card2 Card 2 of the potential set
     * @param card3 Card 3 of the potential set
     * @return True if 3 cards are a set, false if not.
     */
    public static boolean isSet(Card card1, Card card2, Card card3) {
        return colorFitsSet(card1, card2, card3)
                && numberFitsSet(card1, card2, card3)
                && shapeFitsSet(card1, card2, card3)
                && shadingFitsSet(card1, card2, card3);
    }

    /**
     * Returns whether the colors of the 3 cards make the cards eligible to be a set. In order to be eligible for
     * a set, the colors of the 3 cards must either be all the same, or each card must have a different color.
     * @param card1 Card 1 of the potential set
     * @param card2 Card 2 of the potential set
     * @param card3 Card 3 of the potential set
     * @return True if the color of all 3 cards are the same or are all different, false if otherwise.
     */
    public static boolean colorFitsSet(Card card1, Card card2, Card card3) {
        return (card3.getColor() == card1.getColor()
                && card3.getColor() == card2.getColor()
                && card1.getColor() == card2.getColor()) ||
                (card3.getColor() != card1.getColor()
                        && card3.getColor() != card2.getColor()
                        && card1.getColor() != card2.getColor());
    }

    /**
     * Returns whether the number of shapes on the 3 cards make the cards eligible to be a set. In order to be
     * eligible for a set, the number of shapes on the 3 cards must either be all the same, or each card must have
     * a different number of shapes.
     * @param card1 Card 1 of the potential set
     * @param card2 Card 2 of the potential set
     * @param card3 Card 3 of the potential set
     * @return True if the number of shapes on all 3 cards are the same or are all different, false if otherwise.
     */
    public static boolean numberFitsSet(Card card1, Card card2, Card card3) {
        return (card1.getNumber() == card2.getNumber()
                && card1.getNumber() == card3.getNumber()
                && card2.getNumber() == card3.getNumber()) ||
                (card1.getNumber() != card2.getNumber()
                        && card1.getNumber() != card3.getNumber()
                        && card2.getNumber() != card3.getNumber());
    }

    /**
     * Returns whether the type of shape on the 3 cards make the cards eligible to be a set. In order to be
     * eligible for a set, the type of shape on the 3 cards must either be all the same, or each card must have a
     * different type of shape.
     * @param card1 Card 1 of the potential set
     * @param card2 Card 2 of the potential set
     * @param card3 Card 3 of the potential set
     * @return True if all the type of shape on all 3 shapes are the same or are all different, false if otherwise.
     */
    public static boolean shapeFitsSet(Card card1, Card card2, Card card3) {
        return (card1.getShape() == card2.getShape()
                && card1.getShape() == card3.getShape()
                && card2.getShape() == card3.getShape()) ||
                (card1.getShape() != card2.getShape()
                        && card1.getShape() != card3.getShape()
                        && card2.getShape() != card3.getShape());
    }

    /**
     * Returns whether the shading of the 3 cards make the cards eligible to be a set. In order to be eligible for
     * a set, the shading of the 3 cards must either be all the same, or each card must have a different shading.
     * @param card1 Card 1 of the potential set
     * @param card2 Card 2 of the potential set
     * @param card3 Card 3 of the potential set
     * @return True if the shading of all 3 cards are the same or are all different, false if otherwise.
     */
    public static boolean shadingFitsSet(Card card1, Card card2, Card card3) {
        return (card1.getShading() == card2.getShading()
                && card2.getShading() == card3.getShading()
                && card1.getShading() == card3.getShading()) ||
                card1.getShading() != card2.getShading()
                        && card2.getShading() != card3.getShading()
                        && card1.getShading() != card3.getShading();
    }

    /**
     * This method goes through every iteration of a 3 card combination from the cardAttributes array and calls the
     * isSet method on each iteration. If the method returns true, the 3 cards are a part of a set and are put into
     * a Card[] array of length 3 and added to the finalSets ArrayList.
     */
    public static void findSets() {
        finalSets = new ArrayList<>();

        for(int i=0; i<10; i++) {
            for(int j=i+1; j<11; j++) {
                for (int k=j+1; k<12; k++) {
                    if(isSet(cardAttributes[i/4][i%4], cardAttributes[j/4][j%4], cardAttributes[k/4][k%4])) {
                        finalSets.add(new Card[]{
                                cardAttributes[i / 4][i % 4],
                                cardAttributes[j / 4][j % 4],
                                cardAttributes[k / 4][k % 4]});
                    }
                }
            }
        }
    }

    /**
     * Determines the color of a pixel.
     * @param pixel Integer pixel in the default RGB color model
     * @return Either the RED, GREEN, PURPLE, or OTHER enum from CardColor.java as the color of the pixel
     */
    public static CardColor colorOfPixel(int pixel) {
        Color inputColor = new Color(pixel, true);
        if(Math.abs(inputColor.getRed()-inputColor.getGreen())<15
                && Math.abs(inputColor.getGreen()-inputColor.getBlue())<15)
            return CardColor.OTHER; //used to catch gray colors

        if(inputColor.getRed()>175 && inputColor.getGreen()<180 && inputColor.getBlue()<180)
            return CardColor.RED;

        else if(inputColor.getRed()<180 && inputColor.getGreen()>150 && inputColor.getBlue()<180)
            return CardColor.GREEN;

        else if (inputColor.getRed()<120 && inputColor.getGreen()<90)
            return CardColor.PURPLE;

        else return CardColor.OTHER;
    }

    /**
     * Creates a JFrame to display all the sets on the board.
     */
    public static void displaySets() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Paint");
            Container content = frame.getContentPane();
            content.setLayout(new BorderLayout());

            Paint paint = new Paint(img);
            content.add(paint, BorderLayout.CENTER);

            //Displays the total number of sets found
            JPanel topPanel = new JPanel();
            JLabel topText = new JLabel("Number of sets found: " + SetSolver.finalSets.size());
            topPanel.add(topText);
            topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            frame.add(topPanel, BorderLayout.NORTH);

            //Return button allows the user to select another Set Board image to solve
            JPanel bottomPanel = new JPanel();
            JButton returnButton = new JButton("Solve another set board");
            returnButton.addActionListener(e -> {
                try {
                    frame.dispose();
                    Paint.repainting = false;
                    if (SetSolver.chooseFile()) {
                        SetSolver.deconstructImage();
                        SetSolver.findSets();
                        SetSolver.displaySets();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            bottomPanel.add(returnButton);
            bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            frame.add(bottomPanel, BorderLayout.SOUTH);

            frame.setSize(1200, 675);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}