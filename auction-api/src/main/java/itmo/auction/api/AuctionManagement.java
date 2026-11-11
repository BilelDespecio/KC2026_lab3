package itmo.auction.api;
import jakarta.ejb.Remote;
import java.util.List;
/** Additive Lab 3 API. The original AuctionService stays unchanged. */
@Remote
public interface AuctionManagement extends AuctionService {
 List<AuctionView> listOpenAuctions();
 List<BidView> getBidHistory(long id) throws AuctionException;
 AuctionResult closeAuction(long id) throws AuctionException;
 AuctionResult cancelAuction(long id) throws AuctionException;
 AuctionResult getAuctionResult(long id) throws AuctionException;
}
