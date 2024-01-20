package com.example.jemini

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jemini.ui.theme.JeminiTheme
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.BasicRichText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JeminiTheme {
                // A surface container using the 'background' color from the theme
                Base()

            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Base() {

    val generativeModel = GenerativeModel(
        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = "AIzaSyD4F1w3DpEMpDStCgFOpguDS-x4S_1ZJIE",

        safetySettings = listOf(SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE), SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE), SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE), SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE))
    )
    val chat = generativeModel.startChat(
        history = listOf(
        )
    )


    //var response = ""
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
        var text by remember { mutableStateOf("") }
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
                        item { }

                        items(mutableListt.size){it ->
                            //Text(text = responseList[it])

                            SelectionContainer {Text(text = "You", fontWeight = FontWeight.Bold)}
                            SelectionContainer { BasicRichText {Markdown(content = mutableListt.get(it))}}
                            SelectionContainer { Spacer(modifier = Modifier.height(10.dp)) }
                                if(it<responseList.size-1){
                                    SelectionContainer {Text(text = "Gemini AI", fontWeight = FontWeight.Bold)}

                                    SelectionContainer { BasicRichText {Markdown(content = responseList.get(it))}}
                                    SelectionContainer {Spacer(modifier = Modifier.height(15.dp))}
                                }


                            //Text(text = "Your AI", fontWeight = FontWeight.Bold)
                            //Text(text = responseList.get(it))
                            //Spacer(modifier = Modifier.height(15.dp))

                        }
                        if(responseList.size > 0){
                            item {
                                SelectionContainer {Text(text = "Gemini AI", fontWeight = FontWeight.Bold)}
                                SelectionContainer { BasicRichText {Markdown(content = responseList[responseList.size-1])}}
                            }

                        }


                    }
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End){
                        //val col = coor
                        // val containerColor = FilledTextFieldTokens.ContainerColor.toColor()
                        OutlinedTextField(modifier = Modifier
                            .width(310.dp)
                            .fillMaxHeight(), value = text, onValueChange = {
                            currentText.value = it
                            text = currentText.value
                        },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            colors = TextFieldDefaults.colors(focusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedTextColor = Color.Black))


                        IconButton(onClick = {
                            //responseList.add("You\n"+currentText.value + "\n")
                            mutableListt.add(currentText.value)
                            var prompt = currentText.value
                            text =  ""
                            currentText.value = ""
                            responseList.add("")
                            //CoroutineScope(Dispatchers.Main).launch {print("test "+ chat.sendMessage(prompt).text.toString())}
                            CoroutineScope(Dispatchers.Main).launch { chat.sendMessageStream(prompt).collect(){chunk->
                                responseList[responseList.size-1] = responseList[responseList.size-1] + chunk.text.toString()
                            } }
                            //CoroutineScope(Dispatchers.Main).launch {  generativeModel.generateContentStream(prompt).collect(){ chunk->
                            //    responseList[responseList.size-1] = responseList[responseList.size-1] + chunk.text.toString()
                            //}  }

                            //responseList.add(response.value)
                            //responseText.value = response
                            //responseList.add("Your AI\n"+responseText.value + "\n")


                            //println(prompt)
                            //println(response)
                            //responseText.value = response
                            //responseList.add(responseText.value)

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
fun JeminiPreview() {
    JeminiTheme {
        Base()
    }
}
    JeminiiTheme {
        Base()
    }
}
