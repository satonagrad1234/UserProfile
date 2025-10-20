package com.example.usersurvey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.usersurvey.ui.theme.UserSurveyTheme
import kotlinx.coroutines.launch

enum class Gender { MALE, FEMALE }

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserSurveyTheme {
                Surface { SurveyScreen() }
            }
        }
    }
}

@Composable
fun SurveyScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf(25f) } // можно заменить на mutableFloatStateOf(25f), если есть
    var gender by rememberSaveable { mutableStateOf(Gender.MALE) }
    var subscribed by rememberSaveable { mutableStateOf(false) }

    val nameIsBlank = name.trim().isEmpty()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name_label)) },
                placeholder = { Text(stringResource(R.string.name_placeholder)) },
                singleLine = true,
                isError = nameIsBlank,
                supportingText = {
                    if (nameIsBlank) Text(text = stringResource(R.string.error_name_required))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.age_title, age.toInt()),
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = age,
                onValueChange = { age = it },
                valueRange = 1f..100f,
                steps = 98,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.gender_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioOption(
                    selected = gender == Gender.MALE,
                    onClick = { gender = Gender.MALE },
                    label = stringResource(R.string.gender_male)
                )
                Spacer(Modifier.width(16.dp))
                RadioOption(
                    selected = gender == Gender.FEMALE,
                    onClick = { gender = Gender.FEMALE },
                    label = stringResource(R.string.gender_female)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = subscribed,
                    onCheckedChange = { subscribed = it }
                )
                Text(text = stringResource(R.string.subscription_label))
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val genderText = when (gender) {
                        Gender.MALE -> context.getString(R.string.gender_male_full)
                        Gender.FEMALE -> context.getString(R.string.gender_female_full)
                    }
                    val subscribedText = if (subscribed)
                        context.getString(R.string.yes)
                    else
                        context.getString(R.string.no)

                    val summary = context.getString(
                        R.string.summary_template,
                        name.trim(),
                        age.toInt(),
                        genderText,
                        subscribedText
                    )
                    scope.launch { snackbarHostState.showSnackbar(summary) }
                },
                enabled = !nameIsBlank,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.submit))
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.summary_hint),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun RadioOption(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label)
    }
}

@Preview(showBackground = true)
@Composable
fun SurveyPreviewLight() {
    UserSurveyTheme(darkTheme = false) { Surface { SurveyScreen() } }
}

@Preview(showBackground = true)
@Composable
fun SurveyPreviewDark() {
    UserSurveyTheme(darkTheme = true) { Surface { SurveyScreen() } }
}
