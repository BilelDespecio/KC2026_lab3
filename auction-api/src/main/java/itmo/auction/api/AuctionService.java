package itmo.auction.api;
import jakarta.ejb.Remote;
@Remote
public interface AuctionService {
 long createAuction(String title, long startingPrice, int durationSeconds) throws AuctionException;
 AuctionView placeBid(long auctionId, String bidder, long amount) throws AuctionException;
 AuctionView getAuction(long auctionId) throws AuctionException;
}
