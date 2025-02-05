package ui.edit

import com.formdev.flatlaf.FlatLightLaf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import state.getSettingsDirectory
import java.io.File
import javax.swing.JOptionPane

@ExperimentalSerializationApi
@Serializable
data class SearchData(
    val matchCaseIsSelected: Boolean = false,
    val wordsIsSelected: Boolean = false,
    val regexIsSelected: Boolean = false,
    val numberSelected: Boolean = false,
)

@OptIn(ExperimentalSerializationApi::class)
class SearchState (searchState: SearchData){
    var matchCaseIsSelected = searchState.matchCaseIsSelected
    var wordsIsSelected = searchState.wordsIsSelected
    var regexIsSelected = searchState.regexIsSelected
    var numberSelected = searchState.numberSelected

    /**  */
    fun saveSearchState() {
        val encodeBuilder = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        runBlocking {
            launch {
                val searchState = SearchData(
                    matchCaseIsSelected,
                    wordsIsSelected,
                    regexIsSelected,
                    numberSelected
                )
                val json = encodeBuilder.encodeToString(searchState)
                val searchStateFile = getSearchDataFile()
                searchStateFile.writeText(json)
            }
        }
    }
}

/** 加载编辑词库界面的设置信息 */
@OptIn(ExperimentalSerializationApi::class)
fun loadSearchState():SearchState{
    val cellVisibleSetting = getSearchDataFile()
    return if(cellVisibleSetting.exists()){
        try{
            val decodeFormat = Json { ignoreUnknownKeys = true }
            val searchData = decodeFormat.decodeFromString<SearchData>(cellVisibleSetting.readText())
            SearchState(searchData)
        }catch (exception:Exception){
            FlatLightLaf.setup()
            JOptionPane.showMessageDialog(null, "设置信息解析错误，将使用默认设置。\n地址：$cellVisibleSetting")
            SearchState(SearchData())
        }

    }else{
        SearchState(SearchData())
    }
}

/**   */
private fun getSearchDataFile(): File {
    val settingsDir = getSettingsDirectory()
    return File(settingsDir, "SearchState.json")
}