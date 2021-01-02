import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Paint
 *
 * A class representing that creates a drawing canvas where the color can be changed through buttons and text fields
 *
 * No outside sources used.
 *
 * @author Misha Rahimi, Section 003
 * @version 11/13/2020
 *
 */

public class Paint extends JComponent {
    private Image image; // the canvas
    private BufferedImage bufferedImage;
    private Graphics2D graphics2D;  // this will enable drawing
    public static boolean repainting = false;

    public Paint(BufferedImage img) {
        image = img;
    }


    public void paintComponent(Graphics g) {
        this.image = SetSolver.img;

        if (image instanceof BufferedImage)
        {
            bufferedImage = (BufferedImage) image;
        } else {

            // Create a buffered image with transparency
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

            // Draw the image on to the buffered image
            Graphics2D bGr = bufferedImage.createGraphics();
            bGr.drawImage(bufferedImage, 0, 0, null);
            bGr.dispose();
        }

        //bufferedImage = (BufferedImage) image;
        CardBounds boardBounds = SetSolver.determineCardBounds(0, bufferedImage.getWidth(), 0, bufferedImage.getHeight(), bufferedImage);
        boardBounds.increaseHeight(.14);

        int highestNumShapesLeft = 0;
        int highestNumShapesRight = 0;
        for (int row = 0; row < 3; row++) {
            if (SetSolver.cardAttributes[row][0].getNumber() > highestNumShapesLeft)
                highestNumShapesLeft = SetSolver.cardAttributes[row][0].getNumber();
            if (SetSolver.cardAttributes[row][3].getNumber() > highestNumShapesRight)
                highestNumShapesRight = SetSolver.cardAttributes[row][3].getNumber();
        }

        System.out.println(highestNumShapesLeft);
        System.out.println(highestNumShapesRight);

        double multiplier = 0;
        switch (highestNumShapesLeft) {
            case 3 -> multiplier = .05;
            case 2 -> multiplier = .08;
            case 1 -> multiplier = .13;
        }
        System.out.println(boardBounds.getXLeft());
        boardBounds.setXLeft((int) (boardBounds.getXLeft() - boardBounds.getWidth() * multiplier));
        System.out.println(boardBounds.getXRight());

        switch (highestNumShapesRight) {
            case 3 -> multiplier = .05;
            case 2 -> multiplier = .08;
            case 1 -> multiplier = .13;
        }
        System.out.println(boardBounds.getXLeft());
        boardBounds.setXRight((int) (boardBounds.getXRight() + boardBounds.getWidth() * multiplier));
        System.out.println(boardBounds.getXLeft());

        int left, right, top, bottom;
        if ((left = boardBounds.getXLeft()) < 0) left = 0;
        if ((right = boardBounds.getXRight()) >= bufferedImage.getWidth()) right = bufferedImage.getWidth() - 1;
        if ((top = boardBounds.getYTop()) < 0) top = 0;
        if ((bottom = boardBounds.getYBottom()) >= bufferedImage.getHeight()) bottom = bufferedImage.getHeight() - 1;

        System.out.println("left " + left + " right " + right + " width " + (right - left) + " actual width " + bufferedImage.getWidth());
        System.out.println("xleft " + boardBounds.getXLeft() + " xright " + boardBounds.getXRight() + " width " + boardBounds.getWidth());

        bufferedImage = bufferedImage.getSubimage(left, top, right - left, bottom - top);

        /* this lets us draw on the image (ie. the canvas)*/
        graphics2D = (Graphics2D) image.getGraphics();

        /* gives us better rendering quality for the drawing lines */
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        /* set canvas to white with default paint color */
        graphics2D.setPaint(Color.black);
        graphics2D.setStroke(new BasicStroke(2));


        drawSets();

        image = bufferedImage.getScaledInstance((int) (bufferedImage.getWidth() * 575.0 / bufferedImage.getHeight()), 575, Image.SCALE_SMOOTH);

        g.drawImage(image, 0, 0, null);
    }

    private void drawSets() {
        Color[] setColors = {Color.RED, Color.GREEN, Color.MAGENTA, Color.BLUE, Color.ORANGE, Color.CYAN, Color.GRAY,
                Color.YELLOW};

        //Determine the ideal spacing between two rectangles surrounding a card
        int rectangleOffset;
        int width = 0;
        int height = 0;
        CardBounds idealBounds = new CardBounds(0, 0, 0, 0);
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


        graphics2D.setStroke(new BasicStroke(rectangleOffset / 2));

        if (!repainting) {
            for (int i = 0; i < SetSolver.finalSets.size(); i++) {
                Card[] set = SetSolver.finalSets.get(i);
                graphics2D.setColor(setColors[i]);
                for (int j = 0; j < 3; j++) {
                    Card cardInSet = set[j];
                    CardBounds cardBounds = cardInSet.getCardBounds();
                    int rectangleHolder = rectangleOffset * cardInSet.getCurrentNumSets();

                    graphics2D.drawRect(cardBounds.getXMid() - width / 2 - rectangleHolder, cardBounds.getYMid() - height / 2 - rectangleHolder, width + 2 * rectangleHolder, height + 2 * rectangleHolder);

                    set[j].incrementNumSets();
                }
            }
            repainting = true;
        }

    }

}
