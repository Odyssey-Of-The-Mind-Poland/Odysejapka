package odyseja.odysejapka.form

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SubjectiveRangesTest {

    @Test
    fun `should define all subjective ranges correctly`() {
        assertThat(SubjectiveRanges.values()).hasSize(7)
    }

    @Test
    fun `ONE_TO_FIVE should have correct FR and FO values`() {
        val range = SubjectiveRanges.ONE_TO_FIVE
        
        assertThat(range.displayName).isEqualTo("1-5")
        assertThat(range.frValues.values).containsExactly(2, 3, 4, 5)
        assertThat(range.foRanges.min).isEqualTo(2)
        assertThat(range.foRanges.max).isEqualTo(5)
    }

    @Test
    fun `ONE_TO_EIGHT should have correct FR and FO values`() {
        val range = SubjectiveRanges.ONE_TO_EIGHT
        
        assertThat(range.displayName).isEqualTo("1-8")
        assertThat(range.frValues.values).containsExactly(2, 3, 5, 7)
        assertThat(range.foRanges.min).isEqualTo(2)
        assertThat(range.foRanges.max).isEqualTo(8)
    }

    @Test
    fun `ONE_TO_TEN should have correct FR and FO values`() {
        val range = SubjectiveRanges.ONE_TO_TEN
        
        assertThat(range.displayName).isEqualTo("1-10")
        assertThat(range.frValues.values).containsExactly(3, 5, 7, 9)
        assertThat(range.foRanges.min).isEqualTo(3)
        assertThat(range.foRanges.max).isEqualTo(10)
    }

    @Test
    fun `ONE_TO_FIFTEEN should have correct FR and FO values`() {
        val range = SubjectiveRanges.ONE_TO_FIFTEEN
        
        assertThat(range.displayName).isEqualTo("1-15")
        assertThat(range.frValues.values).containsExactly(4, 7, 10, 13)
        assertThat(range.foRanges.min).isEqualTo(4)
        assertThat(range.foRanges.max).isEqualTo(15)
    }

    @Test
    fun `ONE_TO_TWENTY should have correct FR and FO values`() {
        val range = SubjectiveRanges.ONE_TO_TWENTY
        
        assertThat(range.displayName).isEqualTo("1-20")
        assertThat(range.frValues.values).containsExactly(6, 10, 14, 18)
        assertThat(range.foRanges.min).isEqualTo(5)
        assertThat(range.foRanges.max).isEqualTo(20)
    }

    @Test
    fun `ONE_TO_TWENTY_THREE should have correct FR and FO values`() {
        val range = SubjectiveRanges.ONE_TO_TWENTY_THREE
        
        assertThat(range.displayName).isEqualTo("1-23")
        assertThat(range.frValues.values).containsExactly(9, 13, 17, 21)
        assertThat(range.foRanges.min).isEqualTo(8)
        assertThat(range.foRanges.max).isEqualTo(23)
    }

    @Test
    fun `ONE_TO_TWENTY_FIVE should have correct FR and FO values`() {
        val range = SubjectiveRanges.ONE_TO_TWENTY_FIVE
        
        assertThat(range.displayName).isEqualTo("1-25")
        assertThat(range.frValues.values).containsExactly(8, 13, 18, 23)
        assertThat(range.foRanges.min).isEqualTo(6)
        assertThat(range.foRanges.max).isEqualTo(25)
    }

    @Test
    fun `FrValues should store list of values`() {
        val frValues = FrValues(listOf(2, 3, 4, 5))
        assertThat(frValues.values).containsExactly(2, 3, 4, 5)
    }

    @Test
    fun `FoRanges should store min and max`() {
        val foRanges = FoRanges(2, 8)
        assertThat(foRanges.min).isEqualTo(2)
        assertThat(foRanges.max).isEqualTo(8)
    }
}

