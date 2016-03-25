package model

case class Gate(id: Id, label: String, address: String, coords: Option[(Long, Long)])