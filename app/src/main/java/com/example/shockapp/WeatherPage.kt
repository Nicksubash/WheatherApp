package com.example.shockapp

import android.support.v4.os.IResultReceiver.Default
import android.widget.ImageView
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontLoadingStrategy.Companion.Async
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.Coil
import coil.ImageLoader
import com.bumptech.glide.Glide
import com.example.shockapp.API.NetworkResponse
import com.example.shockapp.API.WeatherModel
import com.squareup.picasso.Picasso


@Composable
fun WeatherPage(modifier: Modifier,viewModel: WeatherViewModel) {

    val weatherResult=viewModel.weatherResult.observeAsState()


    var city by remember {
        mutableStateOf("")
    }
    Column (
        modifier= Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(text = "Weather app", fontSize = 20.sp)
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            OutlinedTextField(
                modifier = Modifier.weight(2f),
                value = city,
                onValueChange ={
                    city =it
                }, label = {
                    Text(text = "Location to search")
                } )
            IconButton(onClick = {
                viewModel.getData(city)
            }){
                Icon(imageVector = Icons.Default.Search,
                    contentDescription ="Search" )

            }
        }

        when(val result =weatherResult.value){
            is NetworkResponse.Failed -> {
                Text(text = "City not found")
            }
            NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }
            is NetworkResponse.Success -> {
                weatherDetail(data=result.data)
//                Text(text = "succeed")
            }
            null -> {}
        }
    }
}

@Composable
fun weatherDetail(data:WeatherModel){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment =Alignment.CenterHorizontally
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(40.dp),
            )

            Text(text = data.location.name, fontSize = 35.sp)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = data.location.country, fontSize = 25.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "${data.current.temp_c} °C", fontSize = 55.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Time")
        Text(text = data.location.localtime, fontSize = 25.sp)

    }


}

