public class Card {
    private CardColor color;
    private CardShading shading;
    private CardShape shape;
    private int number;
    private CardBounds cardBounds;
    private int currentNumSets;

    public Card() {
        color = CardColor.NONE;
        shading = CardShading.NONE;
        shape = CardShape.NONE;
        number = 0;
        currentNumSets = 1;
    }

    public int getCurrentNumSets() {
        return currentNumSets;
    }

    public void incrementNumSets() {
        currentNumSets++;
    }

    public CardBounds getCardBounds() {
        return cardBounds;
    }

    public void setCardBounds(CardBounds cardBounds) {
        this.cardBounds = cardBounds;
    }

    public void setColor(CardColor newColor) {
        color = newColor;
    }

    public void setShading(CardShading newShading) {
        shading = newShading;
    }

    public void setShape(CardShape newShape) {
        shape = newShape;
    }

    public void setNumber(int newNumber) {
        number = newNumber;
    }

    public CardColor getColor() {
        return color;
    }

    public CardShading getShading() {
        return shading;
    }

    public CardShape getShape() {
        return shape;
    }

    public int getNumber() {
        return number;
    }


}
