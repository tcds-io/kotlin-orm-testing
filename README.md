# Kotlin ORM Testing utilities

Mockk extension for testing [kotlin-orm](https://github.com/tcds-io/kotlin-orm)

### installing
```gradle
testImplementation("io.tcds.orm:testing:0.1.0")
```

### Match Query
will match a raw query of a given where statement
```kotlin
val condition = where(table.id equalsTo "address-xxx") and (table.number differentOf "NA")

mock.findBy(condition)

verify { mock.findBy(matchQuery { "WHERE id = `address-xxx` AND number != `NA`" }) }
```


### Match Params
will match the params provided in methods that require a list of Params
```kotlin
val condition = where(table.id equalsTo "address-xxx")
val params = listOf(
    Param(table.main, false),
    Param(table.number, "890"),
    Param(table.street, "Foo Bar"),
)

mock.update(params, condition)

verify { mock.update(matchParams { listOf("main" to false, "number" to "890", "street" to "Foo Bar") }, any()) }
```
