package server;


import java.util.Random;
import java.util.Stack;

public enum Card {

    //status of a player: 0=out, 1=in game, 2= protected

    PRINCESS("princess", 8, 1) {
        void function(int myIndex) {
            // put the played card into discarded cards
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.PRINCESS;
            Game.getInstance().status[myIndex] = 0; // when princess played, out of game
            Server.getServer().sendMessageToAll(Game.getInstance().playerNames.get(myIndex) + " is out of game.");
        }

    },

    COUNTESS("countess", 7, 1) {
        void function(int myIndex) {
            // put the played card into discarded cards
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.COUNTESS;
            Game.getInstance().status[myIndex] = 1; // when countess played, nothing happens
        }

    },

    KING("king", 6, 1) {
        void function(int myIndex, int targetIndex) {
            // put the played card into discarded cards
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.KING;
            Game.getInstance().status[myIndex] = 1;

            // King's card is traded with another player's card 
            Card tempCard = Game.getInstance().handCard[targetIndex];
            Game.getInstance().handCard[myIndex] = tempCard;
            Game.getInstance().handCard[targetIndex] = Card.KING;
        }
    },

    PRINCE("prince", 5, 2) {
        void function(int myIndex, int targetIndex) {
            // put the played card into discarded cards
            int myCountDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][myCountDiscarded] = Card.PRINCE;
            Game.getInstance().status[myIndex] = 1;
            // target player discards the hand card
            int targetCountDiscarded = Game.getInstance().countDiscarded[targetIndex]++;
            Card targetDiscard = Game.getInstance().handCard[targetIndex];
            Game.getInstance().discardedCard[myIndex][targetCountDiscarded] = targetDiscard;
            Game.getInstance().handCard[targetIndex] = null;
            // if the discarded hand card is princess -> target player out of game
            if (targetDiscard.getType() == Card.PRINCESS.getType()) {
                Game.getInstance().status[targetIndex] = 0;
                // inform all players that the target player has played a PRINCESS
                Server.getServer().playedCard(Game.getInstance().playerNames.get(targetIndex), Card.PRINCESS);
                // inform all players that the target player is out of game
                Server.getServer().sendMessageToAll(Game.getInstance().playerNames.get(targetIndex) + " is out of game.");
            } else { // draw a new card
                if (Game.getInstance().deck.size() == 0) { // when the deck is empty, draw a card from top cards
                    Card drawnCard = Game.getInstance().topCards.get(Game.getInstance().topCards.size() - 1);
                    Game.getInstance().handCard[targetIndex] = drawnCard;
                } else { // when the deck is not empty, draw a card from deck
                    Game.getInstance().handCard[targetIndex] = Game.getInstance().deck.pop();
                }
                // inform the player which card he has drawn
                Server.getServer().drawnCard(targetIndex, Game.getInstance().handCard[targetIndex]);
            }
        }
    },

    HANDMAID("handmaid", 4, 2) {
        void function(int myIndex) {
            // put the played card into discarded cards
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.HANDMAID;
            Game.getInstance().status[myIndex] = 2; // set the player status to "protected"
        }
    },

    BARON("baron", 3, 2) {
        void function(int myIndex, int targetIndex) {
            // put the played card into discarded cards
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.BARON;

            int targetValue = Game.getInstance().handCard[targetIndex].value;
            int myValue = Game.getInstance().handCard[targetIndex].value;

            if (targetValue > myValue) {
                Game.getInstance().status[myIndex] = 0;
                Server.getServer().sendMessageToAll(Game.getInstance().playerNames.get(myIndex) + " is out of game.");
            } else if (targetValue < myValue) {
                Game.getInstance().status[targetIndex] = 0;
                Game.getInstance().status[myIndex] = 1;
                Server.getServer().sendMessageToAll(Game.getInstance().playerNames.get(targetIndex) + " is out of game.");
            } else {
                Game.getInstance().status[myIndex] = 1;
            }
        }
    },

    PRIEST("priest", 2, 2) {
        void function(int myIndex, int targetIndex) {
            // put the played card into discarded cards
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.PRIEST;
            Game.getInstance().status[myIndex] = 1;

            if(myIndex != targetIndex){
                Game.getInstance().seenCard[targetIndex] = targetIndex;
                Card seenCard = Game.getInstance().handCard[targetIndex];
                String message = Game.getInstance().playerNames.get(targetIndex) + " has a " + seenCard.getType();
                //*******************brauchen wir hier ein besondere Funktion oder kann man so private Nachricht schicken? oder exception?*************
                Server.getServer().sendTo(Game.getInstance().playerNames.get(myIndex), message);
            }
        }
    },

    GUARD("guard", 1, 5) {
        void function(int myIndex, int targetIndex, Card guessCard) {
            // put the played card into discarded cards
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.GUARD;
            Game.getInstance().status[myIndex] = 1;

            if(myIndex != targetIndex){
                if (Game.getInstance().handCard[targetIndex].getType() == guessCard.getType()) {
                    Game.getInstance().status[targetIndex] = 0;
                    Server.getServer().sendMessageToAll(Game.getInstance().playerNames.get(targetIndex) + " is out of game.");
                }
            }
        }
    };

    private String type;
    private int value;
    private int count;

    Card(String type, int value, int count) {
        this.type = type;
        this.value = value;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    void function(int myIndex) {
    }

    void function(int myIndex, int targetIndex) {
    }

    void function(int myIndex, int targetIndex, Card guessCard) {
    }

    public static Stack<Card> shuffle() {

        java.util.Random random = new Random();
        Card[] deckArray = new Card[16];
        int j = 0;

        //put the cards in the deck according to the count of each type of cards
        for (Card card : Card.values()) {
            for (int i = 0; i < card.count; i++) {
                deckArray[j] = card;
                j++;
            }
        }
        //shuffle the deck : change two cards of the deck for 100 times randomly
        for (int i = 0; i < 100; i++) {
            int indexCard1 = random.nextInt(16);
            int indexCard2 = random.nextInt(16);
            Card card = deckArray[indexCard1];
            deckArray[indexCard1] = deckArray[indexCard2];
            deckArray[indexCard2] = card;
        }

        //put the cards from array to stack:
        Stack<Card> deck = new Stack<>();
        for (int i = 0; i < 16; i++) {
            deck.push(deckArray[i]);
        }
        return deck;
    }
}
