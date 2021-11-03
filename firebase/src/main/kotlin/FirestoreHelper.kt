import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.Debt
import model.JsJSON
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.json

class FirestoreHelper(
        val useStubs : Boolean = false,//workaround for swapping inline methods for stubs
        val addDataStub : suspend (JsJSON, String) -> Unit = {_,_ ->},
        val findStub : suspend (String, WhereClause) -> String = {_,_ -> ""},
        val findAllStub : suspend (String) -> String = {_ -> ""},
        val removeStub : (String, String) -> Unit = {_,_ -> }
){
    suspend fun addData(obj : JsJSON, collection : String) =
            if(useStubs) addDataStub(obj,collection)
            else suspendCoroutine {
                val doc = firestore.collection(collection).doc()
                firestore.collection(collection)
                        .doc(doc.id)
                        .set(obj.addId(doc.id).jsonObject, json("merge" to false)).then { ref ->
                            it.resume(Unit)
                        }
            }

    suspend inline fun <reified T : JsJSON> find(collection: String, where : WhereClause) =
            if(useStubs) Json.decodeFromString(findStub(collection, where))
            else suspendCoroutine<List<T>> {
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
            if (useStubs) Json.decodeFromString(findAllStub(collection))
            else suspendCoroutine<List<T>> {
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
        if(useStubs) removeStub(collection, id)
        else firestore.collection(collection)
                .doc(id)
                .delete()
    }
}

data class WhereClause(val field : String, val operator : String, val value : Any)