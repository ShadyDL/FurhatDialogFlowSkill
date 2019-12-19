package furhatos.app.apg.flow

import furhatos.app.apg.nlu.JaIntent
import furhatos.app.apg.nlu.NeeIntent
import furhatos.app.apg.nlu.StopIntent
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.gestures.Gestures
import org.json.JSONObject


const val URL = "https://api.dialogflow.com/v1"
const val APP_ID = "00497f569fda48b0b076d8f8c99dcde0"
val FAILED_RESPONSES = listOf("Ik begrijp het niet.", "Ik versta het niet.")
const val TIMEOUT = 4000 // 4 seconden.

val start: State = state(interaction) {

    onEntry {
        furhat.ask("Hallo! Heb je een pensioengerelateerde vraag?")
    }

    onResponse<JaIntent> {
        furhat.ask("Zeg het eens?")
    }

    onResponse<NeeIntent> {
        furhat.say("Oke, geen probleem")
        goto(idle)
    }

    onResponse<StopIntent> {
        furhat.say("Oke")
        goto(idle)
    }

    onResponse {
        furhat.say(async = true) {
            +"Eens even kijken"
            +Gestures.GazeAway
        }

        val response = call(sendQuestion(it.text, it.userId)) as String
        furhat.say(response)

        furhat.listen(timeout = 8000)
    }
}

fun sendQuestion(question: String, session: String) = state {
    onEntry {
        val query = "$URL/query?v=20170712&lang=nl&query=$question&sessionId=$session".replace(" ", "+")

        val obj = call {
            try {
                khttp.get(query, headers = mapOf("Authorization" to "Bearer $APP_ID")).jsonObject
            } catch (ex: Exception) {
                terminate("Er ging iets mis met de HTTP bibliotheek")
            }
        } as JSONObject

        val status = obj.getJSONObject("status")
        val code: Int = status["code"] as Int
        if (code != 200 && code != 206) {
            terminate("API probleem! HTTP status code is: $code")
        }

        val result = obj.getJSONObject("result")
        val fulfillment = result.getJSONObject("fulfillment")

        val response = fulfillment["speech"]

        // Kijkt naar response voor een fallback en bepaalt dan wat er terug gestuurd wordt.
        val reply = when {
            FAILED_RESPONSES.contains(response) -> {
                println("DialogFlow heeft geen antwoord op vraag: $question")
                "Dialog flow heeft geen antwoord voor je"
            }
            else -> response
        }

        terminate(reply)
    }

    onTime(TIMEOUT) {
        println("Connectie is TIMEOUT!")
        // Als de timeout wordt geactiveerd, dan niks terug sturen!
        terminate("TIMEOUT! Ik heb een connectie probleem!")
    }
}