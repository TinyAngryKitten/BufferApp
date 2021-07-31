import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.Debt
import model.JsJSON
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.json

object firestoreHelper {
    suspend fun addData(obj : JsJSON, collection : String) =
        suspendCoroutine<Unit> {
            val doc = firestore.collection(collection).doc()
            firestore.collection(collection)
                .doc(doc.id)
                .set(obj.addId(doc.id).jsonObject, json("merge" to false)).then { ref ->
                    it.resume(Unit)
                }
        }

    suspend inline fun <reified T : JsJSON> find(collection: String, where : WhereClause) =
        suspendCoroutine<List<T>> {
            firestore.collection(collection).where(where.field,where.operator,where.value)
                .get().then { querySnapshot ->
                    val list = mutableListOf<T>()
                    querySnapshot.forEach { doc ->
                        try {
                            val stringData = JSON.stringify(doc.data())
                            if(stringData != null && !stringData.isEmpty() && stringData != "undefined") {
                                list.add(Json.decodeFromString(stringData))
                            }
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                    }
                    it.resume(list)
                }
        }
    suspend inline fun <reified T : JsJSON> findAll(collection: String) =
        suspendCoroutine<List<T>> {
        firestore.collection(collection)
            .get().then { querySnapshot ->
                val list = mutableListOf<T>()
                querySnapshot.forEach { doc ->
                    try {
                        val stringData = JSON.stringify(doc.data())
                        list.add(Json.decodeFromString(stringData))
                    } catch (e : Exception) {
                        e.printStackTrace()
                        println(e.message)
                    }
                }
                it.resume(list)
            }
        }

    fun remove(collection: String, id : String) {
        firestore.collection(collection)
            .doc(id)
            .delete()
    }
}

data class WhereClause(val field : String, val operator : String, val value : Any)