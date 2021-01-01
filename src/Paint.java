import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

public class Paint extends JComponent implements Runnable {
    public static Image image; // the canvas
    public static BufferedImage bufferedImage;
    public static Graphics2D graphics2D;  // this will enable drawing
    public static Card card;

    Paint paint;

    public static void displaySets() {
        SwingUtilities.invokeLater(new Paint());
    }

    public void paintComponent(Graphics g) {
        bufferedImage = (BufferedImage) image;
        CardBounds boardBounds = SetSolver.determineCardBounds(0, bufferedImage.getWidth(), 0, bufferedImage.getHeight(), bufferedImage);
        boardBounds.increaseWidth(.07);
        boardBounds.increaseHeight(.11);
        bufferedImage = bufferedImage.getSubimage(boardBounds.getXLeft(), boardBounds.getYTop(), boardBounds.getWidth(), boardBounds.getHeight());

        /* this lets us draw on the image (ie. the canvas)*/
        graphics2D = (Graphics2D) image.getGraphics();

        /* gives us better rendering quality for the drawing lines */
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        /* set canvas to white with default paint color */
        graphics2D.setPaint(Color.black);
        graphics2D.setStroke(new BasicStroke(2));


        drawSets();

        image = bufferedImage.getScaledInstance(1134, 568, Image.SCALE_SMOOTH);

        g.drawImage(image, 0, 0, null);
    }

    public static void drawSets() {
        Color[] setColors = {Color.RED, Color.GREEN, Color.MAGENTA, Color.BLUE, Color.ORANGE, Color.CYAN};

        //Determine the ideal spacing between two rectangles surrounding a card
        int rectangleOffset;
        int width = 0;
        int height = 0;
        CardBounds idealBounds = new CardBounds(0, 0, 0, 0);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                idealBounds = SetSolver.cardAttributes[row][col].getCardBounds();
                if (idealBounds.getWidth() > width) {
                    System.out.println(width);
                    width = idealBounds.getWidth();
                }
                if (idealBounds.getHeight() > height) height = idealBounds.getHeight();
            }
        }
        rectangleOffset = (int) ((double) width * .04);


        graphics2D.setStroke(new BasicStroke(rectangleOffset / 2));

        for (int i = 0; i < SetSolver.finalSets.size(); i++) {
            Card[] set = SetSolver.finalSets.get(i);
            graphics2D.setColor(setColors[i]);
            for (int j = 0; j < 3; j++) {
                Card cardInSet = set[j];
                CardBounds cardBounds = cardInSet.getCardBounds();
                int rectangleHolder = rectangleOffset * cardInSet.getCurrentNumSets();

                graphics2D.drawRect(cardBounds.getXMid() - width / 2 - rectangleHolder, cardBounds.getYMid() - height / 2 - rectangleHolder, width + 2 * rectangleHolder, height + 2 * rectangleHolder);

                set[j].incrementNumSets();
                System.out.println(rectangleOffset);
            }
        }

    }

    @Override
    public void run() {
        JFrame frame = new JFrame("Paint");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        paint = new Paint();
        content.add(paint, BorderLayout.CENTER);
        frame.setSize(1300, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
