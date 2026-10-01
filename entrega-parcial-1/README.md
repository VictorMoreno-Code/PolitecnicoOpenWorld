# Entrega — Primer examen parcial

**Unidad de aprendizaje:** Desarrollo de aplicaciones móviles nativas
**Programa:** Ingeniería en Sistemas Computacionales / Plan 2020 — ESCOM-IPN
**Grupo:** 7CV4 · Periodo 2027-1
**Fecha de entrega:** 1 de octubre de 2026

---

## 1. Equipo

| Integrante | Usuario de GitHub | PR entregado |
|---|---|---|
| Moreno López Victor Eduardo | [@VictorMoreno-Code](https://github.com/VictorMoreno-Code) | [#154](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/154) |
| Rodriguez Candelario Miguel Ángel | [@TheMike54](https://github.com/TheMike54) | [#145](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145) |
| Reyna Mendoza Ian Gael | [@IanRey692](https://github.com/IanRey692) | [#146](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/146) |

Este índice corresponde a la entrega individual de **Victor Eduardo Moreno López**.

## 2. Objetivo y alcance

### Qué se hizo

Se agregó una **reacción de voz que se reproduce cuando un golpe especial** (súper, poder extra o
fatality) **conecta sobre el rival sin ser bloqueado**, en el modo Titulación por Combate.

### Comportamiento actual vs. esperado

| | |
|---|---|
| **Antes** | Un súper que conecta produce la misma retroalimentación sonora que un golpe normal. El jugador no recibe señal extra de que el movimiento de mayor impacto sí entró. |
| **Después** | Al conectar un golpe especial sin bloqueo se reproduce un clip de voz, con un enfriamiento de 3 s para que no se encime si conectan varios seguidos. |

### Usuario afectado

Cualquier jugador del modo Titulación por Combate.

### Archivos modificados

- `shared/.../domain/streetfighter/SfSuperHitReaction.kt` *(nuevo)* — la regla, sin dependencias de Android.
- `shared/.../viewmodel/StreetFighterCombate.kt` — enganche en la resolución de impactos.
- `shared/.../viewmodel/StreetFighterViewModel.kt` — marca de tiempo del enfriamiento.
- `app/src/main/assets/STREETFIGHTER/SOUNDS/special_super_hit_reaction.m4a` *(nuevo)* — el audio.
- `shared/src/commonTest/.../SfSuperHitReactionTest.kt` *(nuevo)* — 8 pruebas unitarias.

### Qué queda fuera

No se modificó ningún valor de combate: daño, frames, máquina de estados, medidor de súper ni
comportamiento de la IA. Tampoco se tocó el catálogo de escenarios ni el modo multijugador.

### Criterios de aceptación

1. **Éxito:** un golpe especial que conecta y NO es bloqueado reproduce la voz.
2. **Condición alterna:** un golpe especial **bloqueado** no la reproduce.
3. **Condición alterna:** un golpe **normal** nunca la reproduce.
4. **Límite:** dos golpes especiales dentro de 3 s reproducen la voz una sola vez.
5. **Tolerancia a fallo:** si el archivo de audio no existe, no suena nada y el juego no se cae.

## 3. Referencias del trabajo

| | |
|---|---|
| **Issue** | [VictorMoreno-Code/PolitecnicoOpenWorld#1](https://github.com/VictorMoreno-Code/PolitecnicoOpenWorld/issues/1) |
| **Pull Request** | [gabrielhuav/PolitecnicoOpenWorld#154](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/154) |
| **Rama de trabajo** | `feature/1-super-hit-reaction-sound` |
| **SHA base** | `7ed32539` — `chore: bump versionName 1.0.0.17 -> 1.0.0.18` |
| **SHA final entregado** | `23690ab7` — `fix: reset the special hit reaction cooldown between fights` |

### Relación entre el SHA probado y el SHA entregado

El QA inicial se ejecutó sobre `dda3b54d`. La revisión de [@TheMike54](https://github.com/TheMike54)
encontró un defecto de comportamiento en ese SHA, se corrigió en `23690ab7`, y **el caso que falló
se volvió a ejecutar sobre el SHA nuevo**. `23690ab7` es el SHA entregado y es el que corresponde a
toda la evidencia marcada como posterior a la corrección.

### Entorno de referencia

| | |
|---|---|
| Sistema operativo | Windows 11 |
| IDE | Android Studio (JDK empaquetado) |
| Dispositivo físico | Samsung Galaxy S25 Ultra — Android 16 |
| Emulador (revisor) | Pixel 8 — API 35 |
| Proyecto Gradle | carpeta interna `PolitecnicoOpenWorld/` |

Se ejecutó la versión base antes de modificar código para confirmar que la reacción **no** existía
previamente (clip `TEST_BEFORE`, en el PR). No se actualizó ninguna dependencia.

## 4. Riesgos identificados

| # | Riesgo | Impacto | Caso que lo cubre |
|---|---|---|---|
| R1 | El clip suena cuando no debe (golpe bloqueado o normal) y rompe la lectura del combate | Medio — confunde al jugador sobre si su ataque entró | CP-02, CP-03 |
| R2 | El clip se encima consigo mismo al encadenar especiales | Bajo — molesto, no afecta jugabilidad | CP-04 |
| R3 | El enganche dentro de la resolución de impactos altera audio o daño ya existentes | Alto — sería una regresión en el núcleo del combate | CP-03, CP-05 |
| R4 | El estado del enfriamiento sobrevive entre peleas y silencia la función | Alto — **se materializó**; ver hallazgo D1 | CP-06 |
| R5 | El archivo de audio falta en el empaquetado | Bajo — `emitVoiceClip` verifica antes de reproducir | CP-07 |

## 5. Matriz de pruebas

Detalle completo en [`PolitecnicoOpenWorld/docs/pruebas.md`](../PolitecnicoOpenWorld/docs/pruebas.md).
Evidencias en video enlazadas desde la sección **QA evidence** del PR #154.

| ID | Categoría | Autor | SHA | Dispositivo | Esperado | Real | Estado |
|---|---|---|---|---|---|---|---|
| CP-01 | Ruta feliz | Victor Moreno | `dda3b54d` | S25 Ultra / Android 16 | Especial conecta sin bloqueo → suena | Sonó | Aprobado |
| CP-02 | Condición alterna — bloqueo | — | `23690ab7` | — | Especial bloqueado → no suena | Cubierto solo por prueba unitaria; no se reprodujo en 5 peleas | No reproducido |
| CP-03 | Regresión — golpe normal | @TheMike54 | `dda3b54d` | Pixel 8 / API 35 | Golpe normal → no suena la reacción | Nunca sonó en golpes normales | Aprobado |
| CP-04 | Límite — enfriamiento 3 s | @TheMike54 | `dda3b54d` | Pixel 8 / API 35 | Nunca dos veces en 3 s | Separación siempre ≥ 8 s | Aprobado (no forzado) |
| CP-05 | Condición alterna — especial fallado | @TheMike54 | `dda3b54d` | Pixel 8 / API 35 | Especial esquivado → no suena | Proyectil saltado: sin daño, sin reacción | Aprobado |
| CP-06 | Navegación y estado — revancha | @TheMike54 → Victor Moreno | `dda3b54d` → `23690ab7` | Pixel 8 / API 35 → S25 Ultra | Tras salir y volver a pelea, sigue sonando | **Falló** en `dda3b54d`; **aprobado** tras corregir en `23690ab7` | Corregido y reprobado |
| CP-07 | Compatibilidad / entorno | Ambos | `dda3b54d`, `23690ab7` | Emulador API 35 **y** físico Android 16 | Mismo comportamiento en ambos entornos | Consistente en los dos | Aprobado |

## 6. Hallazgos

### D1 — La reacción deja de sonar después de la primera pelea · severidad alta · **corregido**

**Reportado por:** [@TheMike54](https://github.com/TheMike54) en la revisión del PR #154.

**Pasos reproducibles:** abrir la app, pelear hasta que un especial conecte (suena), terminar la
pelea y dar revancha, conectar otro especial.

**Esperado vs. observado:** debía sonar; no sonaba. Al reiniciar la app volvía a funcionar.

**Causa:** `lastSuperHitReactionMs` no se reiniciaba en `resetInternals()` ni en `resetRound()`,
mientras que `gameNow` sí vuelve a `0` en cada pelea. En la siguiente pelea `ahora - ultimaVezMs`
resultaba negativo y `deberiaSonar` devolvía `false` hasta que el reloj nuevo rebasara el valor de
la pelea anterior más 3 s — más de lo que dura una pelea normal.

**Severidad alta** porque desactiva la función completa en el uso real del juego, en silencio y sin
ningún síntoma visible.

**Corrección (`23690ab7`):** se reinicia la marca junto a `lastHurtVoiceMs` / `lastAttackVoiceMs` en
ambos bloques de reinicio, y además la regla se volvió defensiva: si `ahora < ultimaVezMs` el reloj
se reinició y no hay enfriamiento pendiente. Se agregó la prueba de regresión
`suena en una pelea nueva aunque el reloj se haya reiniciado`, que **falla en `dda3b54d` y pasa en
`23690ab7`**. Verificado además en dispositivo físico.

### D2 — Una sola marca de tiempo compartida por ambos peleadores · severidad baja · **pendiente**

Si P1 conecta un especial y P2 conecta otro antes de 3 s, se omite la reacción de P2. Se deduce del
código; no se observó en las peleas ejecutadas. Se documentó y se dejó fuera de alcance por
proximidad de la entrega, en lugar de ampliar el cambio sin poder re-ejecutar todo el QA.

### D3 — Referencias cortas `#1` / `#2` en `docs/pruebas.md` · severidad baja · **corregido**

Apuntaban a elementos ajenos del repositorio original. Corregidas a la forma
`propietario/repositorio#número` en `23690ab7`.

### D4 — Falta de salto de línea al final de `SfSuperHitReaction.kt` · **corregido** en `23690ab7`.

## 7. Verificaciones automáticas

El workflow **PR Quality Gate** (`.github/workflows/pr-quality-gate.yml`) ejecuta construcción debug
de Android, pruebas unitarias de `app` y `shared`, comprobación de nombres de pruebas KMP y análisis
estático con detekt. Se activa en PR hacia `main` con cambios en `PolitecnicoOpenWorld/`.

| Verificación | Estado | SHA | Enlace |
|---|---|---|---|
| PR Quality Gate — PR #154 (repo original) | **Bloqueado**: requiere autorización del mantenedor | `23690ab7` | pestaña Checks del PR #154 |
| PR Quality Gate — PR #2 (fork propio) | **Aprobado** | `dda3b54d` | [run](https://github.com/VictorMoreno-Code/PolitecnicoOpenWorld/actions) |
| `:shared:testAndroidHostTest` (local) | **8/8 aprobadas** | `23690ab7` | salida de Gradle |

### Interpretación

GitHub no ejecuta workflows de contribuyentes externos desde un fork sin que un mantenedor lo
autorice, así que el Quality Gate del PR #154 aparece como *awaiting approval*. **Es un bloqueo
externo documentado, no un fallo del cambio, y no se presenta como prueba aprobada.** La validación
local disponible sí se ejecutó: las 8 pruebas unitarias pasan sobre el SHA entregado, y el mismo
workflow corrió en verde sobre esta rama en el fork propio.

### Qué queda fuera de la cobertura del CI

El Quality Gate valida lógica pura y estilo; **no reproduce audio**. Que el clip efectivamente suene
en un dispositivo solo pudo verificarse con las grabaciones manuales. Además, el CI permite
`MAPS_API_KEY` vacío y omite `google-services.json`, por lo que nada de lo verde ahí demuestra que
mapas o servicios externos funcionen.

## 8. Revisión de pares

| Rol | PR | Enlace |
|---|---|---|
| Recibida de [@TheMike54](https://github.com/TheMike54) | #154 (mío) | https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/154#pullrequestreview-5374139314 |
| Mi respuesta a esa revisión | #154 | https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/154#issuecomment-5938462174 |
| Emitida sobre [@TheMike54](https://github.com/TheMike54) | [#145](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145) | https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145#issuecomment-5920967885 |
| Emitida sobre [@IanRey692](https://github.com/IanRey692) | [#146](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/146) | https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/146#issuecomment-5939101814 |

Se respondió a cada una de las observaciones recibidas: tres corregidas mediante commit nuevo
(D1, D3, D4) y una documentada con justificación (D2).

## 9. Bitácora — Victor Eduardo Moreno López

### Commits

| SHA | Mensaje |
|---|---|
| `4e31b200` | `feat: add rule deciding when a special hit plays a reaction voice` |
| `ea6f21b6` | `feat: play reaction voice when a special attack connects` |
| `5bcd62a` | `docs: add QA test matrix for the special hit reaction` |
| `dda3b54d` | `docs: fix device line formatting in test matrix` |
| `23690ab7` | `fix: reset the special hit reaction cooldown between fights` |

### Casos ejecutados por mí

CP-01 (ruta feliz), CP-06 (revancha, re-ejecución tras la corrección) y CP-07 (entorno físico), más
la ejecución de la versión base antes de modificar código.

### Revisiones emitidas por mí

PR #145 y PR #146 — ver sección 8.

## 10. Conclusiones

El cambio es aditivo y está acotado: no modifica ningún valor de combate y reutiliza la ruta de
audio existente, que verifica la presencia del archivo antes de reproducirlo. La revisión de pares
encontró un defecto real de estado entre peleas que la matriz inicial no cubría — ese caso se
convirtió en prueba unitaria de regresión y se volvió a ejecutar en dispositivo.

**Dictamen:** el cambio está listo para integrarse. Riesgos que permanecen: la marca de tiempo
compartida entre peleadores (D2, severidad baja, documentada), y que el caso de bloqueo no pudo
reproducirse manualmente — queda cubierto únicamente por prueba unitaria.

## 11. Referencias

- Enunciado del examen: *Primer examen parcial — Pull Request con aseguramiento de calidad*, 7CV4, 2027-1.
- README y `.github/workflows/pr-quality-gate.yml` del repositorio original, consultados el 28-sep-2026.
- Documentación interna del proyecto: `README for IAS/SF/`.
