package com.example.jeminii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeminii.ui.theme.JeminiiTheme
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import java.time.format.TextStyle
import kotlinx.coroutines.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JeminiiTheme {
                Base()

            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Base() {

    val generativeModel = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = "YOUR_API_KEY"
    )
    val response = remember {
        mutableStateOf("")
    }
    val responseList = remember {
        mutableStateListOf<String>()
    }
    val mutableListt = remember {
        mutableStateListOf<String>()
    }
    val currentText = remember {
        mutableStateOf("")
    }
    val responseText = remember {
        mutableStateOf("")
    }



    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        color = Color.White
    ) {
        var text by remember {mutableStateOf("")}
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(modifier = Modifier.fillMaxSize(), border = BorderStroke(1.dp, Color.Black), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier
                        .height(680.dp)
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp)){

                        items(mutableListt.size){it ->
                            Text(text = "You", fontWeight = FontWeight.Bold)
                            Text(text = mutableListt.get(it))
                            Spacer(modifier = Modifier.height(10.dp))
                            if(it<responseList.size-1){
                                Text(text = "Gemini AI", fontWeight = FontWeight.Bold)
                                Text(text = responseList.get(it))
                                Spacer(modifier = Modifier.height(15.dp))

                            }

                        }
                        if(responseList.size > 0){
                            item {
                                Text(text = "Gemini AI", fontWeight = FontWeight.Bold)
                                Text(text = responseList[responseList.size-1])
                            }

                        }


                    }
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End){
                        OutlinedTextField(modifier = Modifier
                            .width(310.dp)
                            .fillMaxHeight(), value = text, onValueChange = {
                                currentText.value = it
                                text = currentText.value
                            },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            colors = TextFieldDefaults.colors(focusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedTextColor = Color.Black))


                        IconButton(onClick = {
                            mutableListt.add(currentText.value)
                            var prompt = currentText.value
                            text =  ""
                            currentText.value = ""
                            responseList.add("")
                            CoroutineScope(Dispatchers.Main).launch {  generativeModel.generateContentStream(prompt).collect(){chunk->
                                responseList[responseList.size-1] = responseList[responseList.size-1] + chunk.text.toString()
                            }  }

                        }) {
                            Icon(imageVector = Icons.Rounded.Send, contentDescription = "send")

                        }
                    }

                }

            }

        }



    }




}


@Preview(showBackground = true)
@Composable
fun JeminiiPreview() {
    JeminiiTheme {
        Base()
    }
}
