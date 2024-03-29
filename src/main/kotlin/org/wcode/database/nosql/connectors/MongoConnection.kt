package org.wcode.database.nosql.connectors

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.wcode.core.EnvironmentConfig
import org.wcode.database.nosql.core.BaseNoSQLConnector

class MongoConnection : BaseNoSQLConnector() {

    override fun createDB(): MongoDatabase {
        val client = KMongo.createClient("mongodb://${EnvironmentConfig.mongoHost}:27017")
        return client.getDatabase("sentency")
    }
}
