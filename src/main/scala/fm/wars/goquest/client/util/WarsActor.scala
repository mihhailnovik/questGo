package fm.wars.goquest.client.util

import akka.actor.{ ActorRef, ActorSystem, Props }

object WarsActor {
  def apply(props: Props)(implicit actorSystem: ActorSystem): ActorRef = actorSystem.actorOf(props)

  //noinspection ScalaStyle
  class SeqA(val s: Seq[ActorRef]) {
    def !(message: Any) = {
      s.foreach(_ ! message)
    }
  }

  implicit def seqToSeqA(seq: Seq[ActorRef]) = new SeqA(seq)

}
