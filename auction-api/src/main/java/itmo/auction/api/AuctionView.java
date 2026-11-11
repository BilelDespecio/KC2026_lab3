package itmo.auction.api;
import java.io.Serializable;
import java.time.Instant;
public final class AuctionView implements Serializable {
 private static final long serialVersionUID=1L;
 private final long id, currentPrice, endsAt, bidCount;
 private final String title, highestBidder;
 private final boolean closed;
 public AuctionView(long id,String title,long price,String bidder,long end,long count,boolean closed) {
  this.id=id;this.title=title;this.currentPrice=price;this.highestBidder=bidder;this.endsAt=end;this.bidCount=count;this.closed=closed;
 }
 public long getId(){return id;} public String getTitle(){return title;}
 public long getCurrentPrice(){return currentPrice;} public String getHighestBidder(){return highestBidder;}
 public long getEndsAt(){return endsAt;} public long getBidCount(){return bidCount;} public boolean isClosed(){return closed;}
 public String toString(){return "Аукцион #"+id+": "+title+"; цена: "+currentPrice+" RUB; лидер: "+(highestBidder==null?"нет ставок":highestBidder)+"; ставок: "+bidCount+"; окончание: "+Instant.ofEpochMilli(endsAt)+"; статус: "+(closed?"ЗАКРЫТ":"ОТКРЫТ");}
}
