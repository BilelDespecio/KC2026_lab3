package itmo.auction.server;
import itmo.auction.api.*;
import jakarta.ejb.*;
import jakarta.annotation.security.PermitAll;
import jakarta.persistence.*;
@Remote({AuctionService.class, AuctionManagement.class})
@Stateless
@PermitAll
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuctionServiceBean implements AuctionManagement {
 @PersistenceContext(unitName="auctionPU") private EntityManager em;
 public long createAuction(String title,long startingPrice,int durationSeconds) throws AuctionException {
  if(title==null||title.isBlank()||title.trim().length()>120) throw new AuctionException("Название: от 1 до 120 символов.");
  if(startingPrice<0) throw new AuctionException("Начальная цена не может быть отрицательной.");
  if(durationSeconds<1||durationSeconds>86400) throw new AuctionException("Длительность: от 1 до 86400 секунд.");
  Auction a=new Auction();a.title=title.trim();a.currentPrice=startingPrice;
  a.endsAt=System.currentTimeMillis()+durationSeconds*1000L;em.persist(a);em.flush();return a.id;
 }
 public AuctionView placeBid(long id,String bidder,long amount) throws AuctionException {
  if(bidder==null||bidder.isBlank()||bidder.trim().length()>80) throw new AuctionException("Имя участника: от 1 до 80 символов.");
  // The database row lock serializes bids on this lot across bean instances.
  Auction a=em.find(Auction.class,id,LockModeType.PESSIMISTIC_WRITE);
  if(a==null) throw new AuctionException("Аукцион не найден.");
  long now=System.currentTimeMillis(); // Check the deadline AFTER acquiring the lock.
  if(a.cancelled||a.closedManually||now>=a.endsAt) throw new AuctionException("Аукцион уже завершён.");
  if(amount<=a.currentPrice) throw new AuctionException("Ставка должна превышать текущую цену.");
  a.currentPrice=amount;a.highestBidder=bidder.trim();
  Bid b=new Bid();b.auction=a;b.bidder=a.highestBidder;b.amount=amount;b.createdAt=now;em.persist(b);
  em.flush();return view(a);
 }
 public AuctionView getAuction(long id) throws AuctionException {
  // Shared row lock keeps price, leader and history count in one consistent snapshot.
  Auction a=em.find(Auction.class,id,LockModeType.PESSIMISTIC_READ);
  if(a==null) throw new AuctionException("Аукцион не найден.");return view(a);
 }
 private boolean isClosed(Auction a){return a.cancelled||a.closedManually||System.currentTimeMillis()>=a.endsAt;}
 private Auction require(long id,LockModeType lock) throws AuctionException {
  Auction a=em.find(Auction.class,id,lock);if(a==null)throw new AuctionException("Аукцион не найден.");return a;
 }
 public java.util.List<AuctionView> listOpenAuctions(){
  java.util.List<Auction> lots=em.createQuery("select a from Auction a where a.cancelled=false and a.closedManually=false and a.endsAt>:now order by a.id",Auction.class).setParameter("now",System.currentTimeMillis()).getResultList();
  java.util.List<AuctionView> result=new java.util.ArrayList<>();for(Auction a:lots)result.add(view(a));return result;
 }
 public java.util.List<BidView> getBidHistory(long id) throws AuctionException {
  require(id,LockModeType.PESSIMISTIC_READ);
  java.util.List<Bid> bids=em.createQuery("select b from Bid b where b.auction.id=:id order by b.id",Bid.class).setParameter("id",id).getResultList();
  java.util.List<BidView> result=new java.util.ArrayList<>();for(Bid b:bids)result.add(new BidView(b.id,id,b.bidder,b.amount,b.createdAt));return result;
 }
 public AuctionResult closeAuction(long id) throws AuctionException {
  Auction a=require(id,LockModeType.PESSIMISTIC_WRITE);
  if(a.cancelled)throw new AuctionException("Отменённый аукцион нельзя завершить.");
  a.closedManually=true;em.flush();return result(a);
 }
 public AuctionResult cancelAuction(long id) throws AuctionException {
  Auction a=require(id,LockModeType.PESSIMISTIC_WRITE);
  if(a.cancelled)return result(a);
  if(isClosed(a))throw new AuctionException("Завершённый аукцион нельзя отменить.");
  if(view(a).getBidCount()>0)throw new AuctionException("Нельзя отменить аукцион с принятыми ставками.");
  a.cancelled=true;em.flush();return result(a);
 }
 public AuctionResult getAuctionResult(long id) throws AuctionException {
  Auction a=require(id,LockModeType.PESSIMISTIC_READ);
  if(!isClosed(a))throw new AuctionException("Аукцион ещё открыт.");return result(a);
 }
 private AuctionResult result(Auction a){
  AuctionView v=view(a);boolean sold=!a.cancelled&&v.getBidCount()>0;
  return new AuctionResult(v,a.cancelled?"ОТМЕНЁН":sold?"ПРОДАНО":"БЕЗ СТАВОК",sold?a.highestBidder:null,sold?a.currentPrice:0);
 }
 private AuctionView view(Auction a){
  long n=em.createQuery("select count(b) from Bid b where b.auction.id=:id",Long.class).setParameter("id",a.id).getSingleResult();
  return new AuctionView(a.id,a.title,a.currentPrice,a.highestBidder,a.endsAt,n,isClosed(a));
 }
}
