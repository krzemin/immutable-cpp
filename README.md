## Immutable C++ class generator

This repository contains little code generator for immutable C++ classes.

It allows to define class descriptors using simple Scala-based DSL:

```scala
  defclass("User")
    .inherits("DomainObj")
    .f("id", "int")
    .f("name", "std::string")
    .f("age", "long")
    .f("height", "double")

```

Based on this descriptor following C++ class will be generated:


```cpp
#ifndef USER_H
#define USER_H

class User : public DomainObj {
private:
    int id_;
    std::string name_;
    long age_;
    double height_;

public:
    User(const int & id, const std::string & name, const long & age, const double & height)
        : id_(id), name_(name), age_(age), height_(height)
        {}

    int id() const { return id_; }
    std::string name() const { return name_; }
    long age() const { return age_; }
    double height() const { return height_; }

    User setId(const int & id) const {
        return User(id, name_, age_, height_);
    }
    User setName(const std::string & name) const {
        return User(id_, name, age_, height_);
    }
    User setAge(const long & age) const {
        return User(id_, name_, age, height_);
    }
    User setHeight(const double & height) const {
        return User(id_, name_, age_, height);
    }
};

#endif
```

### Usage

* edit `src/main/scala/Main.scala`
* `sbt --warn run`

### Disclaimer

This is only initial implementation, far from being complete.
Based on ideas found at
http://stackoverflow.com/questions/26858518/idiomatic-way-to-declare-c-immutable-classes

Thanks to Mateusz Kubuszok for insights and review.
