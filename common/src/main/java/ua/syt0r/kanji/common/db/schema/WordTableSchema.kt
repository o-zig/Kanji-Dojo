package ua.syt0r.kanji.common.db.schema

object WordTableSchema {

    const val name = "word"

    object Columns {
        const val id = "id"
        const val expression = "expression"
        const val furigana = "furigana"
        const val priority = "priority"
    }

}