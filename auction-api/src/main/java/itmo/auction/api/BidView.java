package itmo.auction.api;
import java.io.Serializable;
import java.time.Instant;
public final class BidView implements Serializable {
 private static final long serialVersionUID=1L;
 private final long id,auctionId,amount,createdAt; private final String bidder;
 public BidView(long id,long auctionId,String bidder,long amount,long createdAt){this.id=id;this.auctionId=auctionId;this.bidder=bidder;this.amount=amount;this.createdAt=createdAt;}
 public long getId(){return id;} public long getAuctionId(){return auctionId;} public String getBidder(){return bidder;}
 public long getAmount(){return amount;} public long getCreatedAt(){return createdAt;}
 public String toString(){return "Ставка #"+id+": "+bidder+", "+amount+" RUB, "+Instant.ofEpochMilli(createdAt);}
}
