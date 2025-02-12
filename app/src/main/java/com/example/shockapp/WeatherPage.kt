package com.example.shockapp

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shockapp.API.NetworkResponse
import com.example.shockapp.API.WeatherModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    val context = LocalContext.current
    val weatherResult = viewModel.weatherResult.observeAsState()
    var city by remember { mutableStateOf("") }

    val searchHistoryList by viewModel.searchHistory.collectAsState(initial = emptyList())

    // State to hold the video URI
    var backgroundVideoUri by remember { mutableStateOf<Uri?>(null) }

    // Update backgroundVideoUri based on weather conditions
    LaunchedEffect(weatherResult.value) {
        backgroundVideoUri = when (val result = weatherResult.value) {
            is NetworkResponse.Success -> {
                when (result.data.current.condition.text.lowercase()) {
                    "cloudy", "partly cloudy" -> Uri.parse("android.resource://${context.packageName}/${R.raw.cloudy}")
                    "rainy", "light rain", "moderate rain","light rain shower","light sleet" -> Uri.parse("android.resource://${context.packageName}/${R.raw.rainy}")
                    "sunny", "clear", "overcast" -> Uri.parse("android.resource://${context.packageName}/${R.raw.sunny}")
                    "thunder" -> Uri.parse("android.resource://${context.packageName}/${R.raw.thunderstrom}")
                    "snow" , "heavy snow" -> Uri.parse("android.resource://${context.packageName}/${R.raw.snow}")
                    else -> null
                }
            }
            else -> null
        }
    }

    // Use a Box to layer the video and the content
    Box(modifier = Modifier.fillMaxSize()) {
        // Video background
        if (backgroundVideoUri != null) {
            VideoBackground(uri = backgroundVideoUri!!)
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
        }

        // Content Column
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "Weather App", fontSize = 30.sp, color = Color.White)
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
                        label = { Text(text = stringResource(R.string.location_search_hint), color = Color.White) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,       // Border color when focused
                            unfocusedBorderColor = Color.LightGray,   // Border color when not focused
                        )

                    )

                }
            }

            item {
                Button(onClick = {
                    if (city.isNotEmpty()) {
                        viewModel.getData(city)
                    }
                }, colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White, containerColor = Color.White
                )) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                    Text(text = "Search", color = Color.Black)
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
                        Text(text = "City not found", color = Color.White)
                    }
                    NetworkResponse.Loading -> {
                        CircularProgressIndicator()
                    }
                    is NetworkResponse.Success -> {
                        WeatherDetail(data = result.data)
                    }
                    null -> {
                        Text(text = "WELCOME To Weather App", color = Color.White)
                    }
                }
            }

            //History section
            if(searchHistoryList.isNotEmpty()){
                item {
                    Spacer(modifier=Modifier.height(16.dp))
                    Text(text = "History", color = Color.White)
                }
                items(
                    items = searchHistoryList,
                    key = { history -> history.timestamp }
                ) { history ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(
                            text = "${history.query} (at ${java.text.DateFormat.getTimeInstance().format(history.timestamp)})",
                            modifier = Modifier.padding(8.dp)
                                .width(250.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(20.dp))

                        IconButton(onClick = { viewModel.deleteHistoryItem(history) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                        }
                    }
                }
            } else {
                item {
                    Text("No search history yet.", color = Color.White)  // Display a message when the list is empty
                }
            }

        }
    }
}