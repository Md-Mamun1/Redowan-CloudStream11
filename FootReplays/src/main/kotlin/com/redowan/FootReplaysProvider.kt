package com.redowan

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class FootReplays : MainAPI() {
    override var mainUrl = "http://172.27.27.84"
    override var name = "My Local Server"
    override val hasMainPage = true
    override var lang = "bn"
    override val hasDownloadSupport = true
    
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val document = app.get(mainUrl).document
        val items = document.select("a[href]").mapNotNull { element ->
            val href = element.attr("href")
            val title = element.text()
            
            if (title.contains("Parent Directory") || href.endsWith("/")) {
                null
            } else {
                newMovieSearchResponse(title, fixUrl(href), TvType.Movie) {
                    this.posterUrl = ""
                }
            }
        }
        // এখানে লিস্ট আকারে পাঠাতে হবে
        return newHomePageResponse(listOf(HomePageList(name, items)), false)
    }

    override suspend fun load(url: String): LoadResponse? {
        val title = url.substringAfterLast("/").replace("%20", " ")
        return newMovieLoadResponse(title, url, TvType.Movie, url)
    }

    override suspend fun loadLinks(
        data: String, 
        isCasting: Boolean, 
        subtitleCallback: (SubtitleFile) -> Unit, 
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback.invoke(
            ExtractorLink(
                source = this.name, 
                name = this.name, 
                url = data, 
                referer = mainUrl, 
                quality = Qualities.Unknown.value
            )
        )
        return true
    }
}
