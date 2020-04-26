package com.rtuitlab.studo.utils

import android.app.Application
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.edit.EditFactory
import com.yydcdut.markdown.syntax.text.TextFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val markdownModule = module {
    single(named("text")) { provideMarkdownTextProcessor(androidApplication()) }
    single(named("edit")) { provideMarkdownEditProcessor(androidApplication()) }
}

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