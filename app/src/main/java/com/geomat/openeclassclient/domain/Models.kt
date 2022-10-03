package com.geomat.openeclassclient.domain

import android.os.Parcelable
import com.geomat.openeclassclient.R
import kotlinx.parcelize.Parcelize

data class Announcement(
    var id: String,                  // ex.  15 or s15 for system announcements
    var courseId: String?,           // ex.  DAI104 or null for system announcements
    var courseName: String?,         // ex.  Βάσεις Δεδομένων
    var title: String,               // ex.  ΠΑΡΑΤΑΣΗ ΗΛΕΚΤΡΟΝΙΚΗΣ ΑΞΙΟΛΟΓΗΣΗΣ ΔΙΔΑΚΤΙΚΟΥ ΕΡΓΟΥ ΕΑΡΙΝΟΥ ΕΞΑΜΗΝΟΥ 2019-2020
    var link: String,                // ex.  https://openeclass.uom.gr/modules/announcements/main_ann.php?aid=15
    var description: String,         // ex.  Σας ενημερώνουμε ότι η ηλεκτρονική αξιολόγηση διδακτικού έργου για το ....
    var date: Long,                  // ex.  1591786714000
    var isRead: Boolean

)

data class CalendarEvent(

    val id: Long,                   // ex. 18

    var title: String,              // ex.  "Βάσεις Δεδομένων: Εργασία 1"
    var start: Long = 0L,           // ex.  1586811540000
    var end: Long = 0L,             // ex.  1586811540000
    var content: String,            // ex.  "&lt;p&gt;Δείτε το συνημμένο.&lt;/p&gt;\n(deadline: 2020-04-13 23:59:00)"
    var event_group: String,        // ex.  "deadline"
    var Class: String,              // ex.  "event-important"
    var event_type: String,         // ex.  "assignment"
    var courseCode: String?,        // ex.  "DAI104" or null for user events
    var url: String                 // ex.  "https://openeclass.uom.gr/modules/work/index.php?id=18&course=DAI104"
)

@Parcelize
data class Course(

    val id: String,                 // ex. DAI107

    var title: String,              // ex.  Βάσεις Δεδομένων ΙΙ - ΠΛ0601
    var desc: String,               // ex.  ""
    var imageUrl: String,
    var tools: List<Tool>
) : Parcelable

data class UserInfo(
    var username: String,           // ex.  xyz2068
    var fullName: String,           // ex.  John Smith
    var category: String,           // ex.  Undergraduate » Comp Sci
    var imageUrl: String            // ex.  /template/default/img/default_256.png
)

@Parcelize
data class Tool(
    var isHandled: Boolean = false,
    var name: String,
) : Parcelable

// Complete Tool Array https://github.com/gunet/openeclass/blob/fa971635786feb67ef8d3837adb3242581422ee2/include/init.php#L447-L474
// TODO: add missing (TC, REQUEST, H5P)
enum class Tools(val value: String, val isHandled: Boolean, val path: String, val stringResource: Int, val icon: String) {
    DOCUMENTS       ("docs",            false, "document",          R.string.tool_docs,             ""),
    ANNOUNCEMENTS   ("announcements",   false, "announcements",     R.string.announcements_tab,     ""),
    EXERCISES       ("exercise",        false, "exercise",          R.string.tool_exercise,         ""),
    GRADEBOOK       ("gradebook",       false, "gradebook",         R.string.tool_gradebook,        ""),
    GLOSSARY        ("glossary",        false, "glossary",          R.string.tool_glossary,         ""),
    LEARN_PATH      ("lp",              false, "learnPath",         R.string.tool_lp,               ""),
    MIND_MAP        ("mindmap",         false, "mindmap",           R.string.tool_mindmap,          ""),
    ASSIGNMENTS     ("assignments",     false, "work",              R.string.tool_assignments,      ""),
    QUESTIONNAIRES  ("questionnaire",   false, "questionnaire",     R.string.tool_questionnaire,    ""),
    EBOOK           ("ebook",           false, "ebook",             R.string.tool_ebook,            ""),
    CONFERENCE      ("conference",      false, "chat",              R.string.tool_conference,       ""),
    CALENDAR        ("calendar",        false, "agenda",            R.string.calendar_tab,          ""),
    BLOG            ("blog",            false, "blog",              R.string.tool_blog,             ""),
    MESSAGES        ("dropbox",         false, "message",           R.string.tool_dropbox,          ""),
    GROUPS          ("groups",          false, "group",             R.string.tool_groups,           ""),
    ATTENDANCE      ("attendance",      false, "attendance",        R.string.tool_attendance,       ""),
    MEDIA           ("videos",          false, "video",             R.string.tool_videos,           ""),
    PROGRESS        ("fa-trophy",       false, "progress",          R.string.tool_fa_trophy,        ""),
    FORUM           ("forum",           false, "forum",             R.string.tool_forum,            ""),
    LINKS           ("links",           false, "link",              R.string.tool_links,            ""),
    WALL            ("fa-list",         false, "wall",              R.string.tool_fa_list,          ""),
    CHAT            ("fa-commenting",   false, "chat",              R.string.tool_fa_commenting,    ""),
    WIKI            ("wiki",            false, "wiki",              R.string.tool_wiki,             "");

    companion object {
        fun from(s: String): Tools? = values().find { it.value == s }
    }
}