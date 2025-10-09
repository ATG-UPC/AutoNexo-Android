package com.atg.autonexo.features.vehiclemaintenance.presentation.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.BottomNavBar

@Composable
fun ServiceOrderListScreen(
	onNavigateBack: () -> Unit,
	onNavigateToRegister: () -> Unit,
	onNavigate: (String) -> Unit,
	viewModel: ServiceOrderListViewModel = hiltViewModel()
) {
	val uiState = viewModel.uiState
	var inProgressExpanded by remember { mutableStateOf(true) }
	var doneExpanded by remember { mutableStateOf(true) }
	var canceledExpanded by remember { mutableStateOf(true) }

	Scaffold(
		bottomBar = {
			BottomNavBar(
				currentRoute = "service",
				onNavigate = onNavigate
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = onNavigateToRegister,
				containerColor = Color(0xFF5E7B97),
				contentColor = Color.White
			) {
				Text("Register Service Order")
			}
		},
		floatingActionButtonPosition = FabPosition.Center
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.background(Color(0xFFF5F5F5))
		) {
			// Header curvo
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
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back",
							tint = Color.White
						)
					}
					Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
						Text(
							text = "Service Order",
							style = MaterialTheme.typography.titleLarge.copy(
								fontWeight = FontWeight.Bold,
								color = Color.White
							)
						)
					}
				}
			}

			Spacer(modifier = Modifier.height(8.dp))

			SectionHeader(
				title = "In progress",
				count = uiState.inProgress.size,
				expanded = inProgressExpanded,
				onToggle = { inProgressExpanded = !inProgressExpanded }
			)
			if (inProgressExpanded) {
				OrderList(items = uiState.inProgress)
			}

			Spacer(modifier = Modifier.height(8.dp))

			SectionHeader(
				title = "Done",
				count = uiState.done.size,
				expanded = doneExpanded,
				onToggle = { doneExpanded = !doneExpanded }
			)
			if (doneExpanded) {
				OrderList(items = uiState.done)
			}

			Spacer(modifier = Modifier.height(8.dp))

			SectionHeader(
				title = "Canceled",
				count = uiState.canceled.size,
				expanded = canceledExpanded,
				onToggle = { canceledExpanded = !canceledExpanded }
			)
			if (canceledExpanded) {
				OrderList(items = uiState.canceled)
			}
		}
	}
}

@Composable
private fun SectionHeader(title: String, count: Int, expanded: Boolean, onToggle: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			Text(text = title, color = Color(0xFF8E8E8E), style = MaterialTheme.typography.bodyMedium)
			Spacer(modifier = Modifier.width(8.dp))
			Text(text = "$count", color = Color(0xFF282828))
		}
		TextButton(onClick = onToggle) {
			Text(text = if (expanded) "▾" else "▸", color = Color(0xFF8E8E8E))
		}
	}
}

@Composable
private fun OrderList(items: List<ServiceOrderUi>) {
	LazyColumn(
		contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		items(items) { order -> OrderCard(order) }
	}
}

@Composable
private fun OrderCard(order: ServiceOrderUi) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(text = order.customerName, fontWeight = FontWeight.SemiBold, color = Color(0xFF282828))
			Spacer(Modifier.height(6.dp))
			Text(text = order.title, color = Color(0xFF6B6B6B), style = MaterialTheme.typography.bodySmall)
			Spacer(Modifier.height(8.dp))
			Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
				Text(text = order.date, color = Color(0xFF8E8E8E), style = MaterialTheme.typography.labelSmall)
				Button(onClick = { /* Details */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3C47))) {
					Text("Details")
				}
			}
		}
	}
}

data class ServiceOrderUi(
	val id: String,
	val customerName: String,
	val title: String,
	val date: String
)

class ServiceOrderListViewModel : androidx.lifecycle.ViewModel() {
	val uiState = ServiceOrderListUi(
		inProgress = listOf(
			ServiceOrderUi("0001", "Sergio Iglesias", "Car Wash, Color Change", "12/10/2025 09:00")
		),
		done = listOf(
			ServiceOrderUi("0002", "Sergio Iglesias", "Tire Change, Color Change", "02/10/2025 13:00")
		),
		canceled = emptyList()
	)
}

data class ServiceOrderListUi(
	val inProgress: List<ServiceOrderUi> = emptyList(),
	val done: List<ServiceOrderUi> = emptyList(),
	val canceled: List<ServiceOrderUi> = emptyList()
)


