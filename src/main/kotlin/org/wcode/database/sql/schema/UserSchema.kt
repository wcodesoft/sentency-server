package org.wcode.database.sql.schema

import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.wcode.database.sql.core.BaseSchema
import org.wcode.database.sql.tables.UserTable
import org.wcode.dto.UserDTO
import java.util.*

class UserSchema(id: EntityID<UUID>) : BaseSchema<UserDTO>(id) {

    companion object : UUIDEntityClass<UserSchema>(UserTable)

    var email by UserTable.email
    var password by UserTable.password

    override fun toDTO(): UserDTO = UserDTO(
        id = this.id.toString(),
        email = this.email,
        password = this.password
    )

}
