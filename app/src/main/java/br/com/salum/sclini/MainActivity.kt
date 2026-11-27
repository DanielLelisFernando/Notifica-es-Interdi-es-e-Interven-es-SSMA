package br.com.salum.sclini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF123B67)
private val Blue = Color(0xFF1769AA)
private val Cyan = Color(0xFF20B8CC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SclIniApp() }
    }
}

@Composable
private fun SclIniApp() {
    var page by remember { mutableStateOf("home") }
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, secondary = Cyan, background = Color(0xFFF5F7FA))) {
        Scaffold(containerColor = Color(0xFFF5F7FA), topBar = {
            if (page != "home") SimpleTopBar(pageTitle(page)) { page = "home" }
        }) { padding ->
            when (page) {
                "home" -> HomeScreen(Modifier.padding(padding)) { page = it }
                "new" -> ModuleScreen(Modifier.padding(padding), "Novo registro", "Escolha o tipo de registro.", listOf("Interdição", "Notificação", "Intervenção"))
                "active" -> ModuleScreen(Modifier.padding(padding), "Interdições ativas", "0 aguardando liberação formal. Os registros serão listados após implementação da persistência local.", emptyList())
                "records" -> ModuleScreen(Modifier.padding(padding), "Consultar registros", "Consulta geral dos registros armazenados no dispositivo.", emptyList())
                "actions" -> ModuleScreen(Modifier.padding(padding), "Acompanhamento de ações", "Responsáveis, prazos, situações e evidências.", emptyList())
                "indicators" -> ModuleScreen(Modifier.padding(padding), "Indicadores", "Indicadores calculados a partir dos dados locais.", emptyList())
                else -> ModuleScreen(Modifier.padding(padding), "Instruções de uso", "SCL - INI: sistema offline para registros de SSMA. Consulte o procedimento corporativo aplicável para autorizações e liberações.", emptyList())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(title = { Text(title, fontWeight = FontWeight.SemiBold) }, navigationIcon = {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") }
    })
}

@Composable
private fun HomeScreen(modifier: Modifier, onNavigate: (String) -> Unit) {
    Column(modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(18.dp))
        Box(Modifier.size(76.dp).background(Navy, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.HealthAndSafety, contentDescription = "Segurança", tint = Color.White, modifier = Modifier.size(46.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text("SALUM CONSTRUÇÕES", color = Navy, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text("SCL - INI", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text("Interdição • Notificação • Intervenção", color = Color(0xFF65758A), fontSize = 13.sp)
        Spacer(Modifier.height(22.dp))
        HomeButton("Novo registro", "Emitir notificação, interdição ou intervenção", Icons.Default.AddCircle, Blue) { onNavigate("new") }
        HomeButton("Interdições ativas", "0 aguardando liberação formal", Icons.Default.Block, Color(0xFFB54735)) { onNavigate("active") }
        HomeButton("Consultar registros", "Pesquisar e consultar documentos", Icons.Default.Search, Navy) { onNavigate("records") }
        HomeButton("Acompanhamento de ações", "Responsáveis, prazos e tratativas", Icons.Default.TaskAlt, Blue) { onNavigate("actions") }
        HomeButton("Indicadores", "Visão gerencial dos registros locais", Icons.Default.BarChart, Blue) { onNavigate("indicators") }
        HomeButton("Instruções de uso", "Orientações para utilização em campo", Icons.Default.Info, Navy) { onNavigate("help") }
        Spacer(Modifier.weight(1f))
        Text("Versão 1.0.0", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
    }
}

@Composable
private fun HomeButton(title: String, subtitle: String, icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(29.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = Navy)
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF667085))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
private fun ModuleScreen(modifier: Modifier, title: String, description: String, options: List<String>) {
    LazyColumn(modifier.fillMaxSize().padding(20.dp)) {
        item {
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy)
            Spacer(Modifier.height(8.dp))
            Text(description, color = Color(0xFF667085))
            Spacer(Modifier.height(18.dp))
        }
        items(options) { option ->
            Card(Modifier.fillMaxWidth().padding(vertical = 5.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(option, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
        if (options.isEmpty()) item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Text("Módulo reservado para implementação das regras de negócio e persistência offline.", Modifier.padding(16.dp), color = Color(0xFF667085))
            }
        }
    }
}

private fun pageTitle(page: String) = when(page) {
    "new" -> "Novo registro"; "active" -> "Interdições ativas"; "records" -> "Consultar registros"
    "actions" -> "Ações"; "indicators" -> "Indicadores"; else -> "Instruções"
}
