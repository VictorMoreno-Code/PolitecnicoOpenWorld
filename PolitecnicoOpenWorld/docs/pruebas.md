# Pruebas — Reacción de voz al conectar un golpe especial

- **Issue:** VictorMoreno-Code/PolitecnicoOpenWorld#1
- **Pull Request:** gabrielhuav/PolitecnicoOpenWorld#154
- **Rama:** `feature/1-super-hit-reaction-sound`
- **Fecha:** 2026-09-28

## Alcance

Se valida que el juego reproduzca una reacción de voz cuando un golpe especial
(súper, poder extra o fatality) conecta sobre el rival **sin ser bloqueado**, y
que **no** la reproduzca en los estados alternos.

La regla vive en `SfSuperHitReaction` (objeto de dominio sin dependencias de
Android) y se engancha en `StreetFighterCombate` en el punto donde ya se
resuelven los impactos.

## Entorno

- **Dispositivo:** Samsung Galaxy S25 Ultra (dispositivo físico)
- **Build:** `debug`
- **Commit probado:** `ea6f21b6`

## Pruebas manuales

| ID | Caso | Pasos | Resultado esperado | Resultado obtenido | Evidencia |
|---|---|---|---|---|---|
| MP-01 | Comportamiento previo | Compilar desde `main`. Conectar un golpe especial sobre el rival. | No se reproduce ninguna voz de reacción. | Correcto: no suena. | `TEST_BEFORE` (video en el PR) |
| MP-02 | Golpe especial conecta | Compilar desde la rama. Conectar un golpe especial sin que el rival se cubra. | Se reproduce la voz de reacción. | Correcto: suena. | `TEST_After` (video en el PR) |
| MP-03 | Golpe especial bloqueado (estado alterno) | En la rama. Mantener atrás en el joystick para cubrirse y recibir un golpe especial. | No se reproduce la voz. | Correcto: no suena. | `TEST_BLOQUEO_After` (video en el PR) |

El bloqueo en este juego es al estilo Street Fighter: caminar hacia atrás
(`WALK_BACKWARD`) cuenta como guardia. Se usó guardia alta a propósito, porque
un ataque `OVERHEAD` rompe la guardia baja por diseño.

## Pruebas automatizadas

```
./gradlew :shared:testAndroidHostTest --tests "ovh.gabrielhuav.pow.domain.streetfighter.SfSuperHitReactionTest"
```

| Test | Qué verifica | Resultado |
|---|---|---|
| `suena cuando un super conecta sin bloqueo y paso el cooldown` | Caso feliz | PASS |
| `no suena si el golpe fue bloqueado` | Estado alterno: bloqueo | PASS |
| `no suena si el golpe no es especial` | Estado alterno: golpe normal | PASS |
| `no suena de nuevo antes de que pase el cooldown` | Anti-spam del clip | PASS |
| `suena otra vez justo al cumplirse el cooldown` | Límite exacto del cooldown | PASS |
| `super art y fatality cuentan como golpe especial` | Clasificación de estados | PASS |
| `un golpe normal no cuenta como golpe especial` | Clasificación de estados | PASS |

**Resultado:** 7/7 PASS — `BUILD SUCCESSFUL`.

## Casos cubiertos solo por prueba automatizada

| Caso | Por qué no se probó a mano |
|---|---|
| Cooldown de 3 s entre dos golpes especiales seguidos | Reproducir dos impactos especiales dentro de una ventana exacta de 3 s depende del comportamiento de la IA y no es determinista a mano. Los tests `no suena de nuevo antes de que pase el cooldown` y `suena otra vez justo al cumplirse el cooldown` cubren ambos lados del límite. |
| Asset de audio ausente | `emitVoiceClip` verifica que el archivo exista antes de reproducirlo, así que un asset faltante es un no-op. No se forzó el escenario en dispositivo. |

## Conclusión

Los tres casos manuales dieron el resultado esperado y los 7 tests unitarios
pasan. No se modificó ningún valor de combate (daño, frames, máquina de
estados), por lo que no se detectaron regresiones en el resto de la pelea.
