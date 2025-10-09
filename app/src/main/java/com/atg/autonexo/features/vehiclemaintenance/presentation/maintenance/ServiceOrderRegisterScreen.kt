package com.atg.autonexo.features.vehiclemaintenance.presentation.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atg.autonexo.core.ui.components.BottomArcShape

@Composable
fun ServiceOrderRegisterScreen(
	onNavigateBack: () -> Unit,
	onSubmit: () -> Unit,
	viewModel: ServiceOrderRegisterViewModel = viewModel()
) {
	val uiState by viewModel.uiState
	Scaffold { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.background(Color(0xFFF5F5F5))
		) {
			// Header
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(120.dp)
					.background(
						brush = Brush.verticalGradient(listOf(Color(0xFF202D36), Color(0xFF2E3C47))),
						shape = BottomArcShape(64.dp)
					)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 16.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					IconButton(onClick = onNavigateBack) {
					Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
					}
					Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
						Text(
							text = "Service Order",
							style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
						)
					}
				}
			}

			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(horizontal = 16.dp)
			) {
				Spacer(Modifier.height(16.dp))
				Text("Select Offer", color = Color(0xFF6B6B6B), style = MaterialTheme.typography.labelLarge)
				Spacer(Modifier.height(8.dp))
				OutlinedTextField(
					value = uiState.selectedOffer,
					onValueChange = { text -> viewModel.updateOffer(text) },
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text("Choose an offer with an appointment realized") },
					shape = RoundedCornerShape(12.dp)
				)

				Spacer(Modifier.height(16.dp))
				Text("Deadline Date", color = Color(0xFF6B6B6B), style = MaterialTheme.typography.labelLarge)
				Spacer(Modifier.height(8.dp))
				OutlinedTextField(
					value = uiState.deadlineDate,
					onValueChange = { text -> viewModel.updateDeadlineDate(text) },
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text("When you will finished the maintenance") },
					shape = RoundedCornerShape(12.dp)
				)

				Spacer(Modifier.height(16.dp))
				Text("Deadline Time", color = Color(0xFF6B6B6B), style = MaterialTheme.typography.labelLarge)
				Spacer(Modifier.height(8.dp))
				OutlinedTextField(
					value = uiState.deadlineTime,
					onValueChange = { text -> viewModel.updateDeadlineTime(text) },
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text("The hour you will finished the maintenance") },
					shape = RoundedCornerShape(12.dp)
				)

				Spacer(Modifier.height(16.dp))
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text("Tasks:", color = Color(0xFF6B6B6B), style = MaterialTheme.typography.labelLarge)
					Spacer(Modifier.width(8.dp))
					AssistChip(onClick = { viewModel.addTaskField() }, label = { Text("Add new task") }, leadingIcon = {
						Icon(Icons.Default.Add, contentDescription = null)
					})
				}
				Spacer(Modifier.height(8.dp))

				uiState.tasks.forEachIndexed { index, value ->
					Row(verticalAlignment = Alignment.CenterVertically) {
						OutlinedTextField(
							value = value,
							onValueChange = { text -> viewModel.updateTask(index, text) },
							modifier = Modifier
								.fillMaxWidth()
								.weight(1f),
							placeholder = { Text("Add a new task") },
							shape = RoundedCornerShape(12.dp)
						)
						Spacer(Modifier.width(8.dp))
						Checkbox(checked = false, onCheckedChange = { })
					}
					Spacer(Modifier.height(8.dp))
				}

				Spacer(Modifier.height(12.dp))
				Button(
					onClick = {
						viewModel.submit()
						onSubmit()
					},
					modifier = Modifier
						.fillMaxWidth()
						.height(52.dp),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E7B97))
				) {
					Text("Register")
				}

				Spacer(Modifier.height(24.dp))
			}
		}
	}
}

class ServiceOrderRegisterViewModel : androidx.lifecycle.ViewModel() {
	private val _uiState = mutableStateOf(ServiceOrderRegisterUi())
	val uiState: State<ServiceOrderRegisterUi> = _uiState

	fun updateOffer(value: String) { _uiState.value = _uiState.value.copy(selectedOffer = value) }
	fun updateDeadlineDate(value: String) { _uiState.value = _uiState.value.copy(deadlineDate = value) }
	fun updateDeadlineTime(value: String) { _uiState.value = _uiState.value.copy(deadlineTime = value) }
	fun addTaskField() { _uiState.value = _uiState.value.copy(tasks = _uiState.value.tasks + "") }
	fun updateTask(index: Int, value: String) {
		_uiState.value = _uiState.value.copy(tasks = _uiState.value.tasks.toMutableList().also { it[index] = value })
	}
	fun submit() { /* TODO: Integrar con repositorio */ }
}

data class ServiceOrderRegisterUi(
	val selectedOffer: String = "",
	val deadlineDate: String = "",
	val deadlineTime: String = "",
	val tasks: List<String> = listOf("", "")
)


