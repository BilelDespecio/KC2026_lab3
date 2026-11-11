package itmo.auction.client;
import itmo.auction.api.*;
import java.util.*;
import java.util.concurrent.*;
public final class IntegrationChecks {
 interface Action {void run() throws Exception;}
 static int checks=0;
 static void ok(boolean condition,String title){if(!condition)throw new AssertionError(title);System.out.println("PASS "+(++checks)+" "+title);}
 static void reject(Action a,String title)throws Exception {try{a.run();throw new AssertionError(title);}catch(AuctionException expected){ok(true,title);}}
 public static void main(String[] args)throws Exception {
  try(RemoteConnection c=new RemoteConnection()){
   AuctionService s=c.service();long id=s.createAuction("Checks",1000,120);
   ok(s.getAuction(id).getBidCount()==0,"new lot has no bids");
   reject(()->s.createAuction(" ",1,60),"empty title");reject(()->s.createAuction("x",-1,60),"negative price");
   reject(()->s.createAuction("x",1,0),"invalid duration");reject(()->s.getAuction(Long.MAX_VALUE),"missing lot");
   reject(()->s.placeBid(id," ",1200),"empty bidder");reject(()->s.placeBid(id,"A",1000),"equal price");
   ok(s.placeBid(id,"A",1200).getCurrentPrice()==1200,"valid bid");
   reject(()->s.placeBid(id,"B",1100),"lower bid");
   AuctionView after=s.getAuction(id);ok(after.getBidCount()==1&&after.getCurrentPrice()==1200&&"A".equals(after.getHighestBidder()),"rejection preserves price, leader and history");
   long concurrent=s.createAuction("Concurrent",0,120);
   ExecutorService pool=Executors.newFixedThreadPool(8);CountDownLatch go=new CountDownLatch(1);
   List<Future<Boolean>> jobs=new ArrayList<>();
   try {
    for(int i=1;i<=20;i++){final int n=i;jobs.add(pool.submit(()->{go.await();try(RemoteConnection cc=new RemoteConnection()){try{cc.service().placeBid(concurrent,"bidder"+n,n*100L);return true;}catch(AuctionException e){return false;}}}));}
    go.countDown();long accepted=0;for(Future<Boolean> job:jobs)if(job.get(45,TimeUnit.SECONDS))accepted++;
    AuctionView v=s.getAuction(concurrent);ok(v.getCurrentPrice()==2000&&"bidder20".equals(v.getHighestBidder()),"20 concurrent bids preserve maximum");
    ok(v.getBidCount()==accepted,"history contains exactly committed bids");
   } finally {pool.shutdownNow();}
   long exp=s.createAuction("Expired",1,1);Thread.sleep(1200);
   reject(()->s.placeBid(exp,"Late",10),"late bid refused");ok(s.getAuction(exp).isClosed(),"expired lot is reported closed");
   System.out.println("ALL "+checks+" CHECKS PASSED");
  }
 }
}
