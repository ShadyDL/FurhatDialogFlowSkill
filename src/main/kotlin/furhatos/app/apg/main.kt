package furhatos.app.apg

import furhatos.app.apg.flow.idle
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill


class APGSkill : Skill() {
    override fun start() {
        Flow().run(idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}