package server;

/**
 * Card is an enum class, which contents all the information about the cards in this game.
 * For each type of card, the card name, value, count and function of this card is defined
 * in the enum constant.
 *
 * @author can ren
 * @author yuliia shaparenko
 * @version 1.0-SNAPSHOT
 */
import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public enum Card {

    //status of a player: 0=out, 1=in game, 2= protected

    PRINCESS("princess", 8, 1) {
        void function(int myIndex) throws IOException {
            // put the played card into discarded cards
            int countdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][countdiscarded] = Card.PRINCESS;
            Game.getInstance().status[myIndex] = 0; // when princess played, out of game
            Server.getServer().outOfRound(Game.getInstance().playernames.get(myIndex));
            Server.getServer().sendMessageToAll(Game.getInstance().playernames.get(myIndex) + " is out of game.");
        }

    },

    COUNTESS("countess", 7, 1) {
        void function(int myIndex) {
            // put the played card into discarded cards
            int countdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][countdiscarded] = Card.COUNTESS;
            Game.getInstance().status[myIndex] = 1; // when countess played, nothing happens
        }

    },

    KING("king", 6, 1) {
        void function(int myIndex, int targetIndex) {
            // put the played card into discarded cards
            int countdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][countdiscarded] = Card.KING;
            Game.getInstance().status[myIndex] = 1;

            // King's card is traded with another player's card
            Card tempCard = Game.getInstance().handcard[targetIndex];
            Game.getInstance().handcard[targetIndex] = Game.getInstance().handcard[myIndex];
            Game.getInstance().handcard[myIndex] = tempCard;
            Server.getServer().sendMessageToAll("Cards exchanged.");
        }
    },

    PRINCE("prince", 5, 2) {
        void function(int myIndex, int targetIndex) throws IOException {
            // put the played card into discarded cards
            int mycountdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][mycountdiscarded] = Card.PRINCE;
            Game.getInstance().status[myIndex] = 1;
            // target player discards the hand card
            int targetcountdiscarded = Game.getInstance().countdiscarded[targetIndex]++;
            Card targetDiscard = Game.getInstance().handcard[targetIndex];
            Game.getInstance().discardedcard[targetIndex][targetcountdiscarded] = targetDiscard;
            Game.getInstance().handcard[targetIndex] = null;
            // inform everyone that target player dropped a card
            Server.getServer().sendMessageToAll(Game.getInstance().playernames.get(targetIndex) + " has dropped a " + targetDiscard);
            Server.getServer().dropCard(Game.getInstance().playernames.get(targetIndex));
            // if the discarded hand card is princess -> target player out of game
            if (targetDiscard.getType() == Card.PRINCESS.getType()) {
                Game.getInstance().status[targetIndex] = 0;
                // inform all players that the target player has played a PRINCESS
                Server.getServer().playedCard(Card.PRINCESS);
                // inform all players that the target player is out of game
                Server.getServer().outOfRound(Game.getInstance().playernames.get(targetIndex));
                //Server.getServer().sendMessageToAll(Game.getInstance().playernames.get(targetIndex) + " is out of game.");
            } else { // draw a new card
                if (Game.getInstance().deck.size() == 0) { // when the deck is empty, draw a card from top cards
                    Card drawncard = Game.getInstance().topcards.get(Game.getInstance().topcards.size() - 1);
                    Game.getInstance().handcard[targetIndex] = drawncard;
                } else { // when the deck is not empty, draw a card from deck
                    Game.getInstance().handcard[targetIndex] = Game.getInstance().deck.pop();
                }
                // inform the player whick card he has drawn
                Server.getServer().drawncard(targetIndex, Game.getInstance().handcard[targetIndex]);
            }
        }
    },

    HANDMAID("handmaid", 4, 2) {
        void function(int myIndex) {
            // put the played card into discarded cards
            int countdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][countdiscarded] = Card.HANDMAID;
            Game.getInstance().status[myIndex] = 2; // set the player status to "protected"
        }
    },

    BARON("baron", 3, 2) {
        void function(int myIndex, int targetIndex) throws IOException {
            // put the played card into discarded cards
            int countdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][countdiscarded] = Card.BARON;

            int targetValue = Game.getInstance().handcard[targetIndex].getValue();
            int myValue = Game.getInstance().handcard[myIndex].getValue();

            if (targetValue > myValue) {
                Game.getInstance().status[myIndex] = 0;
                Server.getServer().outOfRound(Game.getInstance().playernames.get(myIndex));
                Server.getServer().sendMessageToAll(Game.getInstance().playernames.get(myIndex) + " is out of game.");
            } else if (targetValue < myValue) {
                Game.getInstance().status[targetIndex] = 0;
                Game.getInstance().status[myIndex] = 1;
                Server.getServer().outOfRound(Game.getInstance().playernames.get(targetIndex));
                Server.getServer().sendMessageToAll(Game.getInstance().playernames.get(targetIndex) + " is out of game.");
            } else {
                // inform everyone that nothing happens
                Server.getServer().sendMessageToAll("Nothing happens, play continues.");
                Game.getInstance().status[myIndex] = 1;
            }
        }
    },

    PRIEST("priest", 2, 2) {
        void function(int myIndex, int targetIndex) {
            // put the played card into discarded cards
            int countdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][countdiscarded] = Card.PRIEST;
            Game.getInstance().status[myIndex] = 1;

            if(myIndex != targetIndex){
                Game.getInstance().seencard[myIndex] = targetIndex;
                Card seencard = Game.getInstance().handcard[targetIndex];
                String message = Game.getInstance().playernames.get(targetIndex) + " has a " + seencard.getType();
                //*******************brauchen wir hier ein besondere Funktion oder kann man so private Nachricht schicken? oder exception?*************
                Server.getServer().sendTo(Game.getInstance().playernames.get(myIndex), message);
            }
        }
    },

    GUARD("guard", 1, 5) {
        void function(int myIndex, int targetIndex, Card guessCard) throws IOException {
            // put the played card into discarded cards
            int countdiscarded = Game.getInstance().countdiscarded[myIndex]++;
            Game.getInstance().discardedcard[myIndex][countdiscarded] = Card.GUARD;
            Game.getInstance().status[myIndex] = 1;

            if(myIndex != targetIndex){
                if (Game.getInstance().handcard[targetIndex].getType() == guessCard.getType()) {
                    Game.getInstance().status[targetIndex] = 0;
                    Server.getServer().outOfRound(Game.getInstance().playernames.get(targetIndex));
                    Server.getServer().sendMessageToAll(Game.getInstance().playernames.get(targetIndex) + " is out of game.");
                }else{
                    Server.getServer().sendMessageToAll("Nothing happened, play continues.");
                }
            }
        }
    };

    private String type;
    private int value;
    private int count;

    /**
     * constructor of class Card, which needs card type, value and count as parameters
     * @param type the name of the card type
     * @param value the point of the card
     * @param count the total count of the card in one round
     */
    Card(String type, int value, int count) {
        this.type = type;
        this.value = value;
        this.count = count;
    }

    /**
     * getter of card type
     * @return String type the card tye will be returned
     */
    public String getType() {
        return type;
    }

    /**
     * setter of card type
     * @param type the card type will be set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * getter of card value
     * @return int value the card value will be returned
     */
    public int getValue() { return value;}

    /**
     * setter of card value
     * @param value the card value will be set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * getter of the count of the card
     * @return int count the count of the card will be returned
     */
    public int getCount() {
        return count;
    }

    /**
     * setter of the count of the card
     * @param count the count of the card will be set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * This method is a basic function, which is in PRINCESS, COUNTESS and HANDMAID overridden
     * @param myIndex is the player index, who plays in turn
     */
    void function(int myIndex) throws IOException {
    }

    /**
     * This method is a basic function, which is in KING, PRINCE, BARON and PRIEST overridden
     * @param myIndex is the player index, who plays in turn
     * @param targetIndex is the index of the target player, whom the player in turn chooses
     */
    void function(int myIndex, int targetIndex) throws IOException {
    }

    /**
     * This method is a basic function, which is in GUARD overridden
     * @param myIndex is the player index, who plays in turn
     * @param targetIndex is the index of the target player, whom the player in turn chooses
     * @param guessCard is the type of the card, which the player in turn guesses
     */
    void function(int myIndex, int targetIndex, Card guessCard) throws IOException {
    }

    /**
     * This method shuffle() is applied to shuffle the card at the beginning of each new round
     * @return Stack<Card> deck the shuffled the cards will be put in a stack for each player to draw the card
     */
    public static Stack<Card> shuffle() {

        java.util.Random random = new Random();
        Card[] deckarray = new Card[16];
        int j = 0;

        //put the cards in the deck according to the count of each type of cards
        for (Card card : Card.values()) {
            for (int i = 0; i < card.count; i++) {
                deckarray[j] = card;
                j++;
            }
        }
        //schuffle the deck : change two cards of the deck for 100 times randomly
        for (int i = 0; i < 100; i++) {
            int indexCard1 = random.nextInt(16);
            int indexCard2 = random.nextInt(16);
            Card card = deckarray[indexCard1];
            deckarray[indexCard1] = deckarray[indexCard2];
            deckarray[indexCard2] = card;
        }

        //put the cards from array to stack:
        Stack<Card> deck = new Stack<>();
        for (int i = 0; i < 16; i++) {
            deck.push(deckarray[i]);
        }
        return deck;
    }
}
