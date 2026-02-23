package com.example.settingsa3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.settingsa3.ui.theme.SettingsA3Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsA3Theme {
                SettingApply()
            }
        }
    }
}

// to be used in preview. my emulator stopped working
@Composable
fun SettingApply() {
    // set up the snackbar
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // main settings interface
            Settings(
                modifier = Modifier.padding(innerPadding)
            )
            // apply button that shows a snackbar confirmation message
            IconButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Settings applied!"
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = "apply icon",
                )
            }
        }
    }
}

@Composable
fun Settings(modifier: Modifier = Modifier) {
    // saves all of the options' state in settings component for now
    var volume by rememberSaveable { mutableStateOf(0f) }
    var micVolume by rememberSaveable { mutableStateOf(0f) }

    var notifications by rememberSaveable { mutableStateOf(false) }
    var subscribed by rememberSaveable { mutableStateOf(false) }
    val notifBy = rememberSaveable { mutableStateListOf(false, false, false) }
    val notifWays = listOf("Email", "SMS", "App")

    val languages = listOf("English", "Spanish", "中文")  // provides list of language options
    val (language, onLanguageChange) = rememberSaveable { mutableStateOf(languages[0]) }

    // encase all setting options in a column
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // tab title
        Text(
            text = "Settings",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(all = 8.dp)
        )
        // volume slider
        SliderRow(
            label = "Volume",
            support = "Adjust app volume",
            value = volume,
            onChange = { newVal -> volume = newVal }
        )
        // mic volume slider
        SliderRow(
            label = "Mic Volume",
            support = "Adjust input volume",
            value = micVolume,
            onChange = { newVal -> micVolume = newVal }
        )
        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
        // notification switch
        SwitchRow(
            label = "Notifications",
            support = "Toggle app notifications",
            value = notifications,
            onChange = { newVal -> notifications = newVal }
        )
        // subscription switch
        SwitchRow(
            label = "Subscription",
            support = "Receive promos from app",
            value = subscribed,
            onChange = { newVal -> subscribed = newVal }
        )
        // notification methods
        CheckboxRow(
            label = "Notification Permissions",
            support = "Permission to send notifications on:",
            value = notifBy,
            options = notifWays,
            onChange = { index, checked -> notifBy[index] = checked }
        )
        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
        // language options. they dont really function at the moment
        RadioButtonsRow(
            label = "Language",
            support = "Select app language",
            value = language,
            options = languages,
            onClick = onLanguageChange
        )
    }
}

// provides the label and supporting text for a row
@Composable
fun Label(label: String, support: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            text = label
        )
        Text(
            fontSize = 12.sp,
            lineHeight = 12.sp,
            text = support
        )
    }
}

// slider option row composable
@Composable
fun SliderRow(label: String, support: String, value: Float, onChange: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Label(
            label = label,
            support = support,
            modifier = Modifier.weight(1f)
        )
        Slider(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.sizeIn(minWidth = 100.dp, maxWidth = 200.dp)
        )
    }
}

// switch option row composable
@Composable
fun SwitchRow(label: String, support: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onChange(!value) },    // make label clickable for ease
        verticalAlignment = Alignment.CenterVertically
    ) {
        Label(
            label = label,
            support = support,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = onChange
        )
    }
}

// radio buttons option 'row' (column) composable
@Composable
fun RadioButtonsRow(
    label: String,
    support: String,
    value: String,
    options: List<String>,
    onClick: (String) -> Unit)
{
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Label(
            label = label,
            support = support
        )
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEach { option -> // for each option in languages, create a radio button row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == value),
                            onClick = { onClick(option) },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (option == value),
                        onClick = null
                    )
                    Text(
                        text = option
                    )
                }
            }
        }
    }
}

// checkbox option row composable
@Composable
fun CheckboxRow(
    label: String,
    support: String,
    value: List<Boolean>,
    options: List<String>,
    onChange: (Int, Boolean) -> Unit
) {
    Column() {
        Label(
            label = label,
            support = support,
            modifier = Modifier.clickable { // make label clickable to reset all options
                value.forEachIndexed { index, _ ->
                    onChange(index, false)
                }
            }
        )
        value.forEachIndexed { index, checked ->    // for each option, create a checkbox row
            Row(
                modifier = Modifier.padding(all = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = options[index])
                Checkbox(
                    checked = checked,
                    onCheckedChange = { isChecked -> onChange(index, isChecked) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsA3Theme {
        SettingApply()
    }
}