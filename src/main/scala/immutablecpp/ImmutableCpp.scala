package immutablecpp


case class ClassDescriptor(name: String,
                           fields: List[Field],
                           baseClass: Option[BaseClass] = None)

case class GenOptions(indentSpaces: Int = 4,
                      genHeaderGuards: Boolean = true,
                      genImmutableSetters: Boolean = true)


case class Field(name: String, typename: String)
case class BaseClass(name: String)

object ImmutableClassDefGenerator {

  def generate(classDescriptor: ClassDescriptor,
               options: GenOptions = GenOptions()): String = {

    val indent = " " * options.indentSpaces

    val inherits = classDescriptor.baseClass.map(bc => s": public ${bc.name} ").getOrElse("")

    val privMembers: String =
      classDescriptor.fields.map { case Field(name, typename) =>
        s"$indent$typename ${name}_;"
      }.mkString("\n")


    val constructorParams = classDescriptor.fields.map(f => s"const ${f.typename} & ${f.name}").mkString(", ")
    val constructorInitializerList = classDescriptor.fields.map(f => s"${f.name}_(${f.name})").mkString(", ")
    val constructor: String =
      s"""$indent${classDescriptor.name}($constructorParams)
         |$indent$indent: $constructorInitializerList
         |$indent$indent{}""".stripMargin

    val accessors: String =
      classDescriptor.fields.map { case Field(name, typename) =>
        s"$indent$typename $name() const { return ${name}_; }"
      }.mkString("\n")

    val immutableSetters: String =
      classDescriptor.fields.map(f => genImmutableSetterFor(f, classDescriptor, indent)).mkString("\n")

    val headerGuardName = s"${classDescriptor.name.toUpperCase}_H"

    val headerGuardBegin =
      s"""#ifndef $headerGuardName
         |#define $headerGuardName
         |""".stripMargin

    s"""${if(options.genHeaderGuards) headerGuardBegin else ""}
       |class ${classDescriptor.name} $inherits{
       |private:
       |$privMembers
       |
       |public:
       |$constructor
       |
       |$accessors
       |
       |${if(options.genImmutableSetters) immutableSetters else ""}
       |};
       |${if(options.genHeaderGuards) "\n#endif" else ""}
       |""".stripMargin
  }

  def genImmutableSetterFor(field: Field, classDescriptor: ClassDescriptor, indent: String): String = {
    val fieldIdx = classDescriptor.fields.indexOf(field)
    val (fieldsPre, _ :: fieldsPost) = classDescriptor.fields.splitAt(fieldIdx)
    val fieldNamesPre = (fieldsPre.map(f => s"${f.name}_") :+ "").mkString(", ")
    val fieldNamesPost = ("" +: fieldsPost.map(f => s"${f.name}_")).mkString(", ")
    s"""$indent${classDescriptor.name} set${field.name.capitalize}(const ${field.typename} & ${field.name}) const {
       |${indent * 2}return ${classDescriptor.name}($fieldNamesPre${field.name}$fieldNamesPost);
       |$indent}""".stripMargin
  }

}