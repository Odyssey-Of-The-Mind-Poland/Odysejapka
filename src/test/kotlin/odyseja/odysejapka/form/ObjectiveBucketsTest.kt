package odyseja.odysejapka.form

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ObjectiveBucketsTest {

    @Test
    fun `should define all objective buckets correctly`() {
        assertThat(ObjectiveBuckets.values()).hasSize(3)
    }

    @Test
    fun `ZERO_THREE should have correct display name and buckets`() {
        val bucket = ObjectiveBuckets.ZERO_THREE
        
        assertThat(bucket.displayName).isEqualTo("0, 3")
        assertThat(bucket.buckets).containsExactly(0, 3)
    }

    @Test
    fun `ZERO_FIVE should have correct display name and buckets`() {
        val bucket = ObjectiveBuckets.ZERO_FIVE
        
        assertThat(bucket.displayName).isEqualTo("0, 5")
        assertThat(bucket.buckets).containsExactly(0, 5)
    }

    @Test
    fun `ZERO_FIVE_TEN_FIFTEEN should have correct display name and buckets`() {
        val bucket = ObjectiveBuckets.ZERO_FIVE_TEN_FIFTEEN
        
        assertThat(bucket.displayName).isEqualTo("0, 5, 10, 15")
        assertThat(bucket.buckets).containsExactly(0, 5, 10, 15)
    }

    @Test
    fun `toBucketsResponse should convert ZERO_THREE correctly`() {
        val bucket = ObjectiveBuckets.ZERO_THREE
        val dto = bucket.toBucketsResponse()
        
        assertThat(dto.name).isEqualTo("ZERO_THREE")
        assertThat(dto.displayName).isEqualTo("0, 3")
        assertThat(dto.bucketsDisplay).isEqualTo("0, 3")
    }

    @Test
    fun `toBucketsResponse should convert ZERO_FIVE correctly`() {
        val bucket = ObjectiveBuckets.ZERO_FIVE
        val dto = bucket.toBucketsResponse()
        
        assertThat(dto.name).isEqualTo("ZERO_FIVE")
        assertThat(dto.displayName).isEqualTo("0, 5")
        assertThat(dto.bucketsDisplay).isEqualTo("0, 5")
    }

    @Test
    fun `toBucketsResponse should convert ZERO_FIVE_TEN_FIFTEEN correctly`() {
        val bucket = ObjectiveBuckets.ZERO_FIVE_TEN_FIFTEEN
        val dto = bucket.toBucketsResponse()
        
        assertThat(dto.name).isEqualTo("ZERO_FIVE_TEN_FIFTEEN")
        assertThat(dto.displayName).isEqualTo("0, 5, 10, 15")
        assertThat(dto.bucketsDisplay).isEqualTo("0, 5, 10, 15")
    }
}

