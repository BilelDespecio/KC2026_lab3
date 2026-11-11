package itmo.auction.server;
import jakarta.persistence.*;
@Entity @Table(name="auction_bid")
public class Bid {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Auction auction;
 @Column(nullable=false,length=80) public String bidder;
 public long amount; public long createdAt;
 public Bid() {}
}
