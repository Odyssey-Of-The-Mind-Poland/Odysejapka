package odyseja.odysejapka.drive

import com.google.api.services.sheets.v4.model.Sheet

interface SheetAdapter {
    fun getSheet(sheetsId: String): MutableList<Sheet>?
    fun getValue(sheetId: String, sheetName: String, range: String): MutableList<MutableList<String>>
    fun writeValue(
        sheetId: String,
        sheetName: String,
        range: String,
        value: String,
        inputType: String = "USER_ENTERED"
    )
    fun writeValues(
        sheetId: String,
        sheetName: String,
        range: String,
        writeValues: List<String>,
        inputType: String = "USER_ENTERED"
    )
} 