package itmo.auction.client
import itmo.auction.api.AuctionService
import itmo.auction.api.AuctionException
class GroovyClient {
 static void main(String[] args) {
  if(args && args[0]=='menu'){GroovyMenu.main(args);return}
  def connection=new RemoteConnection()
  try {
   AuctionService service=connection.service()
   String command=args ? args[0] : 'demo'
   switch(command) {
    case 'show': println service.getAuction(args[1].toLong());break
    case 'bid': println service.placeBid(args[1].toLong(),args[2],args[3].toLong());break
    case 'create': println 'ID: '+service.createAuction(args[1],args[2].toLong(),args[3].toInteger());break
    case 'demo':
     long id=service.createAuction('Клавиатура / Groovy',500L,600)
     println service.getAuction(id)
     println service.placeBid(id,'Анна',700L)
     try { service.placeBid(id,'Иван',600L);throw new AssertionError('Low bid accepted') }
     catch(AuctionException e) { println 'Ожидаемый отказ: '+e.message }
     println service.getAuction(id);break
    default: throw new IllegalArgumentException('demo | create title price seconds | bid id name amount | show id')
   }
  } finally { connection.close() }
 }
}
