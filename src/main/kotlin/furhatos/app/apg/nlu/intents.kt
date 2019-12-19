package furhatos.app.apg.nlu

import furhatos.nlu.Intent
import furhatos.util.Language


class JaIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Ja",
            "Jazeker",
            "Ik denk het wel",
            "Ja die heb ik"
        )
    }
}

class NeeIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Nee",
            "Nee ik heb een andere vraag",
            "Nee die heb ik niet",
            "Nee niet echt"
        )
    }
}

class StopIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Stop",
            "Stoppen nu",
            "Afsluiten",
            "Hou je kop dicht"
        )
    }
}