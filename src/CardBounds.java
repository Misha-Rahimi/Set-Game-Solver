/**
 * Class representing the left, right, top, and bottom bounds of a rectangular region enclosing the shape(s) in a card.
 */

public class CardBounds {
    private int xLeft; //x coordinate for the left bound
    private int xRight; //x coordinate for the right bound
    private int yTop; //y coordinate for the top bound
    private int xTop; //x coordinate for the top bound
    private int yBottom; //y coordinate for the bottom bound

    public CardBounds(int xLeft, int xRight, int yBottom, int yTop) {
        this.xLeft = xLeft;
        this.xRight = xRight;
        this.yBottom = yBottom;
        this.yTop = yTop;
    }

    public int getXLeft() {
        return xLeft;
    }

    public int getXRight() {
        return xRight;
    }

    public int getYTop() {
        return yTop;
    }

    public int getXTop() {
        return xTop;
    }

    public int getYBottom() {
        return yBottom;
    }

    public int getWidth() {
        return this.xRight - this.xLeft;
    }

    public int getYMid() {
        return (yBottom - yTop) / 2 + yTop;
    }

    public int getXMid() {
        return (xRight - xLeft) / 2 + xLeft;
    }

    public int getHeight() {
        return this.yBottom - this.yTop;
    }

    public void increaseHeight(double percentIncrease) {
        int change = (int) ((this.yBottom - this.yTop) * (percentIncrease / 2.0));
        this.yTop -= change;
        this.yBottom += change;
    }

    public void setXLeft(int xLeft) {
        this.xLeft = xLeft;
    }

    public void setXRight(int xRight) {
        this.xRight = xRight;
    }

    public void setYBottom(int yBottom) {
        this.yBottom = yBottom;
    }

    public void setYTop(int yTop) {
        this.yTop = yTop;
    }

    public void setXTop(int xTop) {
        this.xTop = xTop;
    }
}