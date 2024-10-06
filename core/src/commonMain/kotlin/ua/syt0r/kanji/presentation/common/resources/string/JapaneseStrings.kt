package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.withClickableUrl
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract
import kotlin.time.Duration

object JapaneseStrings : Strings {

    override val appName: String = "Kanji Dojo"

    override val hiragana: String = "ひらがな"
    override val katakana: String = "カタカナ"

    override val kunyomi: String = "訓読み"
    override val onyomi: String = "音読み"

    override val loading: String = "読み込み中"

    override val letterPracticeTypeWriting: String = "書く事"
    override val letterPracticeTypeReading: String = "読む事"
    override val vocabPracticeTypeFlashcard: String = "フラッシュカード"
    override val vocabPracticeTypeReadingPicker: String = "読み取りピッカー"
    override val vocabPracticeTypeWriting: String = "書く事"

    override val reviewStateDone: String = "完了"
    override val reviewStateDue: String = "復習"
    override val reviewStateNew: String = "未習"

    override val home: HomeStrings = JapaneseHomeStrings
    override val generalDashboard: GeneralDashboardStrings = JapaneseGeneralDashboardStrings
    override val commonDashboard = JapaneseCommonDashboardStrings
    override val dailyLimit = JapaneseDailyLimitStrings
    override val tutorialDialog: TutorialDialogStrings = JapaneseTutorialDialogStrings
    override val stats: StatsStrings = JapaneseStatsStrings
    override val search: SearchStrings = JapaneseSearchStrings
    override val alternativeDialog: AlternativeDialogStrings = JapaneseAlternativeDialogStrings
    override val addWordToDeckDialog: AddWordToDeckDialogStrings =
        JapaneseAddWordToDeckDialogStrings

    override val settings: SettingsStrings = JapaneseSettingsStrings
    override val reminderDialog: ReminderDialogStrings = JapaneseReminderDialogStrings
    override val about: AboutStrings = JapaneseAboutStrings
    override val backup: BackupStrings = JapaneseBackupStrings
    override val feedback: FeedbackStrings = JapaneseFeedbackStrings
    override val sponsor: SponsorStrings = JapaneseSponsorStrings

    override val deckPicker: DeckPickerStrings = JapaneseDeckPickerStrings
    override val deckEdit: DeckEditStrings = JapaneseDeckEditStrings
    override val deckDetails: DeckDetailsStrings = JapaneseDeckDetailsStrings
    override val commonPractice: CommonPracticeStrings = JapaneseCommonPracticeStrings
    override val letterPractice: LetterPracticeStrings = JapaneseLetterPracticeStrings
    override val vocabPractice: VocabPracticeStrings = JapaneseVocabPracticeStrings
    override val kanjiInfo: KanjiInfoStrings = JapaneseKanjiInfoStrings

    override val urlPickerMessage: String = "開く"
    override val urlPickerErrorMessage: String = "ブラウザーが見つかりません"

    override val reminderNotification: ReminderNotificationStrings =
        JapaneseReminderNotificationStrings

}

object JapaneseGeneralDashboardStrings : GeneralDashboardStrings {
    override val buttonDailyLimit: String = "毎日の目標"
    override val buttonVersionChange: String = "新機能"
    override val buttonTutorial: String = "チュートリアル"
    override val letterDecksTitle: String = "文字デッキ"
    override val vocabDecksTitle: String = "単語デッキ"
    override val buttonNoDecksTitle: String = "デッキなし"
    override val buttonNoDecksMessage: String = "作成"
    override val practiceTypeLabel: String = "練習タイプ"
    override val buttonNew: String = "学習"
    override val buttonDue: String = "復習"
    override val buttonAll: String = "学習 & 復習"
    override val streakTitle: String = "連続記録"
    override val currentStreakLabel: String = "現在の連続記録"
    override val longestStreakLabel: String = "最長連続記録"
}

object JapaneseHomeStrings : HomeStrings {
    override val screenTitle: String = JapaneseStrings.appName
    override val generalDashboardTabLabel: String = "ホーム"
    override val lettersDashboardTabLabel: String = "文字"
    override val vocabDashboardTabLabel: String = "単語"
    override val statsTabLabel: String = "統計"
    override val searchTabLabel: String = "検索"
    override val settingsTabLabel: String = "設定"
}

object JapaneseCommonDashboardStrings : CommonDashboardStrings {
    override val emptyScreenMessage: (inlineIconId: String) -> AnnotatedString = {
        buildAnnotatedString {
            append("アプリを使うにはデッキが必要です。")
            appendInlineContent(it)
            append("　ボタンを押して、デッキを作成してください。")
        }
    }

    override val mergeButton: String = "統合"
    override val mergeCancelButton: String = "キャンセル"
    override val mergeAcceptButton: String = "統合"
    override val mergeTitle: String = "複数のデッキを1つに統合"
    override val mergeTitleHint: String = "タイトルを入力"
    override val mergeSelectedCount: (Int) -> String = { "$it 個選択中" }
    override val mergeClearSelectionButton: String = "クリア"

    override val mergeDialogTitle: String = "統合の確認"
    override val mergeDialogMessage: (String, List<String>) -> String = { newTitle, mergedTitles ->
        "以下の${mergedTitles.size}個のデッキが新しいデッキ「$newTitle」に統合されます: ${mergedTitles.joinToString()}"
    }
    override val mergeDialogCancelButton: String = "キャンセル"
    override val mergeDialogAcceptButton: String = "統合"

    override val sortButton: String = "並べ替え"
    override val sortCancelButton: String = "キャンセル"
    override val sortAcceptButton: String = "適用"
    override val sortTitle: String = "デッキの順序を変更"
    override val sortByTimeTitle: String = "最終練習時間で並べ替える"

    override val itemTimeMessage: (Duration?) -> String = {
        "最終練習日: " + when {
            it == null -> "なし"
            it.inWholeDays > 0 -> "${it.inWholeDays}日前"
            else -> "1日以内"
        }
    }

    override val itemTotal: String = "合計"
    override val itemDone: String = "完了"
    override val itemReview: String = "復習"
    override val itemNew: String = "未習"
    override val dailyPracticeTitle: String = "クイック練習"
    override val dailyPracticeNew: (Int) -> String = { "新しく学習 ($it)" }
    override val dailyPracticeDue: (Int) -> String = { "復習 ($it)" }
    override val itemGraphProgressTitle: String = "完了率"

    override val selectedPracticeTypeTemplate: (practiceType: String) -> String =
        { "練習タイプ: $it" }

}

object JapaneseDailyLimitStrings : DailyLimitStrings {
    override val enableSwitchTitle: String = "有効"
    override val enableSwitchDescription: String =
        "アプリによって促される毎日の練習の数を制限するには有効にします"
    override val lettersSectionTitle: String = "文字"
    override val vocabSectionTitle: String = "単語"
    override val combinedLimitSwitchTitle: String = "共通の上限"
    override val combinedLimitSwitchDescription: String = "すべての練習タイプで共通の上限を設定"
    override val newLabel: String = "新しい"
    override val dueLabel: String = "期限"
    override val noteMessage: String = "注意: 書く練習と読み練習は、制限に別々に加増されます"
    override val button: String = "保存"
    override val changesSavedMessage: String = "完了"
}


object JapaneseTutorialDialogStrings : TutorialDialogStrings {
    override val title: String = "チュートリアル"

    override val page1: String = """
        • このアプリは、復習のタイミングを最適化するSRS（間隔反復システム）を使っています
        • 復習を行うと、SRSがあなたの記憶力に応じて次の復習を予定します
        • 簡単に思い出せたら、復習の間隔は長くなり、難しかったら短くなります
    """.trimIndent()

    override val page2Top: String = """
        • あなたの記憶力を評価するために、復習後にいくつかの評価オプションが表示されます
    """.trimIndent()

    override val page2Bottom: String = """
        • 自分の記憶力に最も合ったオプションを選んでください
        • 自己評価することで、アプリがあなたに合ったペースで学習を調整できます
    """.trimIndent()

    override val page3Top: String = """
        • 毎日、復習した項目のステータスが更新されます
        • アプリは、新しい項目と復習の期限が過ぎた項目を復習させてくれます
    """.trimIndent()

    override val page3Bottom: String = """
        • 負担を抑えるために、1日の制限を設定できます
    """.trimIndent()

    override val page4Top: String = """
        • アプリを使い始めるには、デッキを作成してください
        • デッキは、マスターしたい項目を整理するために使用します。アプリには文字デッキと単語デッキがあります
    """.trimIndent()

    override val page4Bottom: String = """
        • 自分でデッキを作成するか、いくつかの事前に作られたデッキを選ぶことができます
    """.trimIndent()

    override val page5: String = """
        • デッキが作成されたら、復習を始められます
        • いくつかの練習モードがあるので、全部試してみてください
        • 一貫性が大切です。毎日少しずつ練習することが進歩の鍵です。無理しないように、1日の制限を下げることも検討してください
        • 日本語マスターへの道、がんばってください！\(^_^)/ 
    """.trimIndent()
}


private fun formatDuration(duration: Duration): String = when {
    duration.inWholeHours > 0 -> "${duration.inWholeHours}時 ${duration.inWholeMinutes % 60}分"
    duration.inWholeMinutes > 0 -> "${duration.inWholeMinutes}分 ${duration.inWholeSeconds % 60}秒"
    else -> "${duration.inWholeSeconds}秒"
}

object JapaneseStatsStrings : StatsStrings {
    override val todayTitle: String = "今日"
    override val monthTitle: String = "今月"
    override val monthLabel: (day: LocalDate) -> String =
        { "${it.year}年${it.monthNumber}月" }
    override val yearTitle: String = "今年"
    override val yearDaysPracticedLabel = { practicedDays: Int, daysInYear: Int ->
        "練習日数: $practicedDays/$daysInYear"
    }
    override val totalTitle: String = "合計"
    override val timeSpentTitle: String = "練習時間"
    override val reviewsCountTitle: String = "練習回数"
    override val formattedDuration: (Duration) -> String = { formatDuration(it) }
    override val uniqueLettersReviewed: String = "練習した異なる文字の数"
    override val uniqueWordsReviewed: String = "練習した異なる単語の数"
}


object JapaneseSearchStrings : SearchStrings {
    override val inputHint: String = "文字または単語を入力"
    override val charactersTitle: (count: Int) -> String = { "文字 ($it)" }
    override val wordsTitle: (count: Int) -> String = { "単語 ($it)" }
    override val radicalsSheetTitle: String = "部首で検索"
    override val radicalsFoundCharacters: String = "見つかった文字"
    override val radicalsEmptyFoundCharacters: String = "何も見つかりませんでした"
    override val radicalSheetRadicalsSectionTitle: String = "部首"
}

object JapaneseAlternativeDialogStrings : AlternativeDialogStrings {
    override val title: String = "別の単語"
    override val readingsTitle: String = "読み方"
    override val meaningsTitle: String = "意味"
    override val reportButton: String = "報告"
    override val closeButton: String = "閉じる"
}

object JapaneseAddWordToDeckDialogStrings : AddWordToDeckDialogStrings {
    override val title: (reading: String) -> String = { "「$it」を単語デッキに追加" }
    override val createDeckButton: String = " + 新しいデッキを作る"
    override val createDeckTitleHint: String = "ここにデッキのタイトルを入力..."
    override val savingStateMessage: String = "追加中"
    override val completedStateMessage: String = "追加完了"
    override val buttonCancel: String = "キャンセル"
    override val buttonAdd: String = "追加"
}

object JapaneseSettingsStrings : SettingsStrings {
    override val analyticsTitle: String = "分析レポート"
    override val analyticsMessage: String = "アプリを向上させるために匿名データの送信を許可する"
    override val themeTitle: String = "テーマ"
    override val themeSystem: String = "システムに従う"
    override val themeLight: String = "ライト"
    override val themeDark: String = "ダーク"
    override val reminderTitle: String = "リマインダー通知"
    override val reminderEnabled: String = "有効"
    override val reminderDisabled: String = "無効"
    override val feedbackTitle: String = "フィードバック"
    override val backupTitle: String = "バックアップ"
    override val aboutTitle: String = "このアプリについて"
}

object JapaneseReminderDialogStrings : ReminderDialogStrings {
    override val title: String = "リマインダー通知"
    override val noPermissionLabel: String = "通知の権限がありません"
    override val noPermissionButton: String = "許可"
    override val enabledLabel: String = "通知"
    override val timeLabel: String = "時間"
    override val cancelButton: String = "キャンセル"
    override val applyButton: String = "適用"
}

object JapaneseAboutStrings : AboutStrings {
    override val title: String = "このアプリについて"
    override val version: (versionName: String) -> String = { "バージョン: $it" }
    override val githubTitle: String = "プロジェクトのGitHubページ"
    override val versionChangesTitle: String = "変更履歴"
    override val versionChangesDescription: String = "アプリの変更履歴"
    override val versionChangesButton: String = "閉じる"
    override val githubDescription: String = "ソースコード、バグ報告、議論"
    override val creditsTitle: String = "クレジット"
    override val creditsDescription: String = "使用されるライブラリとデータソース"
}

object JapaneseBackupStrings : BackupStrings {
    override val title: String = "バックアップ"
    override val backupButton: String = "バックアップ作成"
    override val restoreButton: String = "バックアップからリストア"
    override val unknownError: String = "不明なエラー"
    override val restoreVersionMessage: (Long, Long) -> String = { backupVersion, currentVersion ->
        "データベースバージョン：$backupVersion（現在のバージョン：$currentVersion）"
    }
    override val restoreTimeMessage: (Instant) -> String = { "作成時間：$it" }
    override val restoreNote: String =
        "注意！すべての現在の進捗は、選択したバックアップからの進捗で置き換えられます"
    override val restoreApplyButton: String = "リストア"
    override val completeMessage: String = "完了"
}

object JapaneseFeedbackStrings : FeedbackStrings by EnglishFeedbackStrings {
    override val title: String = "フィードバック"
    override val topicTitle: String = "トピック"
    override val messageLabel: String = "ここにフィードバックを入力してください"
    override val button: String = "送信"
    override val successMessage: String = "フィードバックを送信しました"
    override val errorMessage: (String?) -> String = { "エラー: $it" }
}

object JapaneseSponsorStrings : SponsorStrings by EnglishSponsorStrings

object JapaneseDeckPickerStrings : DeckPickerStrings {

    override val title: String = "選択"

    override val customDeckButton: String = "空のデッキを作る"
    override val kanaTitle: String = "かな"

    override val kanaDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("かなは、いちばんやさしい日本語の文字です。かなはふたつに分けることができます。\n")
            append("・平仮名（ひらがな）─ 日本語のことばや音をつたえるときにつかいます。\n")
            append("・片仮名（かたかな）─ 外国のことばなどを書くときにつかいます。")
            withClickableUrl(
                url = "https://ja.wikibooks.org/wiki/%E3%81%B2%E3%82%89%E3%81%8C%E3%81%AA%E3%83%BB%E3%82%AB%E3%82%BF%E3%82%AB%E3%83%8A",
                color = urlColor
            ) {
                append("もっと知る")
            }
        }
    }
    override val hiragana: String = JapaneseStrings.hiragana
    override val katakana: String = JapaneseStrings.katakana

    override val jltpTitle: String = "日本語能力試験"
    override val jlptDescription: StringResolveScope<AnnotatedString> = {
        buildAnnotatedString {
            append("日本語能力試験 (JLPT) は、日本語を母語としない人のための日本語の試験です。N5からN1までの難しさがあります。")
            withClickableUrl(
                url = "https://ja.wikipedia.org/wiki/%E6%97%A5%E6%9C%AC%E8%AA%9E%E8%83%BD%E5%8A%9B%E8%A9%A6%E9%A8%93",
                color = MaterialTheme.extraColorScheme.link
            ) {
                append("詳細情報")
            }
        }
    }
    override val jlptItem: (level: Int) -> String = { "JLPT N$it" }

    override val gradeTitle: String = "常用漢字"
    override val gradeDescription = { urlColor: Color ->
        buildAnnotatedString {
            withClickableUrl(
                url = "https://ja.wikipedia.org/wiki/%E5%B8%B8%E7%94%A8%E6%BC%A2%E5%AD%97",
                color = urlColor
            ) {
                append("常用漢字")
            }
            append("は、2,136字から成る、よく使われる漢字の表です。内容は以下の通りです。\n")
            append("・最初の1,026字は小学校1年から6年までに学習（")
            withClickableUrl(
                url = "https://ja.wikipedia.org/wiki/%E6%95%99%E8%82%B2%E6%BC%A2%E5%AD%97",
                color = urlColor
            ) {
                append("教育漢字")
            }
            append("）。\n")
            append("・以降の1,110字は中学校以降に学習。")
        }
    }
    override val gradeItemNumbered: (Int) -> String = { "小学校${it}年" }
    override val gradeItemSecondary: String = "中学校以降"
    override val gradeItemNames: String = "人名用漢字(一)"
    override val gradeItemNamesVariants: String = "人名用漢字(二)（常用漢字の異体字）"

    override val wanikaniTitle: String = EnglishDeckPickerStrings.wanikaniTitle
    override val wanikaniDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("Tofuguが運営するWaniKaniに準拠したレベル別の漢字の一覧です。")
            withClickableUrl("https://www.wanikani.com/kanji?difficulty=pleasant", urlColor) {
                append("詳細情報")
            }
        }
    }
    override val wanikaniItem: (Int) -> String = { "WaniKani レベル$it" }

    override val vocabOtherTitle: String = "その他"
    override val vocabOtherDescription: AnnotatedString = "始めるための基本的なデッキ"
        .let { AnnotatedString(it) }

    override val vocabDeckItemWordsCountLabel: (words: Int) -> String = { "${it}語" }
    override val vocabDeckTitleTime: String = "時間"
    override val vocabDeckTitleWeek: String = "曜日"
    override val vocabDeckTitleCommonVerbs: String = "よく使う動詞"
    override val vocabDeckTitleColors: String = "色"
    override val vocabDeckTitleRegularFood: String = "普段の食べ物"
    override val vocabDeckTitleJapaneseFood: String = "日本の食べ物"
    override val vocabDeckTitleGrammarTerms: String = "文法用語"
    override val vocabDeckTitleAnimals: String = "動物"
    override val vocabDeckTitleBody: String = "体"
    override val vocabDeckTitleCommonPlaces: String = "よく行く場所"
    override val vocabDeckTitleCities: String = "都市"
    override val vocabDeckTitleTransport: String = "交通"

}

object JapaneseDeckEditStrings : DeckEditStrings {
    override val createTitle: String = "デッキの作成"
    override val ediTitle: String = "デッキの編集"
    override val searchHint: String = "文字を入力"
    override val editingModeSearchTitle: String = "検索"
    override val editingModeDetailsTitle: String = "詳細"
    override val editingModeRemovalTitle: String = "削除"
    override val vocabDetailsMessage: (inlineIconId: String) -> AnnotatedString = {
        buildAnnotatedString {
            append("下のボタンをクリックしてデッキを保存してください。新しい単語を追加するには、検索画面や、書く練習中、アプリ内の他の場所で ")
            appendInlineContent(it)
            append(" アイコンを使用してください。")
        }
    }
    override val completeMessage: String = "完了"
    override val saveTitle: String = "変更の保存"
    override val saveInputHint: String = "名前"
    override val saveButtonDefault: String = "保存"
    override val saveButtonCompleted: String = "完了"
    override val deleteTitle: String = "削除の確認"
    override val deleteMessage: (practiceTitle: String) -> String = {
        "デッキ「$it」を削除してもよろしいですか？"
    }
    override val deleteButtonDefault: String = "削除"
    override val deleteButtonCompleted: String = "完了"
    override val unknownTitle: String = "不明な文字"
    override val unknownMessage: (characters: List<String>) -> String = {
        "${it.joinToString()} のデータが見つかりませんでした"
    }
    override val unknownButton: String = "OK"

    override val leaveConfirmationTitle: String = "編集をやめますか？"
    override val leaveConfirmationMessage: String = "現在の変化は失われます"
    override val leaveConfirmationCancel: String = "キャンセル"
    override val leaveConfirmationAccept: String = "やめる"

}

object JapaneseDeckDetailsStrings : DeckDetailsStrings {
    override val emptyListMessage: String = "何もありません"
    override val detailsGroupTitle: (index: Int) -> String = { "グループ $it" }
    override val firstTimeReviewMessage: (LocalDateTime?) -> String = {
        "初めて練習した時間: " + when (it) {
            null -> "なし"
            else -> groupDetailsDateTimeFormatter(it)
        }
    }
    override val lastTimeReviewMessage: (LocalDateTime?) -> String = {
        "最後に練習した時間: " + when (it) {
            null -> "なし"
            else -> groupDetailsDateTimeFormatter(it)
        }
    }
    override val groupDetailsButton: String = "練習を開始"
    override val expectedReviewDate: (LocalDate?) -> String = {
        "予定の復習日: ${it ?: "-"}"
    }
    override val lastReviewDate: (LocalDateTime?) -> String = {
        "最後の復習日: ${it?.date ?: "-"}"
    }
    override val repetitions: (Int) -> String = { "連続正解回数: $it" }
    override val lapses: (Int) -> String = { "忘却回数: $it" }

    override val dialogCommon: LetterDeckDetailDialogCommonStrings =
        JapaneseLetterDeckDetailDialogCommonStrings
    override val filterDialog: FilterDialogStrings = JapaneseFilterDialogStrings
    override val sortDialog: SortDialogStrings = JapaneseSortDialogStrings
    override val layoutDialog: PracticePreviewLayoutDialogStrings =
        JapanesePracticePreviewLayoutDialogStrings

    override val multiselectTitle: (selectedCount: Int) -> String = { "$it 個選択中" }
    override val multiselectDataNotLoaded: String = "しばらくお待ちください…"
    override val multiselectNoSelected: String = "少なくとも1つ選んでください"

    override val filterAllLabel: String = "すべて"
    override val filterNoneLabel: String = "何も"
    override val kanaGroupsModeActivatedLabel: String = "仮名グループモード"

    override val shareLetterDeckClipboardMessage: String =
        "デッキからの文字がクリップボードにコピーされました"
}

object JapaneseLetterDeckDetailDialogCommonStrings : LetterDeckDetailDialogCommonStrings {
    override val buttonCancel: String = "キャンセル"
    override val buttonApply: String = "適用"
}

object JapaneseFilterDialogStrings : FilterDialogStrings {
    override val title: String = "表示する文字"
}

object JapaneseSortDialogStrings : SortDialogStrings {
    override val title: String = "順序"
    override val sortOptionAddOrder: String = "追加順"
    override val sortOptionAddOrderHint: String = "↑ 新しい文字が最後\n↓ 新しい文字が最初"
    override val sortOptionFrequency: String = "頻出順"
    override val sortOptionFrequencyHint: String =
        "新聞で使われる頻度\n↑ 頻度が高い文字が最初\n↓ 頻度が高い文字が最後"
    override val sortOptionName: String = "符号順"
    override val sortOptionNameHint: String = "↑ 小さい文字が最初\n↓ 小さい文字が最後"
    override val sortOptionReviewTime: String = "予想復習時間"
    override val sortOptionReviewTimeHint: String =
        "↑ 一度も復習していないカードが最初\n↓ 予定が最も遠いカードが最初"
}

object JapanesePracticePreviewLayoutDialogStrings : PracticePreviewLayoutDialogStrings {
    override val title: String = "レイアウト"
    override val singleCharacterOptionLabel: String = "リスト"
    override val groupsOptionLabel: String = "グループ"
    override val kanaGroupsTitle: String = "仮名グループ"
    override val kanaGroupsSubtitle: String =
        "すべての仮名が含まれている場合、五十音に従ってグループのサイズを設定します"
}

object JapaneseCommonPracticeStrings : CommonPracticeStrings {

    override val configurationTitle: String = "練習の設定"
    override val configurationSelectedItemsLabel: String = "練習の数:"
    override val configurationCharactersPreview: String = "文字のプレビュー"
    override val shuffleConfigurationTitle: String = "順序のシャッフル"
    override val shuffleConfigurationMessage: String = "復習順をランダムにする"
    override val configurationCompleteButton: String = "開始"

    override val additionalKanaReadingsNote: (List<String>) -> String = {
        "注：${it.joinToString { "「$it」" }}と書くこともあります"
    }

    override val formattedSrsInterval: (Duration) -> String = {
        val duration = formattedSrsDuration(
            duration = it,
            dayLabel = "日",
            hourLabel = "時",
            minuteLabel = "分",
            secondLabel = "秒",
            separator = ""
        )
        "${duration}後"
    }
    override val flashcardRevealButton: String = "答えを見る"
    override val againButton: String = "もう一度"
    override val hardButton: String = "難しい"
    override val goodButton: String = "正解"
    override val easyButton: String = "簡単"

    override val summaryTimeSpentLabel: String = "時間"
    override val summaryTimeSpentValue: (Duration) -> String = { formatDuration(it) }
    override val summaryAccuracyLabel: String = "正解率"
    override val summaryItemsCountTitle: String = "練習した項目の数"
    override val summaryNextReviewLabel: String = "次の復習:"
    override val summaryButton: String = "終了"

    override val earlyFinishDialogTitle: String = "練習を終了しますか？"
    override val earlyFinishDialogMessage: String =
        "まとめに移動します。現在の進捗はすでに保存されています"
    override val earlyFinishDialogCancelButton: String = "キャンセル"
    override val earlyFinishDialogAcceptButton: String = "終了"

}

object JapaneseLetterPracticeStrings : LetterPracticeStrings {
    override val configurationTitle: (practiceType: String) -> String = { "文字練習・$it" }
    override val hintStrokesTitle: String = "字画のヒント表示"
    override val hintStrokesMessage: String = "ヒントを表示する条件を設定する"
    override val hintStrokeNewOnlyMode: String = "新規のみ"
    override val hintStrokeAllMode: String = "常時"
    override val hintStrokeNoneMode: String = "しない"
    override val inputModeTitle: String = "入力モード"
    override val inputModeMessage: String = "字画ごとに検証するか、文字全体を検証するかを選択する"
    override val inputModeStroke: String = "字画"
    override val inputModeCharacter: String = "文字"
    override val kanaRomajiTitle: String = "ローマ字を表示"
    override val kanaRomajiMessage: String =
        "かなを練習するときは、かなの代わりにローマ字単語を表示する"
    override val noTranslationLayoutTitle: String = "翻訳の非表示"
    override val noTranslationLayoutMessage: String = "書く練習で字義の翻訳を隠す"
    override val leftHandedModeTitle: String = "左手モード"
    override val leftHandedModeMessage: String = "書く練習で横画面の場合、書く場所を左に移す"

    override val headerWordsMessage: (count: Int) -> String = {
        "単語  " + if (it > LetterPracticeScreenContract.WordsLimit) "(100+)" else "($it)"
    }
    override val studyFinishedButton: String = "復習"
    override val altStrokeEvaluatorTitle: String = "代替字画認識"
    override val altStrokeEvaluatorMessage: String =
        "オリジナルの字画認識の代わりに代替のアルゴリズムを使う"
    override val noKanjiTranslationsLabel: String = "[翻訳なし]"

    override val variantsTitle: String = "異体字"
    override val variantsHint: String = "クリックして表示"
    override val unicodeTitle: (String) -> String = EnglishLetterPracticeStrings.unicodeTitle
    override val strokeCountTitle: (count: Int) -> String = { "${it}画" }
}

object JapaneseVocabPracticeStrings : VocabPracticeStrings {
    override val configurationTitle: (String) -> String = { "単語練習・$it" }
    override val readingPriorityConfigurationTitle: String = "単語の読み方"
    override val readingPriorityConfigurationMessage: String =
        "単語に複数の読みがある場合、使用する読みを選択してください"
    override val readingPriorityConfigurationDefault: String = "辞書の最初"
    override val readingPriorityConfigurationKanji: String = "漢字"
    override val readingPriorityConfigurationKana: String = "かな"
    override val readingMeaningConfigurationTitle: String = "常に意味の表示"
    override val readingMeaningConfigurationMessage: String =
        "回答が選択されていない場合の意味の表示を選択してください"
    override val translationInFrontConfigurationTitle: String = "表に翻訳を置く"
    override val translationInFrontConfigurationMessage: String =
        "フラッシュカードが隠れているときに単語の代わりに翻訳を表示する"
    override val detailsButton: String = "詳細"
}

object JapaneseKanjiInfoStrings : KanjiInfoStrings {
    override val strokesMessage: (count: Int) -> AnnotatedString = {
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("${it}画") }
        }
    }
    override val clipboardCopyMessage: String = "コピーしました"
    override val radicalsSectionTitle: (count: Int) -> String = { "部首 ($it)" }
    override val noRadicalsMessage: String = "部首なし"
    override val wordsSectionTitle: (count: Int) -> String = { "単語 ($it)" }
    override val romajiMessage: (romaji: List<String>) -> String = {
        "ローマ字: ${it.joinToString { "「$it」" }}"
    }
    override val gradeMessage: (grade: Int) -> String = {
        when {
            it <= 6 -> "常用漢字，小学校${it}年で学習"
            it == 8 -> "常用漢字，中学校以降で学習"
            it >= 9 -> "人名用漢字"
            else -> throw IllegalStateException("Unknown grade $it")
        }
    }
    override val jlptMessage: (level: Int) -> String = { "JLPT レベル$it" }
    override val frequencyMessage: (frequency: Int) -> String = {
        "新聞頻出漢字の2500中${it}番目"
    }
    override val noDataMessage: String = "データなし"

}

object JapaneseReminderNotificationStrings : ReminderNotificationStrings {
    override val channelName: String = "リマインダー通知"
    override val title: String = "勉強の時間です！"
    override val noDetailsMessage: String = "日本語の学習を続ける"
    override val newOnlyMessage: (Int) -> String = {
        "今日は未習うカードが${it}枚あります"
    }
    override val dueOnlyMessage: (Int) -> String = {
        "今日は復習するカードが${it}枚あります"
    }
    override val message: (Int, Int) -> String = { new, due ->
        "今日は未習うカードが${new}枚、復習するカードが${due}枚あります"
    }
}
