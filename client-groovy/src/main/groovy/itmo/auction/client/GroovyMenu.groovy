package itmo.auction.client
import itmo.auction.api.*
import java.nio.charset.StandardCharsets
class GroovyMenu {
 private static final BufferedReader input=new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8))
 static String read(String label){ print label+': ';String x=input.readLine();if(x==null)throw new EOFException();return x }
 static long number(String label){return Long.parseLong(read(label).trim())}
 static void main(String[] args){
  def connection=new RemoteConnection()
  try {
   AuctionManagement service=connection.management()
   while(true){
    println "\n=== АУКЦИОНЫ / GROOVY ===\n1 Создать аукцион\n2 Открытые аукционы\n3 Информация об аукционе\n4 Сделать ставку\n5 История ставок\n6 Завершить аукцион\n7 Отменить аукцион без ставок\n8 Результат аукциона\n0 Выход"
    try {
     switch(read('Выбор').trim()){
      case '0':println 'До свидания!';return
      case '1':
       String title=read('Название');long price=number('Начальная цена RUB');int duration=Integer.parseInt(read('Длительность в секундах').trim())
       println 'ID: '+service.createAuction(title,price,duration);break
      case '2':
       def lots=service.listOpenAuctions();if(lots.empty)println 'Открытых аукционов нет.';else lots.each { println it };break
      case '3':println service.getAuction(number('ID аукциона'));break
      case '4':
       long id=number('ID аукциона');String name=read('Имя участника');println service.placeBid(id,name,number('Сумма RUB'));break
      case '5':
       def history=service.getBidHistory(number('ID аукциона'));if(history.empty)println 'Ставок пока нет.';else history.each { println it };break
      case '6':
       long closeId=number('ID аукциона');if(read('Досрочное завершение. Подтвердите ДА').trim()=='ДА')println service.closeAuction(closeId);else println 'Действие отменено.';break
      case '7':
       long cancelId=number('ID аукциона');if(read('Подтвердите отмену ДА').trim()=='ДА')println service.cancelAuction(cancelId);else println 'Действие отменено.';break
      case '8':println service.getAuctionResult(number('ID аукциона'));break
      default:println 'Выберите пункт от 0 до 8.'
     }
    } catch(EOFException e){return} catch(NumberFormatException e){println 'Введите целое число допустимого размера.'}
    catch(AuctionException e){println 'Отказ: '+e.message}
   }
  } finally {connection.close()}
 }
}
