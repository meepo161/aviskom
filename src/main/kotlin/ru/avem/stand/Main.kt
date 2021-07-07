package ru.avem.stand

import ru.avem.stand.modules.i.Head.Companion.head
import ru.avem.stand.modules.i.Head.Companion.tests
import ru.avem.stand.modules.i.Head.Companion.views
import ru.avem.stand.modules.i.views.TFXViewManager
import ru.avem.stand.modules.r.common.AggregateView
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.validateDB
import ru.avem.stand.modules.r.tests.pi.load.Load
import ru.avem.stand.modules.r.tests.psi.hv.HV
import ru.avem.stand.modules.r.tests.psi.hvSG.HVSG
import ru.avem.stand.modules.r.tests.psi.idle.Idle
import ru.avem.stand.modules.r.tests.psi.ikasSG.IKASSG
import ru.avem.stand.modules.r.tests.psi.mgr.MGR
import ru.avem.stand.modules.r.tests.psi.mgrSG.MGRSG

val head = head {
    validateDB() //TODO вынести в модуль СУБД и перенести валидацию туда
    tests {
        it.addModules(
            MGR(),
            HV(),
            MGRSG(),
            HVSG(),
            IKASSG(),
            Idle(),
            Load()
        )
    }
    views {
        TFXViewManager
//        it.addView(AuthorizationView::class)
        it.addView(AggregateView::class)
    }
}

fun main() {
    Properties.initTestsData()
    head.showRequiredViews()
}
