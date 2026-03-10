package odyseja.odysejapka.spontan

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATOR"])
class SpontanDefinitionTest : OdysejaDsl() {

    @Test
    fun `should create verbal spontan`() {
        val created = spontanClient.create(
            SpontanDefinition(
                name = "Słowny spontan",
                type = SpontanType.VERBAL
            )
        )

        assertThat(created.id).isNotNull()
        assertThat(created.name).isEqualTo("Słowny spontan")
        assertThat(created.type).isEqualTo(SpontanType.VERBAL)
        assertThat(created.fields).isEmpty()
    }

    @Test
    fun `should create manual spontan with fields`() {
        val created = spontanClient.create(
            SpontanDefinition(
                name = "Manualny spontan",
                type = SpontanType.MANUAL,
                multiplier = 2.0,
                fields = listOf(
                    SpontanFieldEntry(name = "Element 1", multiplier = 1.5),
                    SpontanFieldEntry(name = "Element 2", multiplier = 2.0)
                )
            )
        )

        assertThat(created.id).isNotNull()
        assertThat(created.name).isEqualTo("Manualny spontan")
        assertThat(created.type).isEqualTo(SpontanType.MANUAL)
        assertThat(created.multiplier).isEqualTo(2.0)
        assertThat(created.fields).hasSize(2)
        assertThat(created.fields[0].name).isEqualTo("Element 1")
        assertThat(created.fields[0].multiplier).isEqualTo(1.5)
        assertThat(created.fields[1].name).isEqualTo("Element 2")
    }

    @Test
    fun `should list all spontans`() {
        spontanClient.create(SpontanDefinition(name = "Spontan A", type = SpontanType.VERBAL))
        spontanClient.create(SpontanDefinition(name = "Spontan B", type = SpontanType.MANUAL, multiplier = 1.0))

        val all = spontanClient.getAll()

        assertThat(all.map { it.name }).contains("Spontan A", "Spontan B")
    }

    @Test
    fun `should get spontan by id`() {
        val created = spontanClient.create(
            SpontanDefinition(name = "Do pobrania", type = SpontanType.VERBAL)
        )

        val fetched = spontanClient.getById(created.id!!)

        assertThat(fetched.name).isEqualTo("Do pobrania")
        assertThat(fetched.type).isEqualTo(SpontanType.VERBAL)
    }

    @Test
    fun `should update spontan`() {
        val created = spontanClient.create(
            SpontanDefinition(name = "Stara nazwa", type = SpontanType.VERBAL)
        )

        val updated = spontanClient.update(
            created.id!!,
            SpontanDefinition(
                name = "Nowa nazwa",
                type = SpontanType.MANUAL,
                multiplier = 3.0,
                fields = listOf(SpontanFieldEntry(name = "Pole", multiplier = 1.0))
            )
        )

        assertThat(updated.name).isEqualTo("Nowa nazwa")
        assertThat(updated.type).isEqualTo(SpontanType.MANUAL)
        assertThat(updated.multiplier).isEqualTo(3.0)
        assertThat(updated.fields).hasSize(1)
    }

    @Test
    fun `should delete spontan`() {
        val created = spontanClient.create(
            SpontanDefinition(name = "Do usunięcia", type = SpontanType.VERBAL)
        )

        spontanClient.delete(created.id!!)

        val all = spontanClient.getAll()
        assertThat(all.map { it.id }).doesNotContain(created.id)
    }

    @Test
    fun `should ignore fields for verbal spontan`() {
        val created = spontanClient.create(
            SpontanDefinition(
                name = "Słowny z polami",
                type = SpontanType.VERBAL,
                fields = listOf(SpontanFieldEntry(name = "Ignorowane", multiplier = 1.0))
            )
        )

        val fetched = spontanClient.getById(created.id!!)
        assertThat(fetched.fields).isEmpty()
    }

    @Test
    fun `should create manual spontan with expression field`() {
        val created = spontanClient.create(
            SpontanDefinition(
                name = "Spontan z wyrażeniem",
                type = SpontanType.MANUAL,
                fields = listOf(
                    SpontanFieldEntry(
                        name = "Bonus",
                        fieldType = SpontanFieldType.EXPRESSION,
                        expression = "FLOOR(v/3)*5"
                    )
                )
            )
        )

        assertThat(created.id).isNotNull()
        assertThat(created.fields).hasSize(1)
        assertThat(created.fields[0].fieldType).isEqualTo(SpontanFieldType.EXPRESSION)
        assertThat(created.fields[0].expression).isEqualTo("FLOOR(v/3)*5")
    }

    @Test
    @Disabled
    fun `should reject invalid expression`() {
        assertThatThrownBy {
            spontanClient.create(
                SpontanDefinition(
                    name = "Nieprawidłowy",
                    type = SpontanType.MANUAL,
                    fields = listOf(
                        SpontanFieldEntry(
                            name = "Złe pole",
                            fieldType = SpontanFieldType.EXPRESSION,
                            expression = "FLOOR(v/3"
                        )
                    )
                )
            )
        }.hasMessageContaining("nieprawidłowe wyrażenie")
    }
}
