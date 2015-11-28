## KotliQuery

[![Build Status](https://travis-ci.org/seratch/kotliquery.svg)](https://travis-ci.org/seratch/kotliquery)

A handy Database access library in Kotlin. Highly inspired from [ScalikeJDBC](http://scalikejdbc.org/). 

Kotlin language is still beta, and this library is also still in beta stage.
When Kotlin 1.0 release, we'll consider releasing the first version of KotliQuery.

### How to use

KotliQuery is so easy-to-use. After reading this short documentation, you should be able to start using this library right now.

#### Creating DB Session

`Session` object, thin wrapper of `java.sql.Connection` instance, runs queries.

```kotlin
import kotliquery.*

val session = sessionOf("jdbc:h2:mem:hello", "user", "pass")
```

#### DDL Execution

```kotlin
session.run(queryOf("""
  create table members (
    id serial not null primary key,
    name varchar(64),
    created_at timestamp not null
  )
""").asExecute) // returns Boolean
```

#### Update Operations

```kotlin
val insertQuery: String = "insert into members (name,  created_at) values (?, ?)"

session.run(queryOf(insertQuery, "Alice", Date()).asUpdate) // returns effected row count
session.run(queryOf(insertQuery, "Bob", Date()).asUpdate)
```

#### Select Queries

Prepare select query execution in the following steps:

- Create `Query` object by using `queryOf` factory
- Bind extractor function (`(Row) -> A`) to the `Query` object via `#map` method
- Specify response type (`asList`/`asSingle`) at the end

```kotlin
val allIdsQuery = queryOf("select id from members").map { row -> row.int("id") }.asList
val ids: List<Int> = session.run(allIdsQuery)
```

Extractor function can return any type of result from `ResultSet`.

```kotlin
data class Member(
  val id: Int,
  val name: String?,
  val createdAt: java.time.ZonedDateTime)

val toMember: (Row) -> Member = { row -> 
  Member(row.int("id")!!, row.string("name"), row.zonedDateTime("created_at")!!)
}

val allMembersQuery = queryOf("select id, name, created_at from members").map(toMember).asList
val members: List<Member> = session.run(allMembersQuery)
```

```kotlin
val aliceQuery = queryOf("select id, name, created_at from members where name = ?", "Alice").map(toMember).asSingle
val alice: Member? = session.run(aliceQuery)
```

#### Working with large data set

`#forEach` allows you to make some side-effect in iterations. This API is useful for handling large `ResultSet`.

```kotlin
session.forEach(queryOf("select id from members"), { row ->
  // working with large data set
})
```

#### Transaction

`Session` object provides transaction block.

```kotlin
session.transaction { tx ->
  // begin
  tx.run(queryOf("insert into members (name,  created_at) values (?, ?)", "Alice", Date()).asUpdate)
}
// commit

session.transaction { tx ->
  // begin
  tx.run(queryOf("update members set name = ? where id = ?", "Chris", 1).asUpdate)
  throw RuntimeException() // rollback
}
```

## License

(The MIT License)

Copyright (c) 2015 Kazuhiro Sera
