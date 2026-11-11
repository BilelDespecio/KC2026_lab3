package itmo.auction.client;
import itmo.auction.api.*;
import java.util.*;
import java.util.concurrent.*;
public final class Lab3Checks {
 interface Action {void run()throws Exception;}
 static int count;
 static void ok(boolean b,String s){if(!b)throw new AssertionError(s);System.out.println("PASS "+(++count)+" "+s);}
 static void reject(Action a,String s)throws Exception {try{a.run();throw new AssertionError(s);}catch(AuctionException expected){ok(true,s);}}
 static boolean listed(AuctionManagement s,long id)throws Exception {return s.listOpenAuctions().stream().anyMatch(a->a.getId()==id);}
 public static void main(String[] args)throws Exception {
  try(RemoteConnection c=new RemoteConnection()){
   AuctionManagement s=c.management();long id=s.createAuction("Complete lifecycle",1000,3600);
   ok(listed(s,id),"new lot is open");ok(s.getBidHistory(id).isEmpty(),"empty history");
   reject(()->s.getAuctionResult(id),"open lot has no terminal result");
   s.placeBid(id,"A",1200);s.placeBid(id,"B",1500);
   var history=s.getBidHistory(id);ok(history.size()==2,"two history rows");
   ok(history.get(0).getAmount()==1200&&history.get(1).getAmount()==1500,"history order");
   ok(history.stream().allMatch(b->b.getAuctionId()==id),"history lot identity");
   reject(()->s.placeBid(id,"C",1500),"equal bid refused");
   reject(()->s.placeBid(id,"C",1400),"lower bid refused");
   reject(()->s.cancelAuction(id),"lot with accepted bids cannot be cancelled");
   ok(!s.getAuction(id).isClosed(),"failed cancellation leaves lot open");
   var result=s.closeAuction(id);ok("ПРОДАНО".equals(result.getStatus())&&"B".equals(result.getWinner())&&result.getFinalPrice()==1500,"close produces winning result");
   ok(!listed(s,id),"closed lot excluded from list");ok(s.getAuction(id).isClosed(),"legacy view sees manual close");
   reject(()->s.placeBid(id,"C",2000),"closed lot rejects bids");
   ok(s.closeAuction(id).getFinalPrice()==1500,"repeated close idempotent");
   ok(s.getBidHistory(id).size()==2,"history preserved after close");
   reject(()->s.cancelAuction(id),"cannot cancel closed lot");
   long empty=s.createAuction("No bids",1000,60);var emptyResult=s.closeAuction(empty);
   ok("БЕЗ СТАВОК".equals(emptyResult.getStatus())&&emptyResult.getWinner()==null&&emptyResult.getFinalPrice()==0,"no bid means no sale");
   long cancelled=s.createAuction("Cancel",50,60);var cancelledResult=s.cancelAuction(cancelled);
   ok("ОТМЕНЁН".equals(cancelledResult.getStatus())&&cancelledResult.getWinner()==null&&cancelledResult.getFinalPrice()==0,"cancellation result");
   ok(s.getAuction(cancelled).isClosed()&&!listed(s,cancelled),"cancelled lot legacy closed and not listed");
   ok("ОТМЕНЁН".equals(s.cancelAuction(cancelled).getStatus()),"cancel idempotent");
   reject(()->s.placeBid(cancelled,"X",100),"cannot bid on cancelled lot");
   reject(()->s.closeAuction(cancelled),"cannot close cancelled lot");
   ok("ОТМЕНЁН".equals(s.getAuctionResult(cancelled).getStatus()),"cancelled result readable");
   long expired=s.createAuction("Expiry",100,1);s.placeBid(expired,"Winner",200);Thread.sleep(1200);
   ok("Winner".equals(s.getAuctionResult(expired).getWinner()),"deadline result without manual close");
   ok(!listed(s,expired),"expired lot not listed");
   reject(()->s.cancelAuction(expired),"expired lot cannot be cancelled");
   reject(()->s.placeBid(expired,"Late",500),"deadline rejects late bid");
   reject(()->s.getBidHistory(Long.MAX_VALUE),"missing history lot");
   reject(()->s.closeAuction(Long.MAX_VALUE),"missing close lot");
   reject(()->s.cancelAuction(Long.MAX_VALUE),"missing cancel lot");
   reject(()->s.getAuctionResult(Long.MAX_VALUE),"missing result lot");
   reject(()->s.createAuction("x",1,86401),"duration bound");
   // Competing bid/cancel requests must leave either a cancelled empty lot or an open bid lot.
   for(int trial=0;trial<3;trial++){
    long race=s.createAuction("Race",0,60);var pool=Executors.newFixedThreadPool(2);var go=new CountDownLatch(1);
    try {
     Future<Boolean> bid=pool.submit(()->{go.await();try(RemoteConnection cc=new RemoteConnection()){try{cc.service().placeBid(race,"Racer",100);return true;}catch(AuctionException e){return false;}}});
     Future<Boolean> cancel=pool.submit(()->{go.await();try(RemoteConnection cc=new RemoteConnection()){try{cc.management().cancelAuction(race);return true;}catch(AuctionException e){return false;}}});
     go.countDown();boolean b=bid.get(40,TimeUnit.SECONDS),k=cancel.get(40,TimeUnit.SECONDS);
     ok(b!=k,"bid/cancel race has one winning action "+trial);
     ok(s.getBidHistory(race).size()==(b?1:0),"race history consistent "+trial);
     ok(s.getAuction(race).isClosed()==k,"race state consistent "+trial);
    }finally{pool.shutdownNow();}
   }
   System.out.println("ALL "+count+" LAB3 CHECKS PASSED");
  }
 }
}
