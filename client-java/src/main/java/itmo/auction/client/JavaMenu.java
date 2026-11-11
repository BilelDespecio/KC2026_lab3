package itmo.auction.client;
import itmo.auction.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
public final class JavaMenu {
 private static final BufferedReader input=new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8));
 private static String read(String label)throws IOException {System.out.print(label+": ");String x=input.readLine();if(x==null)throw new EOFException();return x;}
 private static long number(String label)throws IOException {return Long.parseLong(read(label).trim());}
 public static void main(String[] args)throws Exception {
  try(RemoteConnection c=new RemoteConnection()){
   AuctionManagement s=c.management();
   while(true){
    System.out.println("\n=== АУКЦИОНЫ / JAVA ===\n1 Создать аукцион\n2 Открытые аукционы\n3 Информация об аукционе\n4 Сделать ставку\n5 История ставок\n6 Завершить аукцион\n7 Отменить аукцион без ставок\n8 Результат аукциона\n0 Выход");
    try {
     String op=read("Выбор").trim();long id;
     switch(op){
      case "0":System.out.println("До свидания!");return;
      case "1":String title=read("Название");long price=number("Начальная цена RUB");int seconds=Integer.parseInt(read("Длительность в секундах").trim());System.out.println("ID: "+s.createAuction(title,price,seconds));break;
      case "2":var lots=s.listOpenAuctions();if(lots.isEmpty())System.out.println("Открытых аукционов нет.");else lots.forEach(System.out::println);break;
      case "3":System.out.println(s.getAuction(number("ID аукциона")));break;
      case "4":id=number("ID аукциона");String name=read("Имя участника");System.out.println(s.placeBid(id,name,number("Сумма RUB")));break;
      case "5":var history=s.getBidHistory(number("ID аукциона"));if(history.isEmpty())System.out.println("Ставок пока нет.");else history.forEach(System.out::println);break;
      case "6":id=number("ID аукциона");if("ДА".equals(read("Досрочное завершение. Подтвердите ДА").trim()))System.out.println(s.closeAuction(id));else System.out.println("Действие отменено.");break;
      case "7":id=number("ID аукциона");if("ДА".equals(read("Подтвердите отмену ДА").trim()))System.out.println(s.cancelAuction(id));else System.out.println("Действие отменено.");break;
      case "8":System.out.println(s.getAuctionResult(number("ID аукциона")));break;
      default:System.out.println("Выберите пункт от 0 до 8.");
     }
    } catch(EOFException e){return;}catch(NumberFormatException e){System.out.println("Введите целое число допустимого размера.");}
    catch(AuctionException e){System.out.println("Отказ: "+e.getMessage());}
   }
  }
 }
}
