package ru.avem.stand

import ru.avem.stand.modules.i.Head.Companion.head
import ru.avem.stand.modules.i.Head.Companion.tests
import ru.avem.stand.modules.i.Head.Companion.views
import ru.avem.stand.modules.i.views.TFXViewManager
import ru.avem.stand.modules.r.common.AggregateView
import ru.avem.stand.modules.r.common.authorization.AuthorizationView
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.validateDB
import ru.avem.stand.modules.r.tests.pi.incn.IncN
import ru.avem.stand.modules.r.tests.pi.load.Load
import ru.avem.stand.modules.r.tests.pi.maxm.MaxM
import ru.avem.stand.modules.r.tests.pi.minm.MinM
import ru.avem.stand.modules.r.tests.pi.overi.OverI
import ru.avem.stand.modules.r.tests.pi.overm.OverM
import ru.avem.stand.modules.r.tests.pi.startmi.StartMI
import ru.avem.stand.modules.r.tests.pi.varyuf.VaryUF
import ru.avem.stand.modules.r.tests.psi.hv.HV
import ru.avem.stand.modules.r.tests.psi.hvSG.HVSG
import ru.avem.stand.modules.r.tests.psi.idle.Idle
import ru.avem.stand.modules.r.tests.psi.ikas.IKAS
import ru.avem.stand.modules.r.tests.psi.ikasSG.IKASSG
import ru.avem.stand.modules.r.tests.psi.kz.KZ
import ru.avem.stand.modules.r.tests.psi.mgr.MGR
import ru.avem.stand.modules.r.tests.psi.mgrSG.MGRSG
import ru.avem.stand.modules.r.tests.psi.mvz.MVZ

val head = head {
    validateDB() // TODO вынести в модуль СУБД и перенести валидацию туда
    tests {
        it.addModules(
            MGR(),
            MGRSG(),
            HV(),
            HVSG(),
//            IKAS(),
            IKASSG(),
            Idle(),
//            MVZ(),
//            KZ(),
            Load(),
//            OverM(),
//            OverI(),
//            VaryUF(),
//            IncN(),
//            StartMI(),
//            MaxM(),
//            MinM()
        )
    }
    views {
        TFXViewManager
        it.addView(AuthorizationView::class)
//        it.addView(AggregateView::class)
    }
}

fun main() {
    Properties.initTestsData()
    head.showRequiredViews()
}
