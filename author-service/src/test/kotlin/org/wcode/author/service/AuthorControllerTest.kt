package org.wcode.author.service

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.wcode.author.service.domain.AuthorRepository
import org.wcode.author.service.models.Author
import org.wcode.author.service.models.GenericResponse
import javax.inject.Inject

@MicronautTest
class AuthorControllerTest {

    @Inject
    @field:Client("/")
    private lateinit var client: HttpClient

    @Test
    fun `Add Author without ID`() {
        val author = Author(name = "Test")
        val request: HttpRequest<Author> = HttpRequest.POST("/author", author)
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)

        val deleteAuthorRequest = HttpRequest.DELETE<Boolean>("/author/${author.id}")
        val bodyDelete = client.toBlocking().retrieve(deleteAuthorRequest, GenericResponse::class.java)
        assertEquals(true, bodyDelete.success)
    }

    @Test
    fun `Add Author with ID`() {
        val author = Author(id = "Test", name = "Test")
        val request: HttpRequest<Author> = HttpRequest.POST("/author", author)
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)

        val deleteAuthorRequest = HttpRequest.DELETE<Boolean>("/author/${author.id}")
        val bodyDelete = client.toBlocking().retrieve(deleteAuthorRequest, GenericResponse::class.java)
        assertEquals(true, bodyDelete.success)
    }

    @Test
    fun `List all Authors`() {
        val authors = listOf(Author(name = "Test"), Author(name = "Test2"))

        authors.forEach { author ->
            val request: HttpRequest<Author> = HttpRequest.POST("/author", author)
            val body = client.toBlocking().retrieve(request, Argument.of(Author::class.java))
            assertNotNull(body)
        }

        val request3: HttpRequest<Void> = HttpRequest.GET("/author/all")
        val body3: List<Author> = client.toBlocking().retrieve(request3, Argument.listOf(Author::class.java))
        assertNotNull(body3)
        assertEquals(2, body3.size)

        authors.forEach { author ->
            val deleteAuthorRequest = HttpRequest.DELETE<Boolean>("/author/${author.id}")
            val bodyDelete = client.toBlocking().retrieve(deleteAuthorRequest, GenericResponse::class.java)
            assertEquals(true, bodyDelete.success)
        }
    }

    @Test
    fun `Get Author By ID`() {
        val author = Author(name = "Test")
        val request: HttpRequest<Author> = HttpRequest.POST("/author", author)
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)

        val requestGet: HttpRequest<Any> = HttpRequest.GET("/author/${author.id}")
        val bodyGet = client.toBlocking().retrieve(requestGet, Argument.of(Author::class.java))
        assertNotNull(bodyGet)
        assertEquals(author.name, bodyGet.name)

        val deleteAuthorRequest = HttpRequest.DELETE<Boolean>("/author/${author.id}")
        val bodyDelete = client.toBlocking().retrieve(deleteAuthorRequest, GenericResponse::class.java)
        assertEquals(true, bodyDelete.success)
    }

    @Test
    fun `Get Author that doesn't exist`() {
        val author = Author(name = "Test")
        val requestGet: HttpRequest<Any> = HttpRequest.GET("/author/${author.id}")
        try {
            client.toBlocking().retrieve(requestGet, Author::class.java)
            assert(false)
        } catch (e: HttpClientResponseException) {
            assert(true)
        }
    }

    @Test
    fun `Delete Author that doesn't exist`() {
        val author = Author(name = "Test")
        val deleteAuthorRequest = HttpRequest.DELETE<Boolean>("/author/${author.id}")
        val bodyDelete = client.toBlocking().retrieve(deleteAuthorRequest, GenericResponse::class.java)
        assertEquals(false, bodyDelete.success)
    }

    @Test
    fun `Update Author`() {
        val author = Author(id = "Test", name = "Test")
        val request: HttpRequest<Author> = HttpRequest.POST("/author", author)
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)

        val newAuthor = author.copy(name = "Test Update")
        val requestUpdate: HttpRequest<Author> = HttpRequest.PUT("/author", newAuthor)
        val bodyUpdate = client.toBlocking().retrieve(requestUpdate, Author::class.java)
        assertNotNull(body)
        assertEquals(newAuthor.name, bodyUpdate.name)

        val deleteAuthorRequest = HttpRequest.DELETE<Boolean>("/author/${author.id}")
        val bodyDelete = client.toBlocking().retrieve(deleteAuthorRequest, GenericResponse::class.java)
        assertEquals(true, bodyDelete.success)
    }

    @Test
    fun `Update Author that doesn't exist`() {
        val author = Author(id = "Test", name = "Test")
        val requestUpdate: HttpRequest<Author> = HttpRequest.PUT("/author", author)
        try {
            client.toBlocking().retrieve(requestUpdate, Author::class.java)
            assert(false)
        } catch (e: HttpClientResponseException) {
            assert(true)
        }
    }
}
