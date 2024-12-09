package livewallpaper.aod.screenlock.unlimited_wallpaper

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CategoriesResponseAdapter : JsonDeserializer<CategoriesResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): CategoriesResponse {
        val jsonObject = json.asJsonObject
        val categories = context.deserialize<MutableList<Category>>(
            jsonObject.get("categories"),
            object : TypeToken<List<Category>>() {}.type
        )
        return CategoriesResponse(categories)
    }
}
