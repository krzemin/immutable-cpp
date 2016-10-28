import immutablecpp._
import immutablecpp.dsl._

object Main extends App {

  implicit val genOpts = GenOptions()

  defclass("User")
    .inherits("DomainObj")
    .f("id", "int")
    .f("name", "std::string")
    .f("age", "long")
    .f("height", "double")
    .printStdOut

}
