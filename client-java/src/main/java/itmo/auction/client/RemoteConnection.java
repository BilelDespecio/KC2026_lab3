package itmo.auction.client;
import itmo.auction.api.AuctionService;
import itmo.auction.api.AuctionManagement;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;
public final class RemoteConnection implements AutoCloseable {
 private final InitialContext context;
 public RemoteConnection() throws Exception {
  String user=System.getenv("AUCTION_USER"),pass=System.getenv("AUCTION_PASSWORD");
  if(user==null||pass==null) throw new IllegalStateException("Set AUCTION_USER and AUCTION_PASSWORD.");
  Properties p=new Properties();p.put(Context.INITIAL_CONTEXT_FACTORY,"org.wildfly.naming.client.WildFlyInitialContextFactory");
  p.put(Context.PROVIDER_URL,System.getenv().getOrDefault("AUCTION_URL","remote+http://127.0.0.1:8080"));
  p.put(Context.SECURITY_PRINCIPAL,user);p.put(Context.SECURITY_CREDENTIALS,pass);context=new InitialContext(p);
 }
 public AuctionService service() throws Exception {return (AuctionService)context.lookup("ejb:/auction-server/AuctionServiceBean!itmo.auction.api.AuctionService");}
 public AuctionManagement management() throws Exception {return (AuctionManagement)context.lookup("ejb:/auction-server/AuctionServiceBean!itmo.auction.api.AuctionManagement");}
 public void close() throws Exception {context.close();}
}
