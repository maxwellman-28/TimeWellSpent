package com.example.timewellspent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class GameEntry(
    var name: String = "Random Game",
    var datePlayed: Date = Date(),
    var elapsedTime: Int = 0, // time in minutes
    var moneySpent: Int = 0, // stored in cents (saves the decimal imprecision nonsense)
    var emotion: String = EMOTION.NEUTRAL.name,
    var ownerId: String? = null, // null so that new objects receive an id from the server
    var objectId: String? = null
) : Parcelable {
    enum class EMOTION(var emoji: String) {
        HAPPY ("\uD83D\uDE03"),
        SAD ("\u2639\uFE0F"),
        FRUSTRATED("\uD83D\uDE2C"),
        ANGRY("\uD83E\uDD2C"),
        NEUTRAL("\uD83D\uDE10")
    }
}
