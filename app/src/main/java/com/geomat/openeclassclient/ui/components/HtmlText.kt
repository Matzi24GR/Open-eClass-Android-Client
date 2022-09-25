package com.geomat.openeclassclient.ui.components

import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    darkThemeEnabled: Boolean,
    maxLines: Int = Integer.MAX_VALUE,
    enableLinks: Boolean = false,
    darkTextColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Gray
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                if (darkThemeEnabled) {
                    setTextColor(darkTextColor.toArgb())
                }
                if (enableLinks) {
                    linksClickable = true
                    movementMethod = LinkMovementMethod.getInstance()
                }
                setMaxLines(maxLines)
                ellipsize = TextUtils.TruncateAt.MIDDLE
            }
        },
        update = {
            it.text = html.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT).trim()
        }
    )
}