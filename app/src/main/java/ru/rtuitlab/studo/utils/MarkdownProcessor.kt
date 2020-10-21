package ru.rtuitlab.studo.utils

import android.app.Application
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.edit.EditFactory
import com.yydcdut.markdown.syntax.text.TextFactory

fun provideMarkdownTextProcessor(app: Application): MarkdownProcessor {
    return MarkdownProcessor(app).apply {
        factory(TextFactory.create())
    }
}

fun provideMarkdownEditProcessor(app: Application): MarkdownProcessor {
    return MarkdownProcessor(app).apply {
        factory(EditFactory.create())
    }
}