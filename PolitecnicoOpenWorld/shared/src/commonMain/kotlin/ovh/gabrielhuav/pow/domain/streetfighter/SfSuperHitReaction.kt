package ovh.gabrielhuav.pow.domain.streetfighter

import ovh.gabrielhuav.pow.domain.models.streetfighter.SfFighterState

/**
 Reacción de voz cuando un golpe ESPECIAL conecta (súper, poder extra o fatality).
 solo debe sonar si: el golpe es de un estado especial, NO fue bloqueado, y ya pasó el
 cooldown desde la última vez que sonó (para no repetirse si conectan varios seguidos).
 */
object SfSuperHitReaction {

    const val COOLDOWN_MS = 3000L

    fun deberiaSonar(esGolpeEspecial: Boolean, bloqueado: Boolean, ahora: Long, ultimaVezMs: Long): Boolean {
        if (!esGolpeEspecial || bloqueado) return false
        return ahora - ultimaVezMs >= COOLDOWN_MS
    }

    fun esEstadoEspecial(state: SfFighterState): Boolean = state in ESTADOS_ESPECIALES

    private val ESTADOS_ESPECIALES: Set<SfFighterState> = setOf(
        SfFighterState.SUPER_ART,
        SfFighterState.FATALITY,
        SfFighterState.SPECIAL_1_LIGHT,
        SfFighterState.SPECIAL_1_MEDIUM,
        SfFighterState.SPECIAL_1_HEAVY,
        SfFighterState.BONUS_POWER_1,
        SfFighterState.BONUS_POWER_2,
        SfFighterState.BONUS_POWER_3,
        SfFighterState.BONUS_POWER_4,
        SfFighterState.BONUS_POWER_5,
        SfFighterState.BONUS_POWER_6,
        SfFighterState.BONUS_POWER_7,
        SfFighterState.BONUS_POWER_8,
        SfFighterState.BONUS_POWER_9,
        SfFighterState.BONUS_POWER_10,
        SfFighterState.BONUS_POWER_11,
    )
}