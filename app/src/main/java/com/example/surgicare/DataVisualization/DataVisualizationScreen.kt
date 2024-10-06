package com.example.surgicare.DataVisualization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.surgicare.vitals.Vitals
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DataVisualizationScreen(viewModel: DataVisualizationViewModel, userId: String) {
    val vitalsHistory by viewModel.vitalsHistory.collectAsState()
    val vitalsAverages by viewModel.vitalsAverages.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchVitals(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Vitals Analysis", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (vitalsHistory.isEmpty()) {
            Text(
                text = "No data available",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Heart Rate Chart
            VitalsBarChart(
                vitalsData = vitalsHistory,
                title = "Heart Rate Over Last 7 Days",
                valueSelector = { it.heartRate!!.toFloat() },
                maxValue = 200f,
                unit = "bpm"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SpO2 Chart
            VitalsBarChart(
                vitalsData = vitalsHistory,
                title = "SpO2 Over Last 7 Days",
                valueSelector = { it.spO2!!.toFloat() },
                maxValue = 100f,
                unit = "%"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature Chart
            VitalsBarChart(
                vitalsData = vitalsHistory,
                title = "Temperature Over Last 7 Days",
                valueSelector = { it.temperature!! },
                maxValue = 110f, // Adjust as necessary
                unit = "°F"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Respiratory Rate Chart
            VitalsBarChart(
                vitalsData = vitalsHistory,
                title = "Respiratory Rate Over Last 7 Days",
                valueSelector = { it.respiratoryRate!!.toFloat() },
                maxValue = 40f,
                unit = "breaths/min"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Averages
            val maxValueMap = mapOf(
                "Heart Rate" to 200f,
                "SpO2" to 100f,
                "Temperature" to 110f,
                "Respiratory Rate" to 40f
            )

            val unitMap = mapOf(
                "Heart Rate" to "bpm",
                "SpO2" to "%",
                "Temperature" to "°F",
                "Respiratory Rate" to "breaths/min"
            )

            VitalsAverageBarChart(
                vitalsAverages = vitalsAverages,
                title = "Average Vitals",
                maxValueMap = maxValueMap,
                unitMap = unitMap
            )
        }
    }
}




@Composable
fun VitalsBarChart(
    vitalsData: List<Vitals>,
    title: String,
    valueSelector: (Vitals) -> Float,
    maxValue: Float,
    unit: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // Set a fixed height for the chart
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            vitalsData.forEach { vitals ->
                val value = valueSelector(vitals)
                val barHeight = (value / maxValue) * 200.dp

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .height(barHeight)
                            .width(20.dp)
                            .background(Color.Blue)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDate(vitals.timestamp?.toDate()?.time ?: 0),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${value.toInt()} $unit",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


@Composable
fun VitalsAverageBarChart(
    vitalsAverages: Map<String, Float>,
    title: String,
    maxValueMap: Map<String, Float>,
    unitMap: Map<String, String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            vitalsAverages.forEach { (vitalName, avgValue) ->
                val maxValue = maxValueMap[vitalName] ?: 100f
                val unit = unitMap[vitalName] ?: ""
                val barHeight = (avgValue / maxValue) * 200.dp

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .height(barHeight)
                            .width(40.dp)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = vitalName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(60.dp),
                        maxLines = 1
                    )
                    Text(
                        text = "${avgValue.toInt()} $unit",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
