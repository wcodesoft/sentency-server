package org.wcode.routes

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.wcode.core.setupTestApplication
import org.wcode.dto.AuthorDTO
import org.wcode.dto.PaginatedDTO
import java.util.*
import kotlin.test.assertEquals

class AuthorRoutesTest {

    @Test
    fun `Add Author with ID`() {
        val author = AuthorDTO(id = UUID.randomUUID().toString(), name = "Test")
        withTestApplication({ setupTestApplication() }) {
            handleRequest(HttpMethod.Post, "/author") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("apiKey", "APIKEY")
                setBody(author.toJson())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Delete, "/author/${author.id}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    assertEquals(HttpStatusCode.Accepted, response.status())
                    assertEquals("Author removed correctly", response.content)
                }
            }
        }
    }

    @Test
    fun `Add Author without ID`() {
        val author = AuthorDTO(name = "Test")
        var authorId = ""
        withTestApplication({ setupTestApplication() }) {
            handleRequest(HttpMethod.Post, "/author") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("apiKey", "APIKEY")
                setBody(author.toJson())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                response.content?.let {
                    val responseAuthor = Json.decodeFromString<AuthorDTO>(it)
                    authorId = responseAuthor.id
                }
            }

            handleRequest(HttpMethod.Delete, "/author/${authorId}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    assertEquals(HttpStatusCode.Accepted, response.status())
                    assertEquals("Author removed correctly", response.content)
                }
            }
        }
    }

    @Test
    fun `Update Author`() {
        val id = UUID.randomUUID().toString()
        val author = AuthorDTO(id = id, name = "Test")
        withTestApplication({ setupTestApplication() }) {
            handleRequest(HttpMethod.Post, "/author") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("apiKey", "APIKEY")
                setBody(author.toJson())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                response.content?.let {
                    val responseAuthor = Json.decodeFromString<AuthorDTO>(it)
                    assertEquals(author.name, responseAuthor.name)
                }
            }

            val newAuthor = AuthorDTO(id = id, name = "Test Updated")
            handleRequest(HttpMethod.Put, "/author") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("apiKey", "APIKEY")
                setBody(newAuthor.toJson())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                response.content?.let {
                    val responseAuthor = Json.decodeFromString<AuthorDTO>(it)
                    assertEquals(newAuthor.name, responseAuthor.name)
                }
            }

            handleRequest(HttpMethod.Delete, "/author/${author.id}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    assertEquals(HttpStatusCode.Accepted, response.status())
                    assertEquals("Author removed correctly", response.content)
                }
            }
        }
    }

    @Test
    fun `Error updating Author`() {
        withTestApplication({ setupTestApplication() }) {
            val newAuthor = AuthorDTO(id = "Test", name = "Test Updated")
            handleRequest(HttpMethod.Put, "/author") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("apiKey", "APIKEY")
                setBody(newAuthor.toJson())
            }.apply {
                assertEquals(HttpStatusCode.NotModified, response.status())
            }
        }
    }

    @Test
    fun `Get all Authors empty list`() {
        withTestApplication({ setupTestApplication() }) {
            handleRequest(HttpMethod.Get, "/author/all") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    val responseAuthor = Json.decodeFromString<List<AuthorDTO>>(it)
                    assertEquals(0, responseAuthor.size)
                }
            }
        }
    }

    @Test
    fun `Get all Authors list with one author`() {
        val author = AuthorDTO(id = UUID.randomUUID().toString(), name = "Test")
        withTestApplication({ setupTestApplication() }) {
            handleRequest(HttpMethod.Post, "/author") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("apiKey", "APIKEY")
                setBody(author.toJson())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/author/all") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    val responseAuthor = Json.decodeFromString<List<AuthorDTO>>(it)
                    assertEquals(1, responseAuthor.size)
                }
            }

            handleRequest(HttpMethod.Delete, "/author/${author.id}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    assertEquals(HttpStatusCode.Accepted, response.status())
                    assertEquals("Author removed correctly", response.content)
                }
            }
        }
    }

    @Test
    fun `Get Author by ID`() {
        val author = AuthorDTO(id = UUID.randomUUID().toString(), name = "Test")
        withTestApplication({ setupTestApplication() }) {
            handleRequest(HttpMethod.Post, "/author") {
                addHeader("apiKey", "APIKEY")
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(author.toJson())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/author/${author.id}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    val responseAuthor = Json.decodeFromString<AuthorDTO>(it)
                    assertEquals(author.id, responseAuthor.id)
                    assertEquals(author.name, responseAuthor.name)
                    assertEquals(author.picUrl, responseAuthor.picUrl)
                }
            }

            handleRequest(HttpMethod.Delete, "/author/${author.id}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    assertEquals(HttpStatusCode.Accepted, response.status())
                    assertEquals("Author removed correctly", response.content)
                }
            }
        }
    }

    @Test
    fun `Delete Author by ID`() {
        val author = AuthorDTO(id = UUID.randomUUID().toString(), name = "Test")
        withTestApplication({ setupTestApplication() }) {
            handleRequest(HttpMethod.Post, "/author") {
                addHeader("apiKey", "APIKEY")
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(author.toJson())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/author/${author.id}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    val responseAuthor = Json.decodeFromString<AuthorDTO>(it)
                    assertEquals(author.id, responseAuthor.id)
                    assertEquals(author.name, responseAuthor.name)
                    assertEquals(author.picUrl, responseAuthor.picUrl)
                }
            }

            handleRequest(HttpMethod.Delete, "/author/${author.id}") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                response.content?.let {
                    assertEquals(HttpStatusCode.Accepted, response.status())
                    assertEquals("Author removed correctly", response.content)
                }
            }
        }
    }

    @Test
    fun `Count authors`() {
        val authors = listOf(AuthorDTO(name = "Test"), AuthorDTO(name = "Test 2"), AuthorDTO(name = "Test 3"))
        withTestApplication({ setupTestApplication() }) {
            for (author in authors) {
                handleRequest(HttpMethod.Post, "/author") {
                    addHeader("apiKey", "APIKEY")
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(author.toJson())
                }.apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                }
            }

            handleRequest(HttpMethod.Get, "/author/count") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{\"count\":3}", response.content)
            }
            for (author in authors) {
                handleRequest(HttpMethod.Delete, "/author/${author.id}") {
                    addHeader("apiKey", "APIKEY")
                }.apply {
                    response.content?.let {
                        assertEquals(HttpStatusCode.Accepted, response.status())
                        assertEquals("Author removed correctly", response.content)
                    }
                }
            }
        }
    }

    @Test
    fun `Get paginated authors`() {
        val authors = listOf(AuthorDTO(name = "Test"), AuthorDTO(name = "Test 2"), AuthorDTO(name = "Test 3"))
        withTestApplication({ setupTestApplication() }) {
            for (author in authors) {
                handleRequest(HttpMethod.Post, "/author") {
                    addHeader("apiKey", "APIKEY")
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(author.toJson())
                }.apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                }
            }

            handleRequest(HttpMethod.Get, "/author?page=1&size=1") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                response.content?.let {
                    val responsePaginatedAuthor = Json.decodeFromString<PaginatedDTO<List<AuthorDTO>>>(it)
                    assertEquals(1, responsePaginatedAuthor.data.size)
                }
            }

            handleRequest(HttpMethod.Get, "/author?page=1&size=2") {
                addHeader("apiKey", "APIKEY")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                response.content?.let {
                    val responsePaginatedAuthor = Json.decodeFromString<PaginatedDTO<List<AuthorDTO>>>(it)
                    assertEquals(2, responsePaginatedAuthor.data.size)
                }
            }

            for (author in authors) {
                handleRequest(HttpMethod.Delete, "/author/${author.id}") {
                    addHeader("apiKey", "APIKEY")
                }.apply {
                    response.content?.let {
                        assertEquals(HttpStatusCode.Accepted, response.status())
                        assertEquals("Author removed correctly", response.content)
                    }
                }
            }
        }
    }
}
