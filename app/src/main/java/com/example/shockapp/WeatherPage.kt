package com.example.shockapp

import android.media.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import com.example.shockapp.API.NetworkResponse

@Composable
fun WeatherPage(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    val context = LocalContext.current
    val weatherResult = viewModel.weatherResult.observeAsState()
    var city by remember { mutableStateOf("") }

    val searchHistoryList by viewModel.searchHistory.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { // Use 'item' to add a single non-repeating item
            Text(text = "Weather App", fontSize = 30.sp)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = city,
                    onValueChange = { city = it },
                    label = { Text(text = stringResource(R.string.location_search_hint)) }
                )

            }
        }

        item {
            Button(onClick = {
                if (city.isNotEmpty()) {
                    viewModel.getData(city)
                }
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                Text(text = "Search")
            }
        }

        item {
            // Only request location data if no city is provided.
            if (city.isEmpty()) {
                RequestLocationPermission {
                    // Use LaunchedEffect to run the location fetch side-effect once.
                    LaunchedEffect(Unit) {
                        val location = getCurrentLocation(context)
                        location?.let {
                            viewModel.getDataByCoordinates(it.latitude, it.longitude)
                        }
                    }
                }
            }
        }

        item {
            when (val result = weatherResult.value) {
                is NetworkResponse.Failed -> {
                    Text(text = "City not found")
                }
                NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }
                is NetworkResponse.Success -> {
                    WeatherDetail(data = result.data)
                }
                null -> {
                    Text(text = "WELCOME To Weather App")
                }
            }
        }

        //History section
        if(searchHistoryList.isNotEmpty()){
            item {
                Spacer(modifier=Modifier.height(16.dp))
                Text(text = "History ")
            }

            items(
                items = searchHistoryList,
                key = { history -> history.timestamp } // Use a unique and stable key!
            ) { history ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${history.query} (at ${java.text.DateFormat.getTimeInstance().format(history.timestamp)})",
                        modifier = Modifier.padding(14.dp)
                    )
                    IconButton(onClick = { viewModel.deleteHistoryItem(history) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        } else {
            item {
                Text("No search history yet.")  // Display a message when the list is empty
            }
        }

    }
}
