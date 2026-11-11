package itmo.auction.api;
import java.io.Serializable;
/** Terminal result. finalPrice=0 if no sale occurred (no bids or cancellation). */
public final class AuctionResult implements Serializable {
 private static final long serialVersionUID=1L;
 private final AuctionView auction; private final String status,winner;private final long finalPrice;
 public AuctionResult(AuctionView auction,String status,String winner,long finalPrice){this.auction=auction;this.status=status;this.winner=winner;this.finalPrice=finalPrice;}
 public AuctionView getAuction(){return auction;} public String getStatus(){return status;}
 public String getWinner(){return winner;} public long getFinalPrice(){return finalPrice;}
 public String toString(){return "Результат #"+auction.getId()+": "+status+"; победитель: "+(winner==null?"нет":winner)+"; итоговая цена: "+finalPrice+" RUB";}
}
