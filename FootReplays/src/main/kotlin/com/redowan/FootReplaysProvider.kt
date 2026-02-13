package com.redowan

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class MyLocalServer : MainAPI() {
    override var mainUrl = "http://172.27.27.84"
    override var name = "My Local Server"
    override val hasMainPage = true
    override var lang = "bn"
    override val hasDownloadSupport = true
    
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        // মেইন পেজ থেকে ফাইল লিস্ট আনা
        val document = app.get(mainUrl).document
        val items = document.select("a[href]").mapNotNull { element ->
            val href = element.attr("href")
            val title = element.text()
            if(title.contains("Parent Directory") || href.endsWith("/")) null
            else newMovieSearchResponse(title, "$mainUrl/$href", TvType.Movie) {
                this.posterUrl = "" 
            }
        }
        return newHomePageResponse(HomePageResponse(items), false)
    }

    override suspend fun load(url: String): LoadResponse? {
        val title = url.substringAfterLast("/").replace("%20", " ")
        return newMovieLoadResponse(title, url, TvType.Movie, url)
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        callback.invoke(
            ExtractorLink(source = name, name = name, url = data, referer = mainUrl, quality = 0)
        )
        return true
    }
}
