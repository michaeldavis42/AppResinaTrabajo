package com.example.appresina

import com.example.appresina.data.AuthRepository
import com.example.appresina.data.SessionManager
import com.example.appresina.model.Usuario
import com.example.appresina.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val isLoggedInFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { sessionManager.isLoggedIn } returns isLoggedInFlow
        viewModel = AuthViewModel(authRepository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login con credenciales correctas actualiza el estado de autenticacion`() = runTest {
        val usuario = Usuario(id = 1, nombre = "test", email = "test@test.com", password_hash = "")
        coEvery { authRepository.login("test@test.com", "password") } coAnswers {
            isLoggedInFlow.value = true
            Result.success(usuario)
        }

        viewModel.actualizarEmailLogin("test@test.com")
        viewModel.actualizarContrasenaLogin("password")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.isAuthenticated.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `login con credenciales incorrectas actualiza el mensaje de error`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception("Credenciales incorrectas"))

        viewModel.actualizarEmailLogin("test@test.com")
        viewModel.actualizarContrasenaLogin("wrong_password")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isAuthenticated.value)
        assertNotNull(viewModel.errorMessage.value)
        assertEquals("Credenciales incorrectas", viewModel.errorMessage.value)
    }

}
