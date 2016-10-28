package immutablecpp

object dsl {

  def defclass(name: String): ClassDescriptor =
    ClassDescriptor(name, Nil)

  implicit class ClassDescriptorOps(val cd: ClassDescriptor) extends AnyVal {

    def f(name: String, typename: String): ClassDescriptor =
      cd.copy(fields = cd.fields :+ Field(name, typename))

    def inherits(name: String): ClassDescriptor =
      cd.copy(baseClass = Some(BaseClass(name)))

    def printStdOut(implicit genOptions: GenOptions): Unit = {
      println(ImmutableClassDefGenerator.generate(cd, genOptions))
    }
  }
}
