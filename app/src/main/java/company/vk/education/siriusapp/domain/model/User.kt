package company.vk.education.siriusapp.domain.model

data class User(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val contact: UserContacts,
    val rating: Double = 100.0,
) {
    override fun equals(other: Any?) = other is User && id == other.id

    override fun hashCode() = id.hashCode()
}
