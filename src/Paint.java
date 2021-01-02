import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Paint
 *
 * A class that displays all the sets on a Set Board with matching colored rectangles around each of the 3 cards in a
 * set.
 */

public class Paint extends JComponent {
    private Image image;
    private Graphics2D graphics2D;
    public static boolean repainting = false;

    public Paint(BufferedImage img) {
        image = img;
    }

    public void paintComponent(Graphics g) {
        this.image = SetSolver.img;

        BufferedImage bufferedImage;
        if (image != null) bufferedImage = (BufferedImage) image;
        else {
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);

            // Draw the image on to the buffered image
            Graphics2D bGr = bufferedImage.createGraphics();
            bGr.drawImage(bufferedImage, 0, 0, null);
            bGr.dispose();
        }

        //Get the bounds of the board in the image
        CardBounds boardBounds = SetSolver.determineCardBounds(0, bufferedImage.getWidth(), 0,
                bufferedImage.getHeight(), bufferedImage);

        /*Increase the board to show the whole height of the board instead of stopping at the top and bottom where the
        shapes are*/
        boardBounds.increaseHeight(.14);

        /*The boardBounds object currently outlines a rectangle containing the set board, but each of the bounds is at
        the edge of the outer shapes on the board and not the whole board itself. The width needs to be increased to
        show the whole board and depends on the card with the highest number of shapes in the left and right column.
         */
        int highestNumShapesLeft = 0;
        int highestNumShapesRight = 0;
        for (int row = 0; row < 3; row++) {
            if (SetSolver.cardAttributes[row][0].getNumber() > highestNumShapesLeft)
                highestNumShapesLeft = SetSolver.cardAttributes[row][0].getNumber();
            if (SetSolver.cardAttributes[row][3].getNumber() > highestNumShapesRight)
                highestNumShapesRight = SetSolver.cardAttributes[row][3].getNumber();
        }

        double multiplier = 0;
        switch (highestNumShapesLeft) {
            case 3 -> multiplier = .05;
            case 2 -> multiplier = .08;
            case 1 -> multiplier = .13;
        }
        boardBounds.setXLeft((int) (boardBounds.getXLeft() - boardBounds.getWidth() * multiplier));

        switch (highestNumShapesRight) {
            case 3 -> multiplier = .05;
            case 2 -> multiplier = .08;
            case 1 -> multiplier = .13;
        }
        boardBounds.setXRight((int) (boardBounds.getXRight() + boardBounds.getWidth() * multiplier));

        //Ensure the new bounds from increasing the height and width of boardBounds don't go off the image
        int left, right, top, bottom;
        if ((left = boardBounds.getXLeft()) < 0) left = 0;
        if ((right = boardBounds.getXRight()) >= bufferedImage.getWidth()) right = bufferedImage.getWidth() - 1;
        if ((top = boardBounds.getYTop()) < 0) top = 0;
        if ((bottom = boardBounds.getYBottom()) >= bufferedImage.getHeight()) bottom = bufferedImage.getHeight() - 1;

        //Get the sub image of just the board from the image and exclude the extra space around.
        bufferedImage = bufferedImage.getSubimage(left, top, right - left, bottom - top);

        graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawSets();

        image = bufferedImage.getScaledInstance((int) (bufferedImage.getWidth() * 575.0 / bufferedImage.getHeight()),
                575, Image.SCALE_SMOOTH);

        g.drawImage(image, 0, 0, null);
    }

    /**
     * Draws colored rectangles around the 3 cards of every set on the board. Each set has a unique color.
     */
    private void drawSets() {
        Color[] setColors = {Color.RED, Color.GREEN, Color.MAGENTA, Color.BLUE, Color.ORANGE, Color.CYAN, Color.YELLOW,
                Color.GRAY, Color.BLACK};

        /*Determine the ideal spacing between two rectangles surrounding a card based on the card with the largest
        width
         */
        int rectangleOffset;
        int width = 0;
        int height = 0;
        CardBounds idealBounds;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                idealBounds = SetSolver.cardAttributes[row][col].getCardBounds();
                if (idealBounds.getWidth() > width) {

                    width = idealBounds.getWidth();
                }
                if (idealBounds.getHeight() > height) height = idealBounds.getHeight();
            }
        }
        rectangleOffset = (int) ((double) width * .04);

        graphics2D.setStroke(new BasicStroke((float) rectangleOffset / 2));

        //Draw a rectangle around each card for every set
        if (!repainting) {
            for (int i = 0; i < SetSolver.finalSets.size(); i++) {
                Card[] set = SetSolver.finalSets.get(i);
                graphics2D.setColor(setColors[i]); //Unique color for this set

                for (int j = 0; j < 3; j++) {
                    Card cardInSet = set[j];
                    CardBounds cardBounds = cardInSet.getCardBounds();
                    /*Determine how much large the rectangle around a card must be depending on how many rectangles
                    are already around the card*/
                    int rectangleHolder = rectangleOffset * cardInSet.getCurrentNumSets();

                    //Draw the rectangle around the card
                    graphics2D.drawRect(cardBounds.getXMid() - width / 2 - rectangleHolder,
                            cardBounds.getYMid() - height / 2 - rectangleHolder, width + 2 * rectangleHolder,
                            height + 2 * rectangleHolder);

                    //Increase the number of sets this card has been a part of by one
                    set[j].incrementNumSets();
                }
            }
            repainting = true;
        }

    }

}
