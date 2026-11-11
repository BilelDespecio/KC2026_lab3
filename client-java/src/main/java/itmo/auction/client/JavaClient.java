package itmo.auction.client;
import itmo.auction.api.*;
public final class JavaClient {
 public static void main(String[] args) throws Exception {
  if(args.length>0 && "menu".equals(args[0])){JavaMenu.main(args);return;}
  try(RemoteConnection c=new RemoteConnection()) {
   AuctionService s=c.service();String op=args.length==0?"demo":args[0];
   switch(op){
    case "create": System.out.println("ID: "+s.createAuction(args[1],Long.parseLong(args[2]),Integer.parseInt(args[3])));break;
    case "bid": System.out.println(s.placeBid(Long.parseLong(args[1]),args[2],Long.parseLong(args[3])));break;
    case "show": System.out.println(s.getAuction(Long.parseLong(args[1])));break;
    case "demo":
     long id=s.createAuction("Наушники / Java",1000,600);System.out.println(s.getAuction(id));
     System.out.println(s.placeBid(id,"Билел",1200));System.out.println(s.placeBid(id,"Иван",1500));
     try{s.placeBid(id,"Анна",1400);throw new AssertionError("Low bid accepted");}catch(AuctionException e){System.out.println("Ожидаемый отказ: "+e.getMessage());}
     System.out.println("ID для второго клиента: "+id);break;
    default:throw new IllegalArgumentException("demo | create title price seconds | bid id name amount | show id");
   }
  }
 }
}
