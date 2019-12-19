package furhatos.app.apg.flow

import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.util.Language


val idle: State = state {

    init {
        furhat.setVoice(Language.DUTCH)
        if (users.count > 0) {
            furhat.attend(users.random)
            goto(start)
        }
    }

    onEntry {
        furhat.attendNobody()
    }

    onUserEnter {
        furhat.attend(it)
        goto(start)
    }
}

val interaction: State = state {

    var nomatches = 0
    var silences = 0

    onResponse {
        nomatches++
        when (nomatches) {
            1 -> furhat.ask("Ik heb je niet begrepen. Kun je het herhalen?")
            2 -> furhat.ask("Sorry, ik heb het nog steeds niet begrepen. Kun je het nog een keer herhalen?")
            else -> {
                furhat.gesture(Gestures.ExpressSad(duration = 2.0))
                furhat.say("Ik begrijp het nog steeds niet.")
                reentry()
            }
        }
    }

    onNoResponse {
        silences++
        when (silences) {
            1 -> furhat.ask("Ik kan je niet horen.")
            2 -> furhat.ask("Ik hoor nog steeds niets, kun je wat harder praten?")
            else -> {
                furhat.gesture(Gestures.ExpressFear(duration = 2.0))
                furhat.say("Ik ben bang dat ik doof wordt, heb je namelijk nog steeds niet gehoord.")
                reentry()
            }
        }
    }

    onUserLeave(instant = true) {
        if (users.count > 0) {
            if (it == users.current) {
                furhat.attend(users.other)
                goto(start)
            } else {
                furhat.glance(it)
            }
        } else {
            goto(idle)
        }
    }

    onUserEnter(instant = true) {
        furhat.glance(it)
    }
}