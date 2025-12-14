package com.example.appresina

import com.example.appresina.data.AuthRepository
import com.example.appresina.data.SessionManager
import com.example.appresina.data.UsuarioRepository
import com.example.appresina.model.Usuario
import com.example.appresina.viewmodel.UserProfileViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

@ExperimentalCoroutinesApi
class UserProfileViewModelTest {

    private lateinit var viewModel: UserProfileViewModel
    private val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { sessionManager.isLoggedIn } returns flowOf(true)
        viewModel = UserProfileViewModel(usuarioRepository, authRepository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarUsuarioActual carga los datos del usuario en el viewModel`() = runTest {
        val usuario = Usuario(1, "test", "test@test.com", "", 0L, "avatar", "bio", 0L, false)
        coEvery { authRepository.getCurrentUser() } returns usuario

        viewModel.cargarUsuarioActual()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("test", viewModel.nombre.value)
        assertEquals("bio", viewModel.biografia.value)
        assertEquals("avatar", viewModel.avatarUrl.value)
    }

    @Test
    fun `actualizarPerfil actualiza los datos del usuario`() = runTest {
        val usuario = Usuario(1, "test", "test@test.com", "", 0L, "avatar", "bio", 0L, false)
        coEvery { authRepository.getCurrentUser() } returns usuario

        viewModel.actualizarNombre("nuevo nombre")
        viewModel.actualizarBiografia("nueva bio")
        viewModel.actualizarAvatarUrl("nuevo avatar")

        viewModel.actualizarPerfil()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Perfil actualizado con éxito", viewModel.successMessage.value)
    }
}
