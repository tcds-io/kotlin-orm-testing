package fixtures

import java.time.LocalDateTime
import java.time.Month

data class Address constructor(
    val id: String,
    val street: String,
    val number: String,
    val main: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun galaxyAvenue() = Address(
            id = "galaxy-avenue",
            street = "Galaxy Avenue",
            number = "123T",
            main = true,
            createdAt = LocalDateTime.of(1995, Month.APRIL, 15, 9, 15, 33),
        )

        fun galaxyHighway() = Address(
            id = "galaxy-highway",
            street = "Galaxy Highway",
            number = "678H",
            main = false,
            createdAt = LocalDateTime.of(1995, Month.APRIL, 15, 9, 15, 33),
        )
    }

    fun updated(street: String? = null, number: String? = null, main: Boolean? = null) = Address(
        id = id,
        street = street ?: this.street,
        number = number ?: this.number,
        main = main ?: this.main,
        createdAt = createdAt,
    )
}
