package odyseja.odysejapka.spontan

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "spontan_definition")
class SpontanDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false)
    var name: String = ""

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: SpontanType = SpontanType.VERBAL

    @Column
    var multiplier: Double? = null

    @Column(name = "form_data", columnDefinition = "jsonb", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = SpontanFieldsConverter::class)
    var fields: List<SpontanFieldEntry>? = null

    fun toSpontanDefinition() = SpontanDefinition(
        id = id,
        name = name,
        type = type,
        multiplier = multiplier,
        fields = fields ?: emptyList()
    )

    fun applyFrom(definition: SpontanDefinition) {
        name = definition.name
        type = definition.type
        multiplier = definition.multiplier
        fields = if (definition.type == SpontanType.MANUAL) definition.fields else null
    }
}
