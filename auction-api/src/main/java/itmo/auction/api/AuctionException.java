package itmo.auction.api;
import jakarta.ejb.ApplicationException;
@ApplicationException(rollback=true)
public class AuctionException extends Exception {
 private static final long serialVersionUID=1L;
 public AuctionException(String message) { super(message); }
}
