package com.geomat.openeclassclient.util

import android.net.Uri
import android.provider.CalendarContract

fun asSyncAdapter(uri: Uri, account: String, accountType: String): Uri {
    return uri.buildUpon()
        .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
        .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
        .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build()
}
