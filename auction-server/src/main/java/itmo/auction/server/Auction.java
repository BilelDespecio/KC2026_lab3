package itmo.auction.server;
import jakarta.persistence.*;
@Entity @Table(name="auction_lot")
public class Auction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=120) public String title;
 public long currentPrice; public long endsAt;
 @Column(length=80) public String highestBidder;
 public boolean closedManually; public boolean cancelled;
 @Version public long version;
 public Auction() {}
}
