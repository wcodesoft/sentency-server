package wcode.software.database.controllers

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import wcode.software.database.base.BaseDAO
import wcode.software.database.models.Author
import wcode.software.database.schema.QuoteDB
import wcode.software.dtos.QuoteDTO
import wcode.software.models.Quote
import wcode.software.utils.CalendarUtils
import java.util.*
import kotlin.collections.ArrayList

class QuoteDAO : BaseDAO<QuoteDTO, Quote> {

    override fun getAll(): ArrayList<QuoteDTO> {
        val quotes = arrayListOf<QuoteDTO>()
        transaction {
            Quote.all().forEach { quote ->
                try {
                    quotes.add(QuoteDTO(quote))
                } catch (e: Exception) {
                    quote.delete()
                }
            }
        }
        return quotes
    }

    override fun getCount(): Int {
        return transaction {
            Quote.all().count().toInt()
        }
    }

    fun getMonthCount(): Int {
        val (start, end) = CalendarUtils.getMonthStartEnd()
        return transaction {
            QuoteDB.select {
                (QuoteDB.timestap greaterEq start) and (QuoteDB.timestap lessEq end)
            }.count().toInt()
        }
    }

    fun getRandom(): QuoteDTO {
        return QuoteDTO(Quote.all().toList().random())
    }

    override fun insert(obj: QuoteDTO) {
        transaction {
            val mAuthor = Author.findById(UUID.fromString(obj.authorId))
            mAuthor?.let {
                QuoteDB.insert {
                    it[author] = mAuthor.id
                    it[quote] = obj.quote
                    it[timestap] = Calendar.getInstance().timeInMillis
                }
            }
        }
    }

    override fun remove(id: String) {
        transaction {
            val quote = Quote.findById(UUID.fromString(id))
            quote?.delete()
        }
    }

    override fun update(obj: QuoteDTO) {
        transaction {
            obj.id?.let {
                val quote = Quote.findById(UUID.fromString(it)) ?: return@transaction
                val mAuthor = Author.findById(UUID.fromString(obj.authorId)) ?: return@transaction
                quote.quote = obj.quote
                quote.author = mAuthor
                quote.timestamp = obj.timestamp
            }
        }
    }
}