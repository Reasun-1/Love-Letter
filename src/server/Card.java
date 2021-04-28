package server;


import java.util.Random;
import java.util.Stack;

public enum Card {

    //status of a player: 0=out, 1=in game, 2= protected

    PRINCESS("princess",8,1){

        void function(int myIndex){

            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.PRINCESS;
            Game.getInstance().status[myIndex] = 0;
            Server.getServer().sendMessageToAll(Game.getInstance().playerNames.get(myIndex) + " is out of game.");
        }

    },

    COUNTESS("countess",7,1){

        void function(int myIndex){
            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.COUNTESS;
            Game.getInstance().status[myIndex] = 1;
        }

    },

    KING("king", 6,1){

        void function(int myIndex, int targetIndex){

            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.KING;
            Game.getInstance().status[myIndex] = 1;

            if(Game.getInstance().status[targetIndex] == 1){
                Card tempCard = Game.getInstance().handCard[targetIndex];
                Game.getInstance().handCard[myIndex] = tempCard;
                Game.getInstance().handCard[targetIndex] = Card.KING;
            }else if(Game.getInstance().status[targetIndex] == 2){
                Server.getServer().sendTo(Game.getInstance().playerNames.get(myIndex), "The chosen player is protected.");
            }else{
                Server.getServer().sendTo(Game.getInstance().playerNames.get(myIndex), "The chosen player is out of game.");
            }
        }

    },

    PRINCE("prince", 5,2){

        void function(int myIndex, int targetIndex){

            int myCountDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][myCountDiscarded] = Card.PRINCE;
            Game.getInstance().status[myIndex] = 1;

            if(Game.getInstance().status[targetIndex] == 1){
                int targetCountDiscarded = Game.getInstance().countDiscarded[targetIndex]++;
                Card targetDiscard = Game.getInstance().handCard[targetIndex];
                Game.getInstance().discardedCard[myIndex][targetCountDiscarded] = targetDiscard;

                if(targetDiscard == Card.PRINCESS){
                    Game.getInstance().status[targetIndex] = 0;
                    System.out.println("player " + "name is out of game.");
                }

                Game.getInstance().handCard[targetIndex] = Game.getInstance().deck.pop();

            }
        }

    },

    HANDMAID("handmaid",4,2){

            void functon(int myIndex){

                int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
                Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.HANDMAID;
                Game.getInstance().status[myIndex] = 2;

            }

    },
    BARON("baron",3,2){
        void function(int myIndex, int targetIndex){

            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.BARON;


            if(Game.getInstance().status[targetIndex] == 1){

                int targetValue = Game.getInstance().handCard[targetIndex].value;
                int myValue = Game.getInstance().handCard[targetIndex].value;

                if(targetValue > myValue){
                    Game.getInstance().status[myIndex] = 0;
                }else if(targetValue < myValue){
                    Game.getInstance().status[targetIndex] = 0;
                    Game.getInstance().status[myIndex] = 1;
                }else{
                    Game.getInstance().status[myIndex] = 1;
                }
            }

            Game.getInstance().status[myIndex] = 1;
        }

    },

    PRIEST("priest", 2,2){

        void function(int myIndex, int targetIndex){

            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.PRIEST;
            Game.getInstance().status[myIndex] = 1;

            if(Game.getInstance().status[targetIndex] == 1){
                Game.getInstance().seenCard[targetIndex] = targetIndex;
            }

        }

    },
    GUARD("guard", 1,5){

        void function(int myIndex, int targetIndex, String guess){

            int countDiscarded = Game.getInstance().countDiscarded[myIndex]++;
            Game.getInstance().discardedCard[myIndex][countDiscarded] = Card.GUARD;
            Game.getInstance().status[myIndex] = 1;

            if(Game.getInstance().status[targetIndex] == 1){

                if(guess.equals(Game.getInstance().handCard[targetIndex].type)){
                    Game.getInstance().status[targetIndex] = 0;
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

    void function(int myIndex){}
    void function(int myIndex, int targetIndex){}
    void function(int myIndex, int targetIndex, String guess){}

    public static Stack<Card> shuffle(){

        java.util.Random random = new Random();
        Card[] deckArray = new Card[16];
        int j = 0;

        //put the cards in the deck according to the count of each type of cards
        for(Card card : Card.values()){
            for (int i = 0; i < card.count; i++) {
                deckArray[j] = card;
                j++;
            }
        }
        //schuffle the deck : change two cards of the deck for 100 times randomly
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
