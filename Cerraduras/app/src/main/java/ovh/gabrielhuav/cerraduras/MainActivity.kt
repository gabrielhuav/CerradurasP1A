package ovh.gabrielhuav.cerraduras

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.call.* // Importación correcta para usar body()
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.http.*
import ovh.gabrielhuav.cerraduras.ui.theme.CerradurasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CerradurasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    InputScreen()
                }
            }
        }
    }
}

@Composable
fun InputScreen() {
    var inputN by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("Esperando respuesta...") }

    // Configurar el cliente Ktor
    val client = HttpClient(Android)

    // Función para realizar la llamada HTTP
    fun fetchCerradura(n: String) {
        result = "Realizando solicitud..."  // Mostrar estado en la UI
        Log.d("InputScreen", "fetchCerradura: Realizando solicitud con n = $n")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llamar al endpoint que devuelve HTML
                val url = "http://10.0.2.2:8080/cerradura?n=$n"
                Log.d("InputScreen", "fetchCerradura: URL = $url")

                val response: HttpResponse = client.get(url)
                val htmlContent: String = response.body() // Extraer el cuerpo de la respuesta como String
                result = htmlContent // Mostrar el contenido HTML en la UI
                Log.d("InputScreen", "fetchCerradura: Respuesta HTML = $htmlContent")
            } catch (e: Exception) {
                result = "Error: ${e.message}"
                Log.e("InputScreen", "Error en fetchCerradura", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = inputN,
            onValueChange = { inputN = it },
            label = { Text("Ingrese el valor de n") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (inputN.isNotEmpty()) {
                    fetchCerradura(inputN)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Obtener Cerradura")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el contenido HTML en la interfaz
        Text(text = result)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CerradurasTheme {
        InputScreen()
    }
}
