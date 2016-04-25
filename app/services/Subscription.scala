package services

trait Subscription {
  // the index is the subscriptionId
  // the value is the reference
  private var subscriptionMap = Map[String, String]()
  
  def addSubscription(subscriptionId: String, reference: String) = {
    subscriptionMap.synchronized {
      subscriptionMap += (subscriptionId -> reference)
    }
  }
  
  def getSubscription(subscriptionId: String) : Option[String] = {
    subscriptionMap.synchronized {
      subscriptionMap.get(subscriptionId)
    }
  }
}