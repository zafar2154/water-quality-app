import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class SensorResponse(val ph: Float?, val tds: Float?, val temperature: Float?)

object ApiClient {
    fun create(ipAddress: String): ApiService {
        val baseUrl = "http://$ipAddress/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
interface ApiService {
    @GET("sensor")
    suspend fun getUser(): SensorResponse
}