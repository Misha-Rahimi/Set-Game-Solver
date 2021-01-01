public class CardBounds {
    private int xLeft;
    private int xRight;
    private int yTop;
    private int yBottom;

    public CardBounds(int xLeft, int xRight, int yBottom, int yTop) {
        this.xLeft = xLeft;
        this.xRight = xRight;
        this.yBottom = yBottom;
        this.yTop = yTop;
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

    public void increaseWidth(double percentIncrease) {
        int change = (int) ((this.xRight - this.xLeft) * (percentIncrease / 2.0));
        this.xLeft -= change;
        this.xRight += change;
    }

    public CardBounds increaseWidth(double percentIncrease, int anchor) {
        int change = (int) ((this.xRight - this.xLeft) * percentIncrease);
        int newLeft = this.xLeft;
        int newRight = this.xRight;

        if (anchor == 1) newRight += change;
        else newLeft -= change;

        return new CardBounds(newLeft, newRight, this.yBottom, this.yTop);
    }

    public void increaseHeight(double percentIncrease) {
        int change = (int) ((this.yBottom - this.yTop) * (percentIncrease / 2.0));
        this.yTop -= change;
        this.yBottom += change;
    }

    public CardBounds increaseHeight(double percentIncrease, int anchor) {
        int change = (int) ((this.yBottom - this.yTop) * percentIncrease);
        int newTop = this.yTop;
        int newBottom = this.yBottom;

        if (anchor == 1) newBottom += change;
        else newTop -= change;

        return new CardBounds(this.xLeft, this.xRight, newBottom, newTop);
    }

    public int getXLeft() {
        return xLeft;
    }

    public int getXRight() {
        return xRight;
    }

    public int getYBottom() {
        return yBottom;
    }

    public int getYTop() {
        return yTop;
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
}
